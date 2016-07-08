package uk.co.lgs.model.graph.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import uk.co.lgs.model.segment.graph.GraphSegment;

/**
 * I have information about the whole graph and I add that information into
 * already created graphSegments.
 *
 * @author bouncysteve
 *
 */
@Component
public class GapServiceImpl implements GapService {

    @Override
    public List<GraphSegment> addGapInfo(final List<GraphSegment> graphSegments) {
        Double minimumGap = Double.MAX_VALUE;
        Double maximumGap = 0D;
        boolean graphContainsIntersection = false;
        for (final GraphSegment graphSegment : graphSegments) {
            final double gap = graphSegment.getGapBetweenSeriesEndValues();
            if (!graphContainsIntersection && graphSegment.isIntersecting()) {
                graphContainsIntersection = true;
            }
            if (!graphContainsIntersection && gap < minimumGap) {
                minimumGap = gap;
            }
            if (gap > maximumGap) {
                maximumGap = gap;
            }
        }
        return setMaxMinGapInfo(minimumGap, maximumGap, graphContainsIntersection, graphSegments);
    }

    private List<GraphSegment> setMaxMinGapInfo(final double minimumGap, final double maximumGap,
            final boolean graphContainsIntersection, final List<GraphSegment> graphSegments) {
        final List<GraphSegment> graphSegmentsWithGapInfo = new ArrayList<>();
        graphSegmentsWithGapInfo.addAll(graphSegments);
        for (final GraphSegment graphSegment : graphSegmentsWithGapInfo) {
            final double gap = graphSegment.getGapBetweenSeriesEndValues();
            if (!graphContainsIntersection && gap == minimumGap) {
                graphSegment.setGlobalMinimumGapAtSegmentEnd(true);
            }
            if (gap == maximumGap) {
                graphSegment.setGlobalMaximumGapAtSegmentEnd(true);
            }
        }
        return graphSegmentsWithGapInfo;
    }
}
