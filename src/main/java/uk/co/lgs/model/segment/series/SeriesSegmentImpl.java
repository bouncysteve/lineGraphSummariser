package uk.co.lgs.model.segment.series;

import uk.co.lgs.model.gradient.GradientType;
import uk.co.lgs.model.point.Point;

/**
 * I encapsulate a short section of the graph in terms of one of the data
 * series.
 *
 * @author bouncysteve
 *
 */
public class SeriesSegmentImpl implements SeriesSegment {

    GradientType gradientType;

    /**
     * We never need to know the exact gradient, so we assume that the interval
     * between records is 1. If we have combined records (and we are taking an
     * average gradient), then the segment length will be a multiple of 1.
     */
    private int segmentLength;

    private final double startValue;

    private double endValue;

    private final String startTime;

    private String endTime;

    private String label;

    private String description;

    private final String units;

    /**
     * @param startPoint
     * @param endPoint
     * @param label
     * @param description
     * @param units
     */
    public SeriesSegmentImpl(final Point startPoint, final Point endPoint, final String label, final String description,
            final String units) {
        this(startPoint, endPoint, label, description, units, 1);
    }

    /**
     * For collated segments, the segment length will be a multiple of the base
     * segment length, 1.
     *
     * @param startPoint
     * @param endPoint
     * @param label
     * @param description
     * @param units
     * @param segmentLength
     */
    public SeriesSegmentImpl(final Point startPoint, final Point endPoint, final String label, final String description,
            final String units, final int segmentLength) {
        this.startTime = startPoint.getTime();
        this.startValue = startPoint.getValue();
        this.endTime = endPoint.getTime();
        this.endValue = endPoint.getValue();
        this.segmentLength = segmentLength;
        this.label = label;
        this.description = description;
        this.units = units;
        this.gradientType = determineGradientType();
    }

    @Override
    public double getStartValue() {
        return this.startValue;
    }

    @Override
    public String getStartTime() {
        return this.startTime;
    }

    @Override
    public double getEndValue() {
        return this.endValue;
    }

    @Override
    public String getEndTime() {
        return this.endTime;
    }

    @Override
    public GradientType getGradientType() {
        return this.gradientType;
    }

    @Override
    public int getSegmentLength() {
        return this.segmentLength;
    }

    @Override
    public SeriesSegment append(final SeriesSegment newSegment) {
        this.endTime = newSegment.getEndTime();
        this.endValue = newSegment.getEndValue();
        this.segmentLength = this.segmentLength + newSegment.getSegmentLength();
        this.gradientType = determineGradientType();
        return this;
    }

    private GradientType determineGradientType() {
        final double numericGradient = (this.getEndValue() - this.getStartValue()) / this.segmentLength;
        GradientType localGradientType;
        if (0 == numericGradient) {
            localGradientType = GradientType.ZERO;
        } else if (0 < numericGradient) {
            localGradientType = GradientType.POSITIVE;
        } else {
            localGradientType = GradientType.NEGATIVE;
        }
        return localGradientType;
    }

    @Override
    public double getGradient() {
        return (this.endValue - this.startValue) / this.segmentLength;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    @Override
    public String getUnits() {
        return this.units;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

}
