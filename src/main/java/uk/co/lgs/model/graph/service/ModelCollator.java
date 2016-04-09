package uk.co.lgs.model.graph.service;

import uk.co.lgs.model.graph.GraphModel;
import uk.co.lgs.model.graph.collator.exception.CollatorException;
import uk.co.lgs.model.segment.exception.SegmentCategoryNotFoundException;

public interface ModelCollator {

    public GraphModel collate(GraphModel model) throws SegmentCategoryNotFoundException, CollatorException;
}
