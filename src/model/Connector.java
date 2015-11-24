package model;

public class Connector implements EPC_Element {

	private ConnectorType type;
	private EPC_Element next = null;
	private EPC_Element[] subprocesses;
	private int width;
	private int id;
	
	public Connector(ConnectorType type, int id) {
		
		setType(type);
		this.id = id;
	}

	public Connector(ConnectorType type, EPC_Element[] subprocesses, int id) {
		
		this(type, id);
		setSubprocesses(subprocesses);
	}

	public EPC_Element getNext() {

		return next;
	}

	public void setNext(EPC_Element succesor) {

		next = succesor;
	}

	public ConnectorType getType() {
		
		return type;
	}

	public void setType(ConnectorType type) {
		
		this.type = type;
	}

	public EPC_Element[] getSubprocesses() {
		
		return subprocesses;
	}
	
	public EPC_Element getSubprocess(int number) {
		
		return subprocesses[number];
	}

	public void setSubprocesses(EPC_Element[] subprocesses) {
		
		this.subprocesses = subprocesses;
	}
	
	public void setSubprocess(int number, EPC_Element element) {
		
		subprocesses[number] = element;
	}
	
	public String toString() {
		
		String text = "Gateway " + type; //+ //"\n";
		/*text += "(\n";
		for (int i = 0; i < subprocesses.length; i++) {
			text +=* subprocesses[i];
			if (i+1<subprocesses.length)
				text += "\n" + type + "\n";
		}
		text += "\n)";
		if (next != null)
			text += "\n" + next;*/
		return text;
	}
	
	public int getMaximumWidth() {
		
		int number = 0;
		if (subprocesses != null) {
			for (int i = 0; i < subprocesses.length; i++) {
				number += subprocesses[i].getMaximumWidth();
			}
		width = number;
		}
		
		if (next == null)
			return width;
		
		return Math.max(width, next.getMaximumWidth());
	}
	
	public int getWidth() {
		
		return width;
	}
	
	public int getLength() {

		if (subprocesses == null)
			return 1;
		
		int maximum = -1;
		EPC_Element element;
		int sublength = 0;
		for (int i = 0; i < subprocesses.length; i++) {
			sublength = 0;
			element = subprocesses[i];
			while (element != null) {
				sublength += element.getLength();
				if (element instanceof Jump)
					element = null;
				else
					element = element.getNext();
			}
			maximum = Math.max(sublength, maximum);
		}

		return maximum + 2;
	}
	
	public int getNumberElements() {

		int number = 2;
		for (int i = 0; i < subprocesses.length; i++) {
			number += subprocesses[i].getNumberElements();
		}
		
		if (next == null)
			return number;
		return number + next.getNumberElements();
	}
	
	public EPC_Element lookForElementSuccesor(String name) {
		
		EPC_Element look = null;
		
		for (EPC_Element child : subprocesses) {
			if (child instanceof Jump)
				break;
			if (child.getName().equals(name))
				return this;
			else {
				look = child.lookForElementSuccesor(name);
				if (look != null)
					return look;
			}
		}
		if (next == null || next.getName() == null)
			return null;
		
		if (next.getName().equals(name)) 
			return this;
		
		return next.lookForElementSuccesor(name);
	}

	public int getId() {
		
		return id;
	}
	
	public String getName() {

		return "";
	}
	
	public String getStringRepresentation() {
		
		String text = "Gateway: " + type + " ID=" + id +  "\n";
		if (subprocesses != null) {
			
			text += "(\n";
			
			for (EPC_Element child : subprocesses) {
				text += child.getStringRepresentation();
				text += type + "\n";
			}
			text += ")\n";
		}
		if (next != null)
			text += next.getStringRepresentation();
		
		return text;
	}
}