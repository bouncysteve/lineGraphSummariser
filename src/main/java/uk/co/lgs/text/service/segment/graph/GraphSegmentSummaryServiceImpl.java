package uk.co.lgs.text.service.segment.graph;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import simplenlg.framework.DocumentElement;
import uk.co.lgs.model.graph.GraphModel;
import uk.co.lgs.model.segment.graph.GraphSegment;

/**
 * I am responsible for generating a summary of each individual graph segment.
 *
 * @author bouncysteve
 *
 */
@Component
public class GraphSegmentSummaryServiceImpl implements GraphSegmentSummaryService {

    @Override
    public List<DocumentElement> getSegmentSummaries(final GraphModel model) {
        final boolean intersectingGraph = model.isIntersecting();

        final List<DocumentElement> segmentSummaries = new ArrayList<>();
        boolean mentionedMaxGapYet = false;
        boolean mentionedMinGapYet = false;
        for (final GraphSegment graphSegment : model.getGraphSegments()) {
            segmentSummaries.add(getSummary(graphSegment, intersectingGraph, mentionedMaxGapYet, mentionedMinGapYet));
            if (!mentionedMaxGapYet && graphSegment.isGlobalMaximumGapAtSegmentEnd()) {
                mentionedMaxGapYet = true;
            }
            if (!mentionedMinGapYet && graphSegment.isGlobalMinimumGapAtSegmentEnd()) {
                mentionedMinGapYet = true;
            }
        }
        return segmentSummaries;
    }

    private DocumentElement getSummary(final GraphSegment graphSegment, final boolean intersectingGraph,
            final boolean mentionedMaxGapYet, final boolean mentionedMinGapYet) {
        // oppositeTrends?
        // converging?
        // diverging?
        // parallel?

        return null;
    }

}
