package converter;

public enum TokenType {

	STARTEND,
	ENDEND,
	PARALELL,
	SYNCHRONISE,
	ALTERNATES,
	ALTERNATESEND,
	OPTIONS,
	OPTIONSEND,
	END,
	AND,
	XOR,
	EVENT,
	FUNCTION,
	JUMP,
	NOTHING,
	UNDEFINED;
	
	/* Is the type a type to end a gateway? */
	public boolean isGatewayEnd() {
		
		return (this.equals(ALTERNATESEND) ||
			    this.equals(SYNCHRONISE) ||
			    this.equals(STARTEND) ||
			    this.equals(ENDEND) ||
			    this.equals(OPTIONSEND));
	}
}
