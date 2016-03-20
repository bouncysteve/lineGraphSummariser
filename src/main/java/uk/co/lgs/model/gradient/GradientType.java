package uk.co.lgs.model.gradient;

public enum GradientType {
    POSITIVE(1), NEGATIVE(-1), ZERO(0);

    private int multiplier;

    private GradientType(int multiplier) {
        this.multiplier = multiplier;
    }

    public int getMultiplier() {
        return this.multiplier;
    }
}
