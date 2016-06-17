package uk.co.lgs.model.graph.service;

import uk.co.lgs.model.graph.GraphModel;
import uk.co.lgs.model.graph.collator.exception.CollatorException;

/**
 * I represent all types of collation service.
 * 
 * @author bouncysteve
 *
 */
@FunctionalInterface
public interface ModelCollator {

    GraphModel collate(GraphModel model) throws CollatorException;

}
