package model;

/* Represents subprocess without activities of an epc */

public class Nothing implements EPC_Element {

	
	public Nothing() {
		
		
	}
	
	@Override
	public EPC_Element getNext() {

		return null;
	}

	@Override
	public void setNext(EPC_Element succesor) {

		
	}

	@Override
	public EPC_Element lookForElementSuccesor(String name) {

		return null;
	}

	@Override
	public String getName() {

		return null;
	}

	@Override
	public int getId() {

		return 0;
	}

	@Override
	public int getMaximumWidth() {

		return 1;
	}

	@Override
	public int getWidth() {

		return 1;
	}

	@Override
	public int getLength() {

		return 0;
	}

	@Override
	public int getNumberElements() {

		return 1;
	}

	@Override
	public String getStringRepresentation() {

		return "Nichts\n";
	}
}