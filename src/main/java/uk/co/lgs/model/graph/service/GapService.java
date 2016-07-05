package uk.co.lgs.model.graph.service;

import java.util.List;

import uk.co.lgs.model.segment.graph.GraphSegment;

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

}
