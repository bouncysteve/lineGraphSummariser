package uk.co.lgs.text.service.segment.graph;

import simplenlg.phrasespec.SPhraseSpec;
import uk.co.lgs.model.segment.graph.GraphSegment;

/**
 * I am responsible for describing the length of the graph segment (i.e. what
 * are the start and end times?).
 * 
 * @author bouncysteve
 *
 */
public interface SegmentDurationDescriberService {

    /**
     * I provide a random phrase decribing a timespan, e.g. "From t1 to t2..."
     * or "During the period from t1 to t2..."
     * 
     * @param graphSegment
     *            - The segment to summarise
     * @return A SPhraseSpec, to be combined with the rest of the description of
     *         the time period.
     */
    SPhraseSpec buildDurationDescription(GraphSegment graphSegment);

    /**
     * By default the service chooses phrases at random. This can be disabled
     * for unit testing.
     * 
     * @param randomise
     */
    void setRandomise(boolean randomise);

}
