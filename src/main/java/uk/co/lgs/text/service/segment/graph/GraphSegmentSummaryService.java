package uk.co.lgs.text.service.segment.graph;

import java.util.List;

import simplenlg.framework.DocumentElement;
import uk.co.lgs.model.graph.GraphModel;

/**
 * I am responsible for generating a text summary of the features of both series
 * over a segment.
 *
 * @author bouncysteve
 *
 */

public interface GraphSegmentSummaryService {

    /**
     * Return (usually) one sentence describing each segment.
     * 
     * @param model
     * @return
     */
    List<DocumentElement> getSegmentSummaries(final GraphModel model);
}
