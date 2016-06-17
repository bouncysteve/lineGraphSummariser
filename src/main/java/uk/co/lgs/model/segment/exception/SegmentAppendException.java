package uk.co.lgs.model.segment.exception;

public class SegmentAppendException extends Exception {

    /**
     * I am thrown when one segment cannot be appended to another due to
     * inconsistent data.
     * 
     * @param message
     */
    public SegmentAppendException(String message) {
        super(message);
    }

    /**
     * 
     */
    private static final long serialVersionUID = -4500179227539318917L;

}
