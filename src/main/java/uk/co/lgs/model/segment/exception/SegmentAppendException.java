package uk.co.lgs.model.segment.exception;

/**
 * I am thrown when one segment cannot be appended to another due to
 * inconsistent data.
 * 
 */
public class SegmentAppendException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -4500179227539318917L;

    public SegmentAppendException(String message) {
        super(message);
    }

}
