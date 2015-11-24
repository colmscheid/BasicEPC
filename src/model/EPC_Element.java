package model;

public interface EPC_Element {
	
	/* Get the <number>th succesor */
	public EPC_Element getNext();
	
	/* Set the <number>th succesor */
	public void setNext(EPC_Element succesor);
	
	/* Search the element before the element with the given name */
	public EPC_Element lookForElementSuccesor(String name);
	
	
	public String getName();
	
	public int getId();
	
	public int getMaximumWidth();
	
	public int getWidth();
	
	public int getLength();
	
	public int getNumberElements();
	
	public String getStringRepresentation();
}