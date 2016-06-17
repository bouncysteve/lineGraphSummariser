package uk.co.lgs.model.graph;

import java.util.List;

import uk.co.lgs.model.segment.graph.GraphSegment;

/**
 * I am a model object representing the entire graph in terms of a series of
 * segments. I expose all meta-data about the graph, such as title, series
 * labels, etc.
 * 
 * @author bouncysteve
 *
 */
public interface GraphModel {

    List<GraphSegment> getGraphSegments();

    List<String> getLabels();

    int getSegmentCount();

    String getTitle();

    void append(GraphSegment start);

    void setLabels(List<String> labels);

    void setTitle(String title);

    int getLength();

    boolean isCollated();

    void setCollated(boolean collated);

    List<String> getUnits();

    void setUnits(List<String> units);
}