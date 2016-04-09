package uk.co.lgs.model.graph.service;

import org.springframework.stereotype.Component;

import uk.co.lgs.model.graph.GraphModel;
import uk.co.lgs.model.graph.GraphModelImpl;
import uk.co.lgs.model.graph.collator.exception.CollatorException;
import uk.co.lgs.model.segment.exception.SegmentAppendException;
import uk.co.lgs.model.segment.exception.SegmentCategoryNotFoundException;
import uk.co.lgs.model.segment.graph.GraphSegment;

@Component
public class ModelCollatorImpl implements ModelCollator {

    private static final String LENGTH_MISMATCH_MESSAGE = "The length of the collated model, %1$d does not match that of the original, %2$d";

    @Override
    public GraphModel collate(GraphModel model) throws SegmentCategoryNotFoundException, CollatorException {
        int modelLength = model.getLength();
        GraphSegment segmentBeingBuilt = null;
        GraphModel collatedModel = new GraphModelImpl();
        collatedModel.setLabels(model.getLabels());
        collatedModel.setTitle(model.getTitle());
        collatedModel.setCollated(true);
        for (GraphSegment segment : model.getGraphSegments()) {
            if (null == segmentBeingBuilt) {
                segmentBeingBuilt = segment;
            } else if (segmentBeingBuilt.getSegmentCategory().equals(segment.getSegmentCategory())) {
                // TODO: how to handle intersections?
                try {
                    segmentBeingBuilt = segmentBeingBuilt.append(segment);
                } catch (SegmentAppendException e) {
                    throw new CollatorException(e);
                }
            } else {
                if (null != segmentBeingBuilt) {
                    collatedModel.append(segmentBeingBuilt);
                    segmentBeingBuilt = segment;
                }
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

}
