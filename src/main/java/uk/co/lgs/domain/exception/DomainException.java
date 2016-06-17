package uk.co.lgs.domain.exception;

/**
 * I am the exception thrown when domain objects cannot be created or
 * manipulated.
 * 
 * @author bouncysteve
 *
 */
public class DomainException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -4483142498130081218L;

    private final String message;

    public DomainException(String message) {
        super();
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

}
