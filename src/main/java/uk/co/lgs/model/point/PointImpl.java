package uk.co.lgs.model.point;

public class PointImpl implements Point {

    private String time;

    private double value;

    public PointImpl(String time, double value) {
        super();
        this.time = time;
        this.value = value;
    }

    @Override
    public String getTime() {
        return this.time;
    }

    @Override
    public double getValue() {
        return this.value;
    }

}
