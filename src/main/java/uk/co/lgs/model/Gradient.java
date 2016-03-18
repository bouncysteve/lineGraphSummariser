package uk.co.lgs.model;

public enum Gradient {
	POSITIVE(1),
	NEGATIVE(-1),
	ZERO(0);

	private int multiplier;
	
	private Gradient (int multiplier){
		this.multiplier = multiplier;
	}
	public int getMultiplier() {
		return this.multiplier;
	}
}
