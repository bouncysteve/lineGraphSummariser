package uk.co.lgs.model.segment.exception;

import uk.co.lgs.model.graph.collator.exception.CollatorException;

/**
 * I am thrown when a segment category enum instance cannot be matched to the
 * values passed.
 * 
 * @author bouncysteve
 *
 */
public class SegmentCategoryNotFoundException extends CollatorException {
    /**
     * 
     */
    private static final long serialVersionUID = -1910128374065199653L;

    public SegmentCategoryNotFoundException(SegmentAppendException e) {
        super(e);
    }

    public SegmentCategoryNotFoundException(String string) {
        super(string);
    }

}
