package uk.co.lgs.domain.loader.exception;

/**
 * I am thrown when a graph cannot be constructed from the underlying data.
 * 
 * @author bouncysteve
 *
 */
public class LoaderException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1966104956853845825L;

    private String message;

    public LoaderException(String message) {
        super(message);
        this.message = message;
    }

    public LoaderException(String message, Exception e) {
        super(message, e);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
