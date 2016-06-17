package uk.co.lgs.model.segment.graph;

import java.text.DecimalFormat;
import java.util.List;

import uk.co.lgs.model.segment.exception.SegmentAppendException;
import uk.co.lgs.model.segment.exception.SegmentCategoryNotFoundException;
import uk.co.lgs.model.segment.graph.category.GraphSegmentGap;
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

    static DecimalFormat df = new DecimalFormat("#.00");

    boolean isIntersecting();

    Double getValueAtIntersection();

    boolean isParallel();

    GraphSegmentGradient getGraphSegmentGradientCategory();

    String getStartTime();

    String getEndTime();

    GraphSegment append(GraphSegment newSegment) throws SegmentCategoryNotFoundException, SegmentAppendException;

    SeriesSegment getSeriesSegment(int index);

    int getLength();

    List<SeriesSegment> getSeriesSegments();

    GraphSegmentGap getGraphSegmentGap();

}