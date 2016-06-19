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
public class IScatterSchemaException extends Exception {

    public IScatterSchemaException(String string) {
        super(string);
    }

    public IScatterSchemaException(String string, AssertionError e) {
        super(string, e);
    }

    /**
     * 
     */
    private static final long serialVersionUID = -5478701897964206097L;

}
