package uk.co.lgs.model.graph.service;

import java.util.List;

import uk.co.lgs.domain.graph.GraphData;
import uk.co.lgs.model.segment.exception.SegmentCategoryNotFoundException;
import uk.co.lgs.model.segment.graph.GraphSegment;

public interface SegmentationService {

    List<GraphSegment> segment(GraphData graphData) throws SegmentCategoryNotFoundException;

}
