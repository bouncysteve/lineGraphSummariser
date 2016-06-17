package uk.co.lgs.text.service.segment.graph;

import simplenlg.framework.DocumentElement;
import uk.co.lgs.model.segment.graph.GraphSegment;

/**
 * I am responsible for generating a text summary of the features of both series
 * over a segment.
 * 
 * @author bouncysteve
 *
 */
public interface GraphSegmentSummaryService {

    DocumentElement getSummary(GraphSegment graphSegment);
}
