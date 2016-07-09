package uk.co.lgs.text.service.segment.graph;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import simplenlg.framework.DocumentElement;
import uk.co.lgs.model.gradient.GradientType;
import uk.co.lgs.model.graph.GraphModel;
import uk.co.lgs.model.segment.graph.GraphSegment;
import uk.co.lgs.text.service.label.LabelService;
import uk.co.lgs.text.service.synonym.SynonymService;
import uk.co.lgs.text.service.value.ValueService;

/**
 * I am responsible for generating a summary of each individual graph segment.
 *
 * @author bouncysteve
 *
 */
@Component
public class GraphSegmentSummaryServiceImpl implements GraphSegmentSummaryService {

    @Autowired
    private LabelService labelService;
    @Autowired
    private SynonymService synonymService;
    @Autowired
    private ValueService valueService;

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
        DocumentElement summary;
        if (isOppositeTrends(graphSegment)) {
            summary = getOppositeTrendsSummary(graphSegment, intersectingGraph, mentionedMaxGapYet, mentionedMinGapYet);
        } else {
            summary = getSameTrendsSummary(graphSegment, intersectingGraph, mentionedMaxGapYet, mentionedMinGapYet);
        }

        // TODO: if both end on same value then add extra sentence
        return summary;
    }

    private DocumentElement getOppositeTrendsSummary(final GraphSegment graphSegment, final boolean intersectingGraph,
            final boolean mentionedMaxGapYet, final boolean mentionedMinGapYet) {
        final boolean diverging = isDiverging(graphSegment);
        return null;
    }

    private boolean isDiverging(final GraphSegment graphSegment) {
        graphSegment.getG
        return false;
    }

    private DocumentElement getSameTrendsSummary(final GraphSegment graphSegment, final boolean intersectingGraph,
            final boolean mentionedMaxGapYet, final boolean mentionedMinGapYet) {
        // TODO Auto-generated method stub
        return null;
    }

    private boolean isOppositeTrends(final GraphSegment graphSegment) {
        final GradientType firstGradient = graphSegment.getFirstSeriesTrend();
        final GradientType secondGradient = graphSegment.getSecondSeriesTrend();
        return null != firstGradient && null != secondGradient && firstGradient.equals(secondGradient);
    }

}
