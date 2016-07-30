package uk.co.lgs.model.graph.service;

import org.springframework.beans.factory.annotation.Autowired;

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

    @Autowired
    GapService gapService;

    @Override
    public GraphModel collate(final GraphModel model) throws CollatorException {
        final int modelLength = model.getLength();
        GraphSegment segmentBeingBuilt = null;
        final GraphModel collatedModel = new GraphModelImpl();
        collatedModel.setLabels(model.getLabels());
        collatedModel.setUnits(model.getUnits());
        collatedModel.setTitle(model.getTitle());
        collatedModel.setCollated(false);
        for (final GraphSegment segment : model.getGraphSegments()) {
            if (null == segmentBeingBuilt) {
                // Initialise the first segment
                segmentBeingBuilt = segment;
            } else if (!isIntersecting(segmentBeingBuilt, segment) && shouldCollate(segmentBeingBuilt, segment)) {
                // Combine the current and incoming segments into one.
                collatedModel.setCollated(true);
                try {
                    segmentBeingBuilt = segmentBeingBuilt.append(segment);
                } catch (final SegmentAppendException e) {
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
        collatedModel.setGraphSegments(this.gapService.addGapInfo(collatedModel.getGraphSegments()));
        return collatedModel;
    }

    private boolean isIntersecting(final GraphSegment segmentBeingBuilt, final GraphSegment segment) {
        return segmentBeingBuilt.isIntersecting() || segment.isIntersecting();
    }

    protected abstract boolean shouldCollate(GraphSegment segmentBeingBuilt, GraphSegment segment);
}
