package uk.co.lgs.model.graph;

import java.util.List;

import uk.co.lgs.model.segment.graph.GraphSegment;

public class GraphImpl {

    List<GraphSegment> graphSegments;

    public int getSegmentCount() {
        return this.graphSegments.size();
    }
}
