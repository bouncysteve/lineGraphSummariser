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
        boolean firstSegment = true;
        for (final GraphSegment graphSegment : graphSegments) {
            if (!graphContainsIntersection && graphSegment.isIntersecting()) {
                graphContainsIntersection = true;
            }
            if (firstSegment) {
                firstSegment = false;
                final double gapAtStartOfGraph = graphSegment.getGapBetweenSeriesStartValues();
                if (gapAtStartOfGraph > maximumGap) {
                    maximumGap = gapAtStartOfGraph;
                }
                if (!graphContainsIntersection && gapAtStartOfGraph < minimumGap) {
                    minimumGap = gapAtStartOfGraph;
                }
            }

            final double gap = graphSegment.getGapBetweenSeriesEndValues();
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

    @Override
    public boolean isGlobalMaximumAtGraphStart(final List<GraphSegment> graphSegments) {
        Double maximumGap = 0D;
        boolean maximumGapAtStartOfGraph = true;
        boolean firstSegment = true;
        for (final GraphSegment graphSegment : graphSegments) {
            if (firstSegment) {
                firstSegment = false;
                final double gapAtStartOfGraph = graphSegment.getGapBetweenSeriesStartValues();
                if (gapAtStartOfGraph > maximumGap) {
                    maximumGap = gapAtStartOfGraph;
                }
            }

            final double gap = graphSegment.getGapBetweenSeriesEndValues();
            if (gap > maximumGap) {
                maximumGap = gap;
                maximumGapAtStartOfGraph = false;
            }
        }
        return maximumGapAtStartOfGraph;
    }

    @Override
    public boolean isGlobalMinimumAtGraphStart(final List<GraphSegment> graphSegments) {
        Double minimumGap = Double.MAX_VALUE;
        final boolean minimumGapAtStartOfGraph = true;
        boolean graphContainsIntersection = false;
        boolean firstSegment = true;
        for (final GraphSegment graphSegment : graphSegments) {
            if (!graphContainsIntersection && graphSegment.isIntersecting()) {
                graphContainsIntersection = true;
            }
            if (firstSegment) {
                firstSegment = false;
                final double gapAtStartOfGraph = graphSegment.getGapBetweenSeriesStartValues();
                if (!graphContainsIntersection && gapAtStartOfGraph < minimumGap) {
                    minimumGap = gapAtStartOfGraph;
                }
            }

            final double gap = graphSegment.getGapBetweenSeriesEndValues();
            if (!graphContainsIntersection && gap < minimumGap) {
                minimumGap = gap;
            }
        }
        return minimumGapAtStartOfGraph;
    }
}
