package uk.co.lgs.model.graph.service;

import uk.co.lgs.model.graph.GraphModel;
import uk.co.lgs.model.graph.collator.exception.CollatorException;
import uk.co.lgs.model.segment.exception.SegmentCategoryNotFoundException;

/**
 * I am a super-interface representing all types of collation service.
 * 
 * @author bouncysteve
 *
 */
public abstract interface ModelCollator {

    GraphModel collate(GraphModel model) throws SegmentCategoryNotFoundException, CollatorException;

}
