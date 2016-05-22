package uk.co.lgs.text.service.segment.graph;

import simplenlg.framework.DocumentElement;
import uk.co.lgs.model.segment.graph.GraphSegment;

public interface GraphSegmentSummaryService {

    DocumentElement getSummary(GraphSegment graphSegment);
}
