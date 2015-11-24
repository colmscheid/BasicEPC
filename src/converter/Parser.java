package converter;

import java.util.ArrayList;
import java.util.Stack;

import model.EPC_Element;
import model.End;
import model.Event;
import model.Function;
import model.Connector;
import model.ConnectorType;
import model.Jump;
import model.Nothing;
import model.Warning;
import model.WarningType;

public class Parser {

	private Lexer lexer;
	
	/* The last token given from the lexer */
	private Token token;
	
	/* The start element */
	private EPC_Element start;
	
	/* Add type to stack when process is in a gateway */
	private Stack<ConnectorType> stack = new Stack<ConnectorType>();
	
	/* The current id refers to the current element */
	private int id = 0;
	
	/* Warnings generated while parsing */
	ArrayList<Warning> warnings = new ArrayList<Warning>();
	
	public Parser(Lexer lexer) {
		
		this.lexer = lexer;
	}
	
	public ArrayList<Warning> getWarnings() {
		
		return warnings;
	}
	
	/* Transforms the stream of tokens in a linked list of EPC elements */
	public EPC_Element parse() throws ConverterException {
		
		token = lexer.nextToken();
		
		/* The start events */
		//start = startEvents();
		
		/* The process following the start events */
		start = nextSubProcess();

		/* Links the jumps */
		replaceJump(start);
		
		return start;
	}
	
	/* Parses a subprocess of the EPC.
	 * A subprocess may be part of a gateway or be the whole process given by the user. */
	private EPC_Element nextSubProcess() throws ConverterException {
		
		/* The first element of the subprocess.
		 * Skip all no EPC Element. */
		EPC_Element start;
		do {
			try {
				start = nextElement();
				break;
			} catch (ConverterException e) {
				warnings.add(new Warning(token.getLine(), WarningType.NoEPCElement));
				token = lexer.nextToken();
			}
		} while (true);
		
		/* If start is an empty element */
		if (start instanceof Nothing) {
			if (stack.isEmpty()) {
				warnings.add(new Warning(token.getLine(), WarningType.NothingNotInGateway));
				token = lexer.nextToken();
				return nextSubProcess();
			} else {
				token = lexer.nextToken();
				return start;
			}
		}
		
		/* last_element is the predecessor of element. 
		 * Variables are needed to build the linked list. */
		EPC_Element last_element;
		EPC_Element element = start;
		
		/* When an END ends the subprocess */
		boolean end = false;

		/* The loop breaks when the following token doesn't represent an event, function or gateway.
		 * For example: XOR, AND, END, SYNCHRONISE, ALTERNATESEND
		 * These tokens represent the ending of a subprocess. */
		do {
			
			/* The next token given by the lexer. We remember the last one. */
			last_element = element;
			try {
				token = lexer.nextToken();
				element = nextElement();
				
				/* Skips Nothing */
				while (element != null && element instanceof Nothing) {
					warnings.add(new Warning(token.getLine(), WarningType.NothingNotAlone));
					element = nextElement();
				}
				
				/* No more elements */
				if (element == null) {
					end = true;
				} 
				
				/* The subprocess ended before through an END */
				if (end) {
					
					/* No more elements */
					if (element == null)
						break;

					/* Warning: There can't be another element after the process end. 
					 * Ignore the elements. */
					warnings.add(new Warning(token.getLine(), WarningType.ElementsAfterEnd));
				}
				
				/* Token END */
				if (element instanceof End) {
					end = true;
				}
			}
			
			/* The last token was no EPC Element. Now we check whether the token ends
			 * the subprocess or there is an error. */
			catch (ConverterException e) {
				
				/* There must be a epc element */
				if (stack.isEmpty()) {
					warnings.add(new Warning(token.getLine(), WarningType.NoEPCElement));
					continue;
				}
				
				/* We are in an AND Gateway. So tokens AND or SYNCHRONISE can ends the subprocess. */
				if (stack.peek().equals(ConnectorType.AND)) {
					if (token.getType().equals(TokenType.AND)) {
						token = lexer.nextToken(); 
						break;
					}
					if (token.getType().equals(TokenType.SYNCHRONISE) ||
						token.getType().equals(TokenType.STARTEND) ||
						token.getType().equals(TokenType.ENDEND))
						break;
					else if (token.getType().equals(TokenType.ALTERNATESEND) ||
							 token.getType().equals(TokenType.OPTIONSEND)) {
						warnings.add(new Warning(token.getLine(), WarningType.ExceptedSynchronise));
						break;
					} else if (token.getType().equals(TokenType.XOR)) {
						warnings.add(new Warning(token.getLine(), WarningType.ExceptedAnd));
						token = lexer.nextToken();
						break;
					} else {
						warnings.add(new Warning(token.getLine(), WarningType.NoElement));
						continue;
					}
				}
				
				/* We are in an XOR Gateway. So tokens XOR, ALTERNATESEND can ends the subprocess. */
				if (stack.peek().equals(ConnectorType.XOR)) {
					if (token.getType().equals(TokenType.XOR)) {
						token = lexer.nextToken(); 
						break;
					}
					if (token.getType().equals(TokenType.ALTERNATESEND) ||
						token.getType().equals(TokenType.STARTEND) ||
						token.getType().equals(TokenType.ENDEND))
						break;
					else if (token.getType().equals(TokenType.SYNCHRONISE) ||
							 token.getType().equals(TokenType.OPTIONSEND)) {
						warnings.add(new Warning(token.getLine(), WarningType.ExceptedAlternatesEnd));
						break;
					} else if (token.getType().equals(TokenType.AND)) {
						warnings.add(new Warning(token.getLine(), WarningType.ExceptedXor));
						token = lexer.nextToken();
						break;
					} else {
						warnings.add(new Warning(token.getLine(), WarningType.NoElement));
						continue;
					}
				}
				
				/* We are in an OR Gateway. So tokens XOR, OPTIONSEND can ends the subprocess. */
				if (stack.peek().equals(ConnectorType.OR)) {
					if (token.getType().equals(TokenType.XOR)) {
						token = lexer.nextToken(); 
						break;
					}
					if (token.getType().equals(TokenType.OPTIONSEND) ||
						token.getType().equals(TokenType.STARTEND) ||
						token.getType().equals(TokenType.ENDEND))
						break;
					else if (token.getType().equals(TokenType.ALTERNATESEND) ||
							 token.getType().equals(TokenType.SYNCHRONISE)) {
						warnings.add(new Warning(token.getLine(), WarningType.ExceptedAlternatesEnd));
						break;
					} else if (token.getType().equals(TokenType.AND)) {
						warnings.add(new Warning(token.getLine(), WarningType.ExceptedXor));
						token = lexer.nextToken();
						break;
					} else {
						warnings.add(new Warning(token.getLine(), WarningType.NoElement));
						continue;
					}
				}
			}
			
			last_element.setNext(element);
			
		} while (true);
		
		return start;
	}
	
	/* Parses the next element of the EPC (event or function or gateway) */
	private EPC_Element nextElement() throws ConverterException {
		
		if (token == null)
			return null;
		
		if (token.getType().equals(TokenType.EVENT)) {
			id++; return new Event(token.getText(), id);
			 
		} else if (token.getType().equals(TokenType.FUNCTION)) {
			id++; return new Function(token.getText(), id);
		
		} else if (token.getType().equals(TokenType.JUMP)) {
			return new Jump(token);
			 
		} else if (token.getType().equals(TokenType.END)) {
			return new End();
			
		} else if (token.getType().equals(TokenType.NOTHING)) {
			return new Nothing();
			
		} else if (token.getType().equals(TokenType.PARALELL)) {
			token = lexer.nextToken();
			stack.push(ConnectorType.AND);
			id++; return nextGateway(TokenType.SYNCHRONISE);
			
		} else if (token.getType().equals(TokenType.ALTERNATES)) {
			token = lexer.nextToken();
			stack.push(ConnectorType.XOR);
			id++; return nextGateway(TokenType.ALTERNATESEND);
			
		} else if (token.getType().equals(TokenType.OPTIONS)) {
			token = lexer.nextToken();
			stack.push(ConnectorType.OR);
			id++; return nextGateway(TokenType.OPTIONSEND);
			
		} 

		/* The last token doesn't represent an EPC element */
		throw ConverterException.exceptedEPCElement(token.getLine());
	}
	
	/* Parses a gateway */
	private Connector nextGateway(TokenType endType) throws ConverterException {
		
		int gateway_id = id;
		ArrayList<EPC_Element> list = new ArrayList<EPC_Element>();
		
		/* Parse all subprocces of the gateway and add it to a list until
		 * the token for ending the gateway is reached (SYNCHRONISE, PARRALELL, STARTEND) */
		while (! token.getType().isGatewayEnd()) {
			while (token.getType().equals(TokenType.XOR) || token.getType().equals(TokenType.AND)) {
				token = lexer.nextToken();
			}
			list.add(nextSubProcess());
			
			if (token == null) {
				warnings.add(new Warning(0, WarningType.MissingSynchronise));
				break;
			}
		}
	
		EPC_Element[] array = new EPC_Element[list.size()];
		array = list.toArray(array);
	
		stack.pop();
		id++;

		if (endType.equals(TokenType.SYNCHRONISE))
			return new Connector(ConnectorType.AND, array, gateway_id);
		else if (endType.equals(TokenType.ALTERNATESEND))
			return new Connector(ConnectorType.XOR, array, gateway_id);
		return new Connector(ConnectorType.OR, array, gateway_id);
	}
	
	/* Replaces the name with the actual EPC_Element a jump shows to */
	public void replaceJump(EPC_Element element) throws ConverterException {
		
		/* The element is a jump */
		if (element instanceof Jump) {

			Jump jump = (Jump) element;

			/* Look for the element before the element the jump links to */
			EPC_Element before = null;
			boolean begin = false;
			if (start.getName().equals(jump.getNextName().getText()))
				begin = true;
			else 
				before = start.lookForElementSuccesor(jump.getNextName().getText());

			/* Exception because no element found to jump to */
			if (before == null && !begin)
				throw ConverterException.afterJumpNotFound(jump.getNextName().getLine());
			
			/* Which element of the gateways child is the element we jump to */
			if (begin) {
				id++;
				Connector gateway = new Connector(ConnectorType.XOR, id);
				gateway.setNext(start);
				jump.setNext(gateway);
				start = gateway;
				
			} else if (before instanceof Connector) {
				Connector before_gateway = (Connector) before;
				int i = 0;
				for (EPC_Element child : before_gateway.getSubprocesses()) {
					if (child.getName().equals(jump.getNextName().getText())) 
						break;
					i++;
				}
				if (! before_gateway.getType().equals(ConnectorType.AND)) {
					jump.setNext(before_gateway);
					jump.setEndConnector(true);
				} else {
					id++;
					Connector gateway = new Connector(ConnectorType.XOR, id);
					if (i == before_gateway.getSubprocesses().length) {
						gateway.setNext(before.getNext());
						before.setNext(gateway);
					} else {
						gateway.setNext(before_gateway.getSubprocess(i));
						before_gateway.setSubprocess(i, gateway);
					}
					jump.setNext(gateway);
				}
				
			/* Element before is an event or a function */
			} else {
				id++;
				Connector gateway = new Connector(ConnectorType.XOR, id);
				gateway.setNext(before.getNext());
				before.setNext(gateway);
				jump.setNext(gateway);
			}
			
		/* Replace jumps in the following elements */
		} else {
			if (element instanceof Connector) {
				if (((Connector) element).getSubprocesses() != null)
					for (EPC_Element child : ((Connector) element).getSubprocesses())
						replaceJump(child);
				replaceJump(element.getNext());
			} else if (element instanceof Event || element instanceof Function) {
				replaceJump(element.getNext());
			}
		}
	}
}