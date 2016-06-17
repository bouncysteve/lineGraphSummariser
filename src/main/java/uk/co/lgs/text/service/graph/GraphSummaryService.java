package uk.co.lgs.text.service.graph;

import uk.co.lgs.model.graph.GraphModel;

/**
 * I am responsible for constructing a text summary of the entire graph. I am
 * the main class in the text generation area.
 * 
 * @author bouncysteve
 *
 */
@FunctionalInterface
public interface GraphSummaryService {

    String getSummary(GraphModel model);

}
