package uk.co.lgs.model.graph.service;

import uk.co.lgs.model.segment.graph.GraphSegment;

public class ModelGapCollatorImpl extends AbstractModelCollatorImpl implements ModelGapCollator {

    @Override
    protected boolean shouldCollate(GraphSegment segmentBeingBuilt, GraphSegment segment) {
        return segmentBeingBuilt.getGraphSegmentGap().equals(segment.getGraphSegmentGap());
    }

}
