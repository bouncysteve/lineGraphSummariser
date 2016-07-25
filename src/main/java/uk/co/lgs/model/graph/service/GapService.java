package uk.co.lgs.model.graph.service;

import java.util.List;

import uk.co.lgs.model.segment.graph.GraphSegment;

/**
 * Overlays gap information onto the graphModel and graphSegment objects
 *
 * @author bouncysteve
 *
 */
public interface GapService {

    /**
     * Parses the list of graphSegments twice, once looking for the
     * maximum/minimum gap values, and again to decorate the graphSegments with
     * gap information.
     *
     * @param graphSegments
     * @return
     */
    List<GraphSegment> addGapInfo(List<GraphSegment> graphSegments);

    /**
     * Parses the list of graphSegments and determines if the largest difference
     * between the series is actually between the two initial points, This is a
     * special case as the gaps are usually considered only at the ends of the
     * segments.
     *
     * @param graphSegments
     * @return
     */
    boolean isGlobalMaximumAtGraphStart(List<GraphSegment> graphSegments);

    /**
     * Parses the list of graphSegments and determines if the smallest
     * difference between the series is actually between the two initial points,
     * This is a special case as the gaps are usually considered only at the
     * ends of the segments.
     *
     * @param graphSegments
     * @return
     */
    boolean isGlobalMinimumAtGraphStart(List<GraphSegment> graphSegments);

}
