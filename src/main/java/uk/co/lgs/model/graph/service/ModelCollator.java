package uk.co.lgs.model.graph.service;

import uk.co.lgs.model.graph.GraphModel;
import uk.co.lgs.model.graph.collator.exception.CollatorException;
import uk.co.lgs.model.segment.exception.SegmentCategoryNotFoundException;

/**
 * I represent all types of collation service.
 * 
 * @author bouncysteve
 *
 */
public interface ModelCollator {

    GraphModel collate(GraphModel model) throws SegmentCategoryNotFoundException, CollatorException;

}
