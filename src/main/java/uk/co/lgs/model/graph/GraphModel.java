package uk.co.lgs.model.graph;

import java.util.List;

import uk.co.lgs.model.segment.graph.GraphSegment;

public interface GraphModel {

    List<GraphSegment> getGraphSegments();

    List<String> getLabels();

    int getSegmentCount();

    String getTitle();

    void append(GraphSegment start);

    void setLabels(List<String> labels);

    void setTitle(String title);

    int getLength();
}