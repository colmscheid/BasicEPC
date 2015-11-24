package model;

import converter.Token;

public class Jump implements EPC_Element {

	EPC_Element next;
	Token next_name;
	boolean end_connector = false;
	
	public Jump(Token name) {
		
		next_name = name;
	}
	
	public Token getNextName() {
		
		return next_name;
	}
	
	public EPC_Element getNext() {

		return next;
	}

	public void setNext(EPC_Element succesor) {
		
		next = succesor;
	}
	
	public boolean getEndConnector() {
		
		return end_connector;
	}
	
	public void setEndConnector(boolean connector) {
		
		end_connector = connector;
	}

	public String toString() {
		
		return "Springe zu " + next;
	}
	
	public int getMaximumWidth() {
		
		return 1;
	}
	
	public int getWidth() {
		
		return 1;
	}
	
	public int getLength() {
		
		return 0;
	}

	public int getNumberElements() {

		return 1;
	}
	
	public EPC_Element lookForElementSuccesor(String name) {

		return null;
	}

	public int getId() {

		return 0;
	}
	
	public String getName() {

		return null;
	}
	
	public String getStringRepresentation() {
		
		return "Springe zu " + next.getId() + "\n";
	}
}