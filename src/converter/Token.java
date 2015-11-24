package converter;

public class Token {

	private TokenType type;
	private String text;
	private int line;
	
	public Token(TokenType type, String text, int line) {
		
		this.type = type;
		this.text = text;
		this.line = line;
	}
	
	public Token(TokenType type, int line) {
		
		this.type = type;
		this.line = line;
	}
	
	public TokenType getType() {
		
		return type;
	}
	
	public void setType(TokenType type) {
		
		this.type = type;
	}
	
	public String getText() {
		
		return text;
	}
	
	public int getLine() {
		
		return line;
	}
	
	public String toString() {
		
		if (type.equals(TokenType.EVENT)) {
			return "Ereignis: " + text;
			
		} else if (type.equals(TokenType.FUNCTION)) {
			return "Funktion: " + text;
		}
		
		return type.toString();
	}
}