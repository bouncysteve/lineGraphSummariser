package uk.co.lgs.model.graph.service;

import uk.co.lgs.model.segment.exception.SegmentAppendException;
import uk.co.lgs.model.segment.exception.SegmentCategoryNotFoundException;
import uk.co.lgs.model.segment.graph.GraphSegment;

public interface GraphSegmentAppender {

    GraphSegment append(GraphSegment originalSegment, GraphSegment additionalSegment)
            throws SegmentCategoryNotFoundException, SegmentAppendException;
}
