package uk.co.lgs.model.graph.service;

import uk.co.lgs.model.graph.GraphModel;
import uk.co.lgs.model.graph.collator.exception.CollatorException;
import uk.co.lgs.model.segment.exception.SegmentCategoryNotFoundException;

/**
 * I am a collator service which combines successive series if both series
 * maintain the same gradient type for successive segments.
 * 
 * @author bouncysteve
 *
 */
public interface ModelGradientCollator extends ModelCollator {

    @Override
    public GraphModel collate(GraphModel model) throws SegmentCategoryNotFoundException, CollatorException;
}
