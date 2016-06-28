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

    boolean isIntersecting();

    Double getValueAtIntersection();

    boolean isParallel();

    GraphSegmentGradient getGraphSegmentGradientCategory();

    String getStartTime();

    String getEndTime();

    GraphSegment append(GraphSegment newSegment) throws SegmentAppendException;

    SeriesSegment getSeriesSegment(int index);

    int getLength();

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
     * @param higherSeries
     * @return The index of the series, i.e. 0 for firstSeries, 1 for
     *         secondSeries...
     */
    int indexOf(SeriesSegment higherSeries);

}