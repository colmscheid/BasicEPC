package converter;

import java.io.IOException;
import java.io.BufferedReader;

public class Lexer {

	private BufferedReader reader;
	private String line_text;
	private int line_number = 0;
	
	public Lexer(BufferedReader reader) {
		
		this.reader = reader;
		readLine();
	}

	/* Reads the next line from the reader */
	private void readLine() {
		
		try {
			do {
				line_text = reader.readLine();
				ignoreComments();
				line_number ++;
				if (line_text == null)
					break;
				line_text = line_text.trim();
				if (line_text == "")
					break;
			} while (line_text.equals(""));
			
		} catch (IOException e) {
			
		}
	}
	
	/* Ignore comments */
	public void ignoreComments() {
		
		if (line_text != null) {
			int number = line_text.indexOf("--");
			if (number != -1)
				line_text =  line_text.substring(0, number);
		}
	}
	
	/* Get the next token */
	public Token nextToken() throws ConverterException {
		
		Token token = null;
		
		/* No more lines */
		if (line_text == null) 
			return null;//token = new Token(TokenType.END, line_number);
		
		/* Line represents an event (line has to begin with an "E"):
		 * Have to check whether there is an event description after the "E" */
		else if (line_text.startsWith("E ")) {
			line_text = line_text.substring(1);
			line_text = line_text.trim();
			if (line_text.equals(""))
				throw ConverterException.missingEventDescription(line_number);
			token = new Token(TokenType.EVENT, line_text, line_number);
		
		/* Line represents an function (line has to begin with an "F"):
		 * Have to check whether there is an function description after the "F" */
		} else if (line_text.startsWith("F ")) {
			line_text = line_text.substring(1);
			line_text = line_text.trim();
			if (line_text.equals(""))
				throw ConverterException.missingFunctionDescription(line_number);
			token = new Token(TokenType.FUNCTION, line_text, line_number);
		
		/* Line represents an jump */
		} else if (line_text.startsWith("SPRINGE ZU")) {
			line_text = line_text.substring(11);
			try {
				token = nextToken();
			} catch (ConverterException excpetion) {
				throw ConverterException.exceptedAfterJump(line_number);
			}
			if (!(token.getType().equals(TokenType.EVENT) || token.getType().equals(TokenType.FUNCTION)))
					throw ConverterException.exceptedAfterJump(line_number);
			token.setType(TokenType.JUMP);
			return token;
			
		/* Line represents a reserved word */	
		} else if (line_text.equals("STARTPROZESSE") ||
				   line_text.equals("STARTPROZESSE EXKLUSIV") ||
				   line_text.equals("STARTEREIGNISSE") ||
				   line_text.equals("STARTEREIGNISSE EXKLUSIV")) {
			token = new Token(TokenType.ALTERNATES, line_number);
			
		} else if (line_text.equals("STARTPROZESSE PARALELL") ||
				   line_text.equals("STARTEREIGNISSE PARALELL")) {
			token = new Token(TokenType.PARALELL, line_number);
			
		} else if (line_text.equals("STARTPROZESSE OPTIONAL") ||
				   line_text.equals("STARTEREIGNISSE OPTIONAL")) {
			token = new Token(TokenType.OPTIONS, line_number);
			
		} else if (line_text.equals("STARTPROZESSE ENDE") ||
				   line_text.equals("STARTEREIGNISSE ENDE")) {
			token = new Token(TokenType.STARTEND, line_number);
			
		} else if (line_text.equals("ENDPROZESSE") ||
				   line_text.equals("ENDPROZESSE EXKLUSIV") ||
				   line_text.equals("ENDEREIGNISSE") ||
				   line_text.equals("ENDEREIGNISSE EXKLUSIV")) {
			token = new Token(TokenType.ALTERNATES, line_number);
			
		} else if (line_text.equals("ENDPROZESSE PARALELL") ||
				   line_text.equals("ENDEREIGNISSE PARALELL")) {
			token = new Token(TokenType.PARALELL, line_number);
			
		} else if (line_text.equals("ENDPROZESSE OPTIONAL") ||
				   line_text.equals("ENDEREIGNISSE OPTIONAL")) {
			token = new Token(TokenType.OPTIONS, line_number);
			
		} else if (line_text.equals("ENDPROZESSE ENDE") ||
				   line_text.equals("ENDEREIGNISSE ENDE")) {
			token = new Token(TokenType.ENDEND, line_number);
			
		} else if (line_text.equals("PARALELLISIERE")) {
			token = new Token(TokenType.PARALELL, line_number);
			
		} else if (line_text.equals("SYNCHRONISIERE")) {
			token = new Token(TokenType.SYNCHRONISE, line_number);
			
		} else if (line_text.equals("ALTERNATIVEN")) {
			token = new Token(TokenType.ALTERNATES, line_number);
			
		} else if (line_text.equals("ALTERNATIVEN ENDE")) {
			token = new Token(TokenType.ALTERNATESEND, line_number);
			
		} else if (line_text.equals("OPTIONEN")) {
			token = new Token(TokenType.OPTIONS, line_number);
			
		} else if (line_text.equals("OPTIONEN ENDE")) {
			token = new Token(TokenType.OPTIONSEND, line_number);
			
		}else if (line_text.equals("PROZESSENDE")) {
			token = new Token(TokenType.END, line_number);
			
		} else if (line_text.equals("ODER")) {
			token = new Token(TokenType.XOR, line_number);
			
		} else if (line_text.equals("UND")) {
			token = new Token(TokenType.AND, line_number);
		
		} else if (line_text.equals("NICHTS")) {
			token = new Token(TokenType.NOTHING, line_number);
		
		} else {
			token = new Token(TokenType.UNDEFINED, line_number);
		}
		
		readLine();
		return token;
	}
}
