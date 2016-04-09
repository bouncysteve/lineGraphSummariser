package uk.co.lgs.model.segment.series;

import uk.co.lgs.model.gradient.GradientType;
import uk.co.lgs.model.point.Point;

public class SeriesSegmentImpl implements SeriesSegment {

    GradientType gradientType;

    /**
     * We never need to know the exact gradient, so we assume that the interval
     * between records is 1. If we have combined records (and we are taking an
     * average gradient), then the segment length will be a multiple of 1.
     */
    private int segmentLength;

    private double startValue;

    private double endValue;

    private String startTime;

    private String endTime;

    public SeriesSegmentImpl(Point startPoint, Point endPoint) {
        this(startPoint, endPoint, 1);
    }

    /**
     * For collated segments, the segment length will be a multiple of the base
     * segment length, 1.
     * 
     * @param startTimeAndValue
     * @param endTimeAndValue
     * @param segmentLength
     */
    public SeriesSegmentImpl(Point startPoint, Point endPoint, int segmentLength) {
        this.startTime = startPoint.getTime();
        this.startValue = startPoint.getValue();
        this.endTime = endPoint.getTime();
        this.endValue = endPoint.getValue();
        this.segmentLength = segmentLength;
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
    public SeriesSegment append(SeriesSegment newSegment) {
        this.endTime = newSegment.getEndTime();
        this.endValue = newSegment.getEndValue();
        this.segmentLength++;
        this.gradientType = determineGradientType();
        return this;
    }

    private GradientType determineGradientType() {
        double numericGradient = (this.getEndValue() - this.getStartValue()) / this.segmentLength;
        GradientType gradientType;
        if (0 == numericGradient) {
            gradientType = GradientType.ZERO;
        } else if (0 < numericGradient) {
            gradientType = GradientType.POSITIVE;
        } else {
            gradientType = GradientType.NEGATIVE;
        }
        return gradientType;
    }

    @Override
    public double getGradient() {
        return (this.endValue - this.startValue) / this.segmentLength;
    }

}
