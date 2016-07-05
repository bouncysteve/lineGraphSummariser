package uk.co.lgs.model.segment.graph;

import java.util.List;

import uk.co.lgs.model.gradient.GradientType;
import uk.co.lgs.model.segment.exception.SegmentAppendException;
import uk.co.lgs.model.segment.graph.category.GapTrend;
import uk.co.lgs.model.segment.graph.category.GraphSegmentGradient;
import uk.co.lgs.model.segment.series.SeriesSegment;

/**
 * Models a segment of a graph, exposes the properties which are useful for
 * summarising the segment.
 *
 * @author bouncysteve
 *
 */
public interface GraphSegment {

    /**
     * @return true if the series intersect at any point in the segment, false
     *         otherwise.
     */
    boolean isIntersecting();

    /**
     * @return true if all(both) series segments have identical gradients, false
     *         otherwise.
     */
    boolean isParallel();

    /**
     * @return the GraphSegmentGradient This describes the gradients of the two
     *         series and any intersections.
     */
    GraphSegmentGradient getGraphSegmentGradientCategory();

    /**
     * @return the label of the time at the start of the segment
     */
    String getStartTime();

    /**
     * @return the label of the time at the end of the segment
     */
    String getEndTime();

    /**
     * For use in collation. Makes a single (longer) segment by appending the
     * new segment to the old.
     *
     * @param newSegment
     *            the segment to be appended.
     * @return the new longer segment.
     * @throws SegmentAppendException
     *             if the segments are non-contiguous.
     */
    GraphSegment append(GraphSegment newSegment) throws SegmentAppendException;

    /**
     * @param index
     * @return the seriesSegment at the given index.
     */
    SeriesSegment getSeriesSegment(int index);

    /**
     * Assuming that all data points are equidistant, the distance between two
     * adjacent points is one. For collated segments the length may be any
     * integer multiple of one.
     *
     * @return the length of the series
     */
    int getLength();

    /**
     * @return a list of seriesSegments. The order will be consistent for any
     *         given GraphSegment.
     */
    List<SeriesSegment> getSeriesSegments();

    /**
     * @return whether the two series are converging, diverging or neither.
     */
    GapTrend getGraphSegmentTrend();

    /**
     *
     *
     * @return the series segment which has the higher value at the start of the
     *         graph segment, or null if their values are the same.
     */
    SeriesSegment getHigherSeriesAtStart();

    /**
     * @return the series segment which has the higher value at the end of the
     *         graph segment, or null if their values are the same.
     */
    SeriesSegment getHigherSeriesAtEnd();

    /**
     * @return the gradient type of the first series.
     */
    GradientType getFirstSeriesTrend();

    /**
     * @return the gradient type of the second series.
     */
    GradientType getSecondSeriesTrend();

    /**
     * @param seriesSegment
     * @return The index of the series, i.e. 0 for firstSeries, 1 for
     *         secondSeries...
     */
    int indexOf(SeriesSegment seriesSegment);

    /**
     * The modulus of the difference between the series' values at the end of
     * the segment is the largest it is during the period covered by the graph.
     * NB. The maximum gap may be exhibited by multiple segments.
     *
     * @return
     */
    boolean isGlobalMaximumGapAtSegmentEnd();

    /**
     * @param b
     *            whether the end values of this segment exhibit the largest
     *            difference between series in the whole graph.
     */
    void setGlobalMaximumGapAtSegmentEnd(boolean b);

    /**
     * The modulus of the difference between the series' values at the end of
     * the segment is the smallest it is during the period covered by the graph.
     * NB. The minimum gap may be exhibited by multiple segments. NB. If any
     * segments intersect then this value is false for all segments.
     *
     * @return
     */
    boolean isGlobalMinimumGapAtSegmentEnd();

    /**
     * @param b
     *            whether the end values of this segment exhibit the smallest
     *            difference between series in the whole graph (if such a
     *            difference exists, i.e. there are no intersections).
     **/
    void setGlobalMinimumGapAtSegmentEnd(boolean b);

    /**
     * @return the modulus of the difference between the series' values at the
     *         end of the segment.
     */
    double getGapBetweenSeriesEndValues();
}