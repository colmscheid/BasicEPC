package converter;

public class ConverterException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/* No reserves word or event or function */
	public static ConverterException wrongSyntax(int line) {
		
		return new ConverterException("Zeile " + line + " - " +
				"Kein gültiges Schlüsselwort bzw. Funktion oder Ereignis!");
	}
	
	/* No event description after an "E" */
	public static ConverterException missingEventDescription(int line) {
		
		return new ConverterException("Zeile " + line + " - " +
				"Nach dem E fehlt eine Beschreibung des Ereignisses!");
	}
	
	/* No event description after an "F */
	public static ConverterException missingFunctionDescription(int line) {
		
		return new ConverterException("Zeile " + line + " - " +
				"Nach dem F fehlt eine Beschreibung der Funktion!");
	}
	
	/* No STARTEREIGNISSE at the beginning */
	public static ConverterException exceptedStartReservedWord(int line) {
		
		return new ConverterException("Zeile " + line + " - " +
			"Die EPK muss mit dem Schlüsselwort STARTEREIGNISSE beginnen!");
	}
	
	/* Excepted an EPC Element */
	public static ConverterException exceptedEPCElement(int line) {
		
		return new ConverterException("Zeile " + line + " - " +
				"Hier müsste ein Ereignis, eine Funktion oder ein Konnektor stehen.");
	}
	
	/* Excepted an SYNCHRONISE */
	public static ConverterException exceptedSynchronise(int line) {
		
		return new ConverterException("Zeile " + line + " - " +
				"Hier müsste SYNCHRONISIERE zum Zusammenführen der Teilprozesse stehen.");
	}
	
	/* Excepted an ALTERNATESEND */
	public static ConverterException exceptedAlternateEnd(int line) {
		
		return new ConverterException("Zeile " + line + " - " +
				"Hier müsste ALTERNATIVEN ENDE zum Zusammenführen der Teilprozesse stehen.");
	}
	
	/* Excepted an STARTEND */
	public static ConverterException exceptedStartEnd(int line) {
		
		return new ConverterException("Zeile " + line + " - " +
				"Hier müsste STARTEREIGNISSE ENDE zum Zusammenführen der Ereignisse stehen.");
	}
	
	/* Excepted an AND */
	public static ConverterException exceptedAnd(int line) {
		
		return new ConverterException("Zeile " + line + " - " +
				"Hier müsste ein UND als Abgrenzung zwischen den einzelnen Teilprozessen stehen.");
	}
	
	/* Excepted an XOR */
	public static ConverterException exceptedXor(int line) {
		
		return new ConverterException("Zeile " + line + " - " +
				"Hier müsste ein ODER als Abgrenzung zwischen den einzelnen Teilprozessen stehen.");
	}
	
	/* Exception: There can't be another element after the process end. */
	public static ConverterException exceptedNothingAfterEnd(int line) {
		
		return new ConverterException("Zeile " + line + " - " +
				"Nach PROZESSENDE darf kein weiteres Element folgen.");
	}
	
	/* Exception: There have to be an event or function after a jump */
	public static ConverterException exceptedAfterJump(int line) {
		
		return new ConverterException("Zeile " + line + " - " +
				"Nach SPRINGE ZU muss ein Ereignis oder eine Funktion stehen, zu der " +
				"zurückgesprungen wird.");
	}
	
	/* Exception: No event or function with the given name is found */
	public static ConverterException afterJumpNotFound(int line) {
		
		return new ConverterException("Zeile " + line + " - " +
				"Das angegebene Element nach SPRINGE ZU existiert nicht.");
	}

	private ConverterException(String msg) {
		
		super(msg);
	}
}