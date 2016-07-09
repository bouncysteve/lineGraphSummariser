package uk.co.lgs.model.graph.service;

import java.util.List;

import uk.co.lgs.domain.graph.GraphData;
import uk.co.lgs.model.segment.exception.SegmentCategoryNotFoundException;
import uk.co.lgs.model.segment.graph.GraphSegment;

/**
 * I split the graph data into a list of successive graph segments.
 *
 * @author bouncysteve
 *
 */

public interface SegmentationService {

    /**
     * Convert graphData into a list of segments.
     * 
     * @param graphData
     * @return
     * @throws SegmentCategoryNotFoundException
     */
    List<GraphSegment> segment(GraphData graphData) throws SegmentCategoryNotFoundException;

}
