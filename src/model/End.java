package model;

/* Represents the end of an epc */

public class End implements EPC_Element {
	
	public End() {
		
	}

	public EPC_Element getNext() {
		
		return null;
	}

	public void setNext(EPC_Element succesor) {

	}

	public String toString() {
		
		return "Ende";
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
		
		return "Ende\n";
	}
}
