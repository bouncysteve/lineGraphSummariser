package uk.co.lgs.model.segment.graph;

import uk.co.lgs.model.segment.graph.category.GraphSegmentCategory;

/**Models a segment of a graph, exposes the properties which are useful for summarising the segment.
 * @author bouncysteve
 *
 */
public interface GraphSegment {

	boolean isIntersecting();

	//TODO: change type (String?)
	Object getPointOfIntersection();

	GraphSegmentCategory getRecordCategory();

	boolean isParallel();

	GraphSegmentCategory getSegmentCategory();

}