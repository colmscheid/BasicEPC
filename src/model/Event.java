package model;

public class Event implements EPC_Element {
	
	public Event(String name, int id) {
		
		setName(name);
		this.id = id;
	}

	private String name;
	private EPC_Element next = null;
	private int width = 1;
	private int id;

	public String getName() {
		
		return name;
	}

	public void setName(String name) {
		
		this.name = name;
	}

	public EPC_Element getNext() {
		
		return next;
	}

	public void setNext(EPC_Element next) {
		
		this.next = next;
	}
	
	public String toString() {
		
		String text = "";
		int length = name.length();
		int begin = 0;
		int number = 17;
		while (true) { 
			if (begin + number >= length) { 
				text += name.substring(begin);
				break;
			}
			text += name.substring(begin, begin + number);
			begin += number;
			if (begin < length)
				text += "";
		}
		return text;
	}


	public int getMaximumWidth() {
		
		if (next == null)
			return 1;
		width = Math.max(1, next.getMaximumWidth());
		return width;
	}
	
	public int getWidth() {
		
		return width;
	}
	
	public int getLength() {
		
		return 1;
	}
	
	public int getNumberElements() {

		if (next == null)
			return 1;
		return 1 + next.getNumberElements();
	}
	
	public EPC_Element lookForElementSuccesor(String name) {

		if (next == null || next.getName() == null)
			return null;

		if (next.getName().equals(name)) 
			return this;
		else
			return next.lookForElementSuccesor(name);
	}

	public int getId() {

		return id;
	}
	
	public String getStringRepresentation() {
		
		String text = "Ereignis: " + name + " ID=" + id +  "\n";
		if (next != null)
			text += next.getStringRepresentation();
		return text;
	}
}