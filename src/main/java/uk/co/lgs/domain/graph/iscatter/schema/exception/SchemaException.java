package uk.co.lgs.domain.graph.iscatter.schema.exception;

/**
 * I am thrown if there is a problem constructing a schema object from the
 * schema.csv file.
 * 
 * @see http://michel.wermelinger.ws/chezmichel/iscatter/
 * 
 * @author bouncysteve
 *
 */
public class SchemaException extends Exception {

    public SchemaException(String string) {
        super(string);
    }

    public SchemaException(String string, AssertionError e) {
        super(string, e);
    }

    /**
     * 
     */
    private static final long serialVersionUID = -5478701897964206097L;

}
