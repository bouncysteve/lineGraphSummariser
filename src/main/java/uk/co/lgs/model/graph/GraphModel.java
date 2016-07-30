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

    /**
     * @return the list of segments in order.
     */
    List<GraphSegment> getGraphSegments();

    /**
     * @return the descriptive labels for each series.
     */
    List<String> getLabels();

    /**
     * @return the number of segments in the graph.
     */
    int getSegmentCount();

    /**
     * @return the graph's title.
     */
    String getTitle();

    /**
     * Used in collating, extends a segment by appending another segment at its
     * end.
     *
     * @param segment
     *            a segment to append to the end of this segment.
     */
    void append(GraphSegment segment);

    /**
     * @param labels
     *            the descriptive label for each series
     */
    void setLabels(List<String> labels);

    /**
     * @param title
     *            the graph's title
     */
    void setTitle(String title);

    /**
     * @return the length of the graph (in a collated graph this is the number
     *         of original segments, not collated segments.)
     */
    int getLength();

    /**
     * @return if any segments have been collated.
     */
    boolean isCollated();

    /**
     * @param collated
     *            if any of the segments have been collated.
     */
    void setCollated(boolean collated);

    /**
     * @return the units for each of the series.
     */
    List<String> getUnits();

    /**
     * @param units
     *            the units for each of the series.
     */
    void setUnits(List<String> units);

    /**
     * Do the series in the graph intersect at any point? (Used in deciding
     * whether to mention minimum gap)
     *
     * @return
     */
    boolean isIntersecting();

    /**
     * @param intersecting
     *            Do the series in the graph intersect at any point?
     */
    void setIntersecting(boolean intersecting);

    /**
     * @return the descriptions of each series
     */
    List<String> getDescriptions();

    /**
     * @param descriptions
     *            the descriptions of each series
     */
    void setDescriptions(List<String> descriptions);

    /**
     * @param segments
     *            the graphSegments
     */
    void setGraphSegments(List<GraphSegment> segments);

}