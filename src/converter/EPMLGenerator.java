package converter;

import java.io.IOException;
import java.io.Writer;

import model.EPC_Element;
import model.End;
import model.Event;
import model.Function;
import model.Connector;
import model.ConnectorType;
import model.Jump;
import model.Margin;
import model.Nothing;

public class EPMLGenerator {
	
	private Writer writer;
	private int max_width;
	private int last_id = 0;
	private int arc_id = 0;
	
	/* Was the last element a end or a jump */
	private boolean isEnd = false;
	
	/* Generate an EPML File based on the given EPC */
	public void generateEPMLFile(EPC_Element start, String name, Writer writer) throws IOException {
		
		/* Create a empty EPML File */
		/*if (name.endsWith(".txt"))
			name = name.substring(0, name.length()-4);
		if (name.endsWith(".bepc"))
			name = name.substring(0, name.length()-9);
		if (! name.endsWith(".epml"))
			name += ".epml";
		
		File file = new File(name);
		file.createNewFile();
		
		/* Start writing the opening of the file */
		this.writer = writer;
		
		writeNewLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		writeNewLine("<epml:epml xmlns:epml =\"http://www.epml.de\">");
		writer.append(System.getProperty("line.separator"));
		intend(1);
		writeNewLine("<epc epcId=\"1\" name=\"" + name + "\">");
		writer.append(System.getProperty("line.separator"));
		
		/* Any elements in the epc? */
		if (start != null) {
		
			/* The maximum number of parallel processes
			 * and the margin to the left and right side */
			max_width = start.getMaximumWidth();
			int margin = (max_width-1);
			
			/* Write the start element in the file */
			nextElement(1, new Margin(margin, margin), start, true, true);
		}
			
		/* Write the end in the file */
		intend(1);
		writeNewLine("</epc>");
		writer.append(System.getProperty("line.separator"));
		writeNewLine("</epml:epml>");
		writer.close();
	}
	
	/* Writes a string in a line of the epc */
	private void writeNewLine(String string) throws IOException {
		
		string = string.replace("Ä", "Ã„");
		string = string.replace("Ö", "Ã–");
		string = string.replace("Ü", "Ãœ");
		string = string.replace("ä", "Ã¤");
		string = string.replace("ö", "Ã¶");
		string = string.replace("ü", "Ã¼");
		string = string.replace("ß", "ÃŸ");
		
		writer.write(string);
		writer.append(System.getProperty("line.separator"));
	}
	
	/* Add a number of indents to the current line of the file */
	private void intend(int number) throws IOException {
		
		while (number != 0) {
			writer.write("  ");
			number--;
		}
	}
	
	/* Add an epc element to the file */
	public void nextElement(int line, Margin margin, EPC_Element element, boolean overProcessStart, boolean overProcessEnd) throws IOException {
		
		/* Center */
		while (margin.getCompleteMargin() + 2 != max_width * 2) {
			margin = margin.addMargin(1, 1);
		}

		isEnd = false;
		
		if (element == null)
			return;
		if (element instanceof End) {
			isEnd = true;
			return;
		} if (element instanceof Connector) {
			nextElement(line, margin, (Connector) element, overProcessStart, overProcessEnd);
		} else if (element instanceof Event)
			nextElement(line, margin, (Event) element, overProcessStart, overProcessEnd);
		else if (element instanceof Function)
			nextElement(line, margin, (Function) element, overProcessStart, overProcessEnd);	
		else if (element instanceof Jump) {
			isEnd = true;
			nextElement(line, margin, (Jump) element, overProcessStart, overProcessEnd);
		}
	}
	
	/* Add a gateway to the file */
	public void nextElement(int line, Margin margin, Connector gateway, boolean overProcessStart, boolean overProcessEnd) throws IOException {
		
		boolean this_end = false;
		if ((gateway.getNext() == null || (gateway.getNext() instanceof End)) && overProcessEnd) 
			this_end = true;

		/* Center */
		while (margin.getCompleteMargin() + 2 != max_width * 2) {
			margin = margin.addMargin(1, 1);
		}
		
		/* The type of the gateway */
		String type;
		if (gateway.getType().equals(ConnectorType.AND))
			type = "and";
		else if (gateway.getType().equals(ConnectorType.OR))
			type = "or";
		else
			type = "xor";

		/* Add a connector and a arc from the last element to the connector 
		 * to the file if it is no start gateway */
		int gateway_id = gateway.getId();
		if (! overProcessStart) {
			addConnector(line, margin, type, gateway_id);
			addArc(last_id, gateway_id);
			line++;
		}
		
		/* The connector joins a jump and the last element */
		if (gateway.getSubprocesses() == null) {
			last_id = gateway_id;
			
			/* Add the next element */
			nextElement(line, margin, gateway.getNext(), false, overProcessEnd);
			
			return;
		}
		
		/* The margin for the children of the gateway */
		int margin_left = margin.getLeft() - (gateway.getWidth() - 1);
		int margin_right = 2 * max_width - margin_left;
		
		/* The last ids of the sub processes */
		int[] ids = new int[gateway.getSubprocesses().length];
		
		/* Add the children of the gateway to the file */
		for (int i = 0; i < gateway.getSubprocesses().length; i++) {
		
			EPC_Element element = gateway.getSubprocesses()[i];
			margin_right -= element.getWidth() * 2;
			
			if (element instanceof Nothing) {
				ids[i] = gateway_id;
			} else if (element instanceof Jump) {
				last_id = gateway_id;
				nextElement(line, null, (Jump) element, false, this_end);
				if (i>0)
					last_id = ids[i-1];
				//addArc(gateway_id, element.getNext().getId());
			} else {
				last_id = gateway_id;
				nextElement(line, new Margin(margin_left, margin_right),
						element, overProcessStart, this_end);
				if (isEnd)
					ids[i] = 0;
				else
					ids[i] = last_id;
			}
			margin_left += element.getWidth() * 2;
		}
		
		/* Add a connector to join the subprocesses if there is a next element */
		if (! this_end) {

			/* The arc of the last elements of the subprocesses to the join connector */
			int count = 0;
			int element_id = 0;
			gateway_id = /*gateway.getNext().getId() - 1*/last_id + 1;
			for (int number : ids) {
				if (number != 0) {
					count++;
					element_id = number;
					addArc(number, gateway_id);
				}
			}

			/* Add the connector to join the subprocesses */
			line = line + gateway.getLength() - 3;
			if (count > 1) {
				line++;
				addConnector(line, margin, type, gateway_id);
				last_id = gateway_id;
			} else {
				last_id = element_id;
			}
			
			line ++;
			
			if ((gateway.getNext() != null && (! (gateway.getNext() instanceof End)))) {
				
				/* Add the next element */
				nextElement(line, margin, gateway.getNext(), false, overProcessEnd);
			}
		}
	}
	
	/* Add a gateway to the event */
	public void nextElement(int line, Margin margin, Event event, boolean overProcessStart, boolean overProcessEnd) throws IOException {
		
		int id = event.getId();
		addElement(line, margin, event, id);
		addArc(last_id, id);
		last_id = id; 
		
		nextElement(line+1, margin, event.getNext(), false, overProcessEnd);
	}
	
	/* Add a gateway to the function */
	public void nextElement(int line, Margin margin, Function function, boolean overProcessStart, boolean overProcessEnd) throws IOException {
		
		int id = function.getId();
		addElement(line, margin, function, id);
		addArc(last_id, id);
		last_id = id; 
		
		nextElement(line+1, margin, function.getNext(), false, overProcessEnd);
	}
	
	/* Add a jump to the function */
	public void nextElement(int line, Margin margin, Jump jump, boolean overProcessStart, boolean overProcessEnd) throws IOException {
		
		if (jump.getEndConnector())
			addArc(last_id, jump.getNext().getNext().getId() - 1);
		else
			addArc(last_id, jump.getNext().getId());
	}
	
	/* Write an event or a function in the file */
	public void addElement(int line, Margin margin, EPC_Element element, int id) throws IOException {
		
		String type;
		if (element instanceof Event)
			type = "event";
		else
			type = "function";
		
		intend(2);
		writeNewLine("<" + type + " id=\"" + id + "\">");
		setEventAndFunctionCoordinates(line, margin);
		intend(3);
		writeNewLine("<name>" + element + "</name>");
		intend(2);
		writeNewLine("</" + type + ">");
		writer.append(System.getProperty("line.separator"));
	}
	
	/* Write an arc in the file */
	public void addArc(int source, int target) throws IOException {
		
		if (source == 0) 
			return;
			
		intend(2);
		writeNewLine("<arc id=\"" + arc_id + "\">");
		intend(3);
		writeNewLine("<flow source=\"" + source 
				   + "\" target=\"" + target + "\"/>");
		intend(2);
		writeNewLine("</arc>");
		writer.append(System.getProperty("line.separator"));
		
		arc_id++;
	}
	
	/* Write a connector in the file */
	public void addConnector(int line, Margin margin, String type, int id) throws IOException {
		
		intend(2);
		writeNewLine("<" + type + " id=\"" + id + "\">");
		setConnectorCoordinates(line, margin);
		intend(2);
		writeNewLine("</" + type + ">");
		writer.append(System.getProperty("line.separator"));
	}
	
	/* Write the coordinates for a connector in the file */
	private void setConnectorCoordinates(int line, Margin margin) throws IOException {
		
		int x = margin.getLeft() * 95 + 81;
		int y = 41 + 100 * (line-1);
		
		intend(3);
		writeNewLine("<graphics>");
		intend(4);
		writeNewLine("<position height=\"30\" width=\"30\" " +
			         "x=\"" + x + "\" y=\"" + y +  "\"/>");
		intend(3);
		writeNewLine("</graphics>");
	}

	/* Write the coordinates for an event and a function in the file */
	public void setEventAndFunctionCoordinates(int line, Margin margin) throws IOException {
		
		int x = margin.getLeft() * 95 + 16;
		int y = 16 + 100 * (line-1);
		
		intend(3);
		writeNewLine("<graphics>");
		intend(4);
		writeNewLine("<position height=\"80\" width=\"160\" " +
			         "x=\"" + x + "\" y=\"" + y +  "\"/>");
		intend(3);
		writeNewLine("</graphics>");
	}
}