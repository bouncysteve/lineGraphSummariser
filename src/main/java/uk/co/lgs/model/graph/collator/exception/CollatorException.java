package uk.co.lgs.model.graph.collator.exception;

import uk.co.lgs.model.segment.exception.SegmentAppendException;

/**
 * I am thrown when two segments (graphSegments or seriesSegments) should
 * collate but for some reason don't.
 * 
 * @author bouncysteve
 *
 */
public class CollatorException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1294210467737864976L;

    public CollatorException(String message) {
        super(message);
    }

    public CollatorException(SegmentAppendException e) {
        super(e);
    }

}
