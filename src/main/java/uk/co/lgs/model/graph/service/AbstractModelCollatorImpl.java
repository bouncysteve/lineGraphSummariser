package uk.co.lgs.model.graph.service;

import uk.co.lgs.model.graph.GraphModel;
import uk.co.lgs.model.graph.GraphModelImpl;
import uk.co.lgs.model.graph.collator.exception.CollatorException;
import uk.co.lgs.model.segment.exception.SegmentAppendException;
import uk.co.lgs.model.segment.graph.GraphSegment;

/**
 * Common implementation of collation algorithm. Only the criterion for
 * collation differs in concrete implementations.
 * 
 * @author bouncysteve
 *
 */
public abstract class AbstractModelCollatorImpl implements ModelCollator {

    static final String LENGTH_MISMATCH_MESSAGE = "The length of the collated model, %1$d does not match that of the original, %2$d";

    @Override
    public GraphModel collate(GraphModel model) throws CollatorException {
        int modelLength = model.getLength();
        GraphSegment segmentBeingBuilt = null;
        GraphModel collatedModel = new GraphModelImpl();
        collatedModel.setLabels(model.getLabels());
        collatedModel.setUnits(model.getUnits());
        collatedModel.setTitle(model.getTitle());
        collatedModel.setCollated(false);
        for (GraphSegment segment : model.getGraphSegments()) {
            if (null == segmentBeingBuilt) {
                // Initialise the first segment
                segmentBeingBuilt = segment;
            } else if (!intersecting(segmentBeingBuilt, segment) && shouldCollate(segmentBeingBuilt, segment)) {
                // Combine the current and incoming segments into one.
                collatedModel.setCollated(true);
                try {
                    segmentBeingBuilt = segmentBeingBuilt.append(segment);
                } catch (SegmentAppendException e) {
                    throw new CollatorException(e);
                }
            } else {
                // not collating, so add the current segment to the model and
                // set the incoming segment as the new current one.
                collatedModel.append(segmentBeingBuilt);
                segmentBeingBuilt = segment;
            }
        }
        if (null != segmentBeingBuilt) {
            collatedModel.append(segmentBeingBuilt);
        }
        if (collatedModel.getLength() != modelLength) {

            throw new CollatorException(String.format(LENGTH_MISMATCH_MESSAGE, collatedModel.getLength(), modelLength));
        }
        return collatedModel;
    }

    private boolean intersecting(GraphSegment segmentBeingBuilt, GraphSegment segment) {
        return segmentBeingBuilt.isIntersecting() || segment.isIntersecting();
    }

    protected abstract boolean shouldCollate(GraphSegment segmentBeingBuilt, GraphSegment segment);
}
