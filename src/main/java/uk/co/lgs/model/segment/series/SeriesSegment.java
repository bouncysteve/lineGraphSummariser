package uk.co.lgs.model.segment.series;

import uk.co.lgs.model.gradient.GradientType;

/**
 * Encapsulates the features of one series over a time period of the graph.
 *
 * @author bouncysteve
 *
 */
public interface SeriesSegment {

    /**
     * @return the numeric value of the series at the start of the time period.
     */
    double getStartValue();

    /**
     * @return the time label at the start of the time period.
     */
    String getStartTime();

    /**
     * @return the numeric value of the series at the end of the time period.
     */
    double getEndValue();

    /**
     * @return the time label at the end of the time period.
     */
    String getEndTime();

    /**
     * @return the general gradient trend over the time period.
     */
    GradientType getGradientType();

    /**
     * @return the actual gradient over the time period.
     */
    double getGradient();

    /**
     * @return the length of the segment, in multiples of the minimum possible
     *         segment length.
     */
    int getSegmentLength();

    /**
     * @param segmentToAppend
     * @return a longer version of this segment, with the argument segment
     *         appended.
     */
    SeriesSegment append(SeriesSegment segmentToAppend);

    /**
     * @return the label for this series, as it appears on the graph.
     */
    String getLabel();

    /**
     * @return a longer description of this series, if specified in the schema
     *         for the data.
     */
    String getDescription();

    /**
     * @return the units in which the values are measured.
     */
    String getUnits();

}