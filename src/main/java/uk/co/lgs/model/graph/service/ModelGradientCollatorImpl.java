package uk.co.lgs.model.graph.service;

import org.springframework.stereotype.Component;

import uk.co.lgs.model.segment.graph.GraphSegment;

@Component
public class ModelGradientCollatorImpl extends AbstractModelCollatorImpl implements ModelGradientCollator {

    @Override
    protected boolean shouldCollate(GraphSegment segmentBeingBuilt, GraphSegment segment) {
        return segmentBeingBuilt.getGraphSegmentGradientCategory().equals(segment.getGraphSegmentGradientCategory());
    }
}
