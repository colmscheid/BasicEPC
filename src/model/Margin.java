package model;

public class Margin {

	private int left;
	private int right;
	
	public Margin(int left, int right) {
		
		setLeft(left);
		setRight(right);
	}
	

	public int getLeft() {
		
		return left;
	}

	public void setLeft(int left) {
		
		this.left = left;
	}

	public int getRight() {
		
		return right;
	}

	public void setRight(int right) {
		
		this.right = right;
	}

	public Margin addMargin(int left, int right) {
		
		return new Margin(getLeft() + left, getRight() + right);
	}
	
	public int getCompleteMargin() {
		
		return left + right;
	}
	
	public String toString() {
		
		return left + " - " + right;
	}
}