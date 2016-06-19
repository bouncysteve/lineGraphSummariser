package uk.co.lgs.model.segment.graph.category;

/**
 * I encapsulate the relationship between the start and end values of the two
 * series.
 * 
 * @author bouncysteve
 *
 */
public enum GapTrend {
    CONVERGING("conv"), DIVERGING("divg"), PARALLEL("para");

    private String name;

    private GapTrend(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}