package model;

public class Warning {

	private int line;
	private WarningType type;
	
	public Warning(int line, WarningType type) {
		
		this.line = line;
		this.type = type;
	}
	
	public String toString() {
		
		String text = "Warnung - ";
		
		if (line != 0)
			text += "Zeile " + line + ": ";	
		
		if (type.equals(WarningType.ElementsAfterEnd))
			text += "Nach dem Prozessende sollten in dem entsprechenden Teilprozess keine"
					+ "weiteren Elemente folgen. Das Element in der angegebenen Zeile wird "
					+ "ignoriert.";
		else if (type.equals(WarningType.ExceptedAlternatesEnd))
			text += "Hier sollte ALTERNATIVEN ENDE stehen um die alternativen Teilprozesse"
					+ "zusammenzuf�hren.";
		else if (type.equals(WarningType.ExceptedStartEnd))
			text += "Hier sollte STARTPROZESSE ENDE stehen um die Startprozesse"
					+ "zusammenzuf�hren.";
		else if (type.equals(WarningType.ExceptedOptionsEnd))
			text += "Hier sollte OPTIONEN ENDE stehen um die Teilprozesse"
					+ "zusammenzuf�hren.";
		else if (type.equals(WarningType.ExceptedSynchronise))
			text += "Hier sollte SYNCHRONISIERE stehen um die paralellen Teilprozesse"
					+ "zusammenzuf�hren.";
		else if (type.equals(WarningType.ExceptedXor))
			text += "Hier sollte ODER stehen um die alternativen Teilprozesse"
					+ "abzugrenzen.";
		else if (type.equals(WarningType.ExceptedAnd))
			text += "Hier sollte UND stehen um die paralellen Teilprozesse"
					+ "abzugrenzen.";
		else if (type.equals(WarningType.NoElement))
			text += "Hier muss ein Ereignis, eine Funktion oder ein Schl�sselwort "
					+ "stehen. Die Zeile wird �bersprungen.";
		else if (type.equals(WarningType.NoEPCElement))
			text += "Hier muss ein Ereignis, eine Funktion oder ein Schl�sselwort zum Aufspalten des Prozesses"
					+ "stehen. Die Zeile wird �bersprungen.";
		else if (type.equals(WarningType.MissingSynchronise))
			text += "Es fehlt ein Schl�sselwort zum Zusammenf�hren der Teilprozesse.";
		
		return text;
	}
}
