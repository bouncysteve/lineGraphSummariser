package uk.co.lgs.model.graph.service;

import org.springframework.stereotype.Component;

import uk.co.lgs.model.segment.graph.GraphSegment;
import uk.co.lgs.model.segment.graph.category.GapTrend;
import uk.co.lgs.model.segment.graph.category.GraphSegmentGradient;

/**
 * I collate successive graph segments if they have the same gap trend
 * (converging/diverging/neither) and both series maintain their trend and they
 * do not intersect.
 * 
 * @author bouncysteve
 *
 */
@Component
public class GapAndGradientCollatorImpl extends AbstractModelCollatorImpl implements GapAndGradientCollator {

    @Override
    protected boolean shouldCollate(GraphSegment segmentBeingBuilt, GraphSegment segment) {
        GapTrend gt1 = segmentBeingBuilt.getGraphSegmentTrend();
        GapTrend gt2 = segment.getGraphSegmentTrend();
        GraphSegmentGradient gsg1 = segmentBeingBuilt.getGraphSegmentGradientCategory();
        GraphSegmentGradient gsg2 = segment.getGraphSegmentGradientCategory();
        

        return segmentBeingBuilt.getGraphSegmentTrend().equals(segment.getGraphSegmentTrend())
                && segmentBeingBuilt.getGraphSegmentGradientCategory().equals(segment.getGraphSegmentGradientCategory())
                && !segmentBeingBuilt.isIntersecting()
                && !segment.isIntersecting();
    }
}
