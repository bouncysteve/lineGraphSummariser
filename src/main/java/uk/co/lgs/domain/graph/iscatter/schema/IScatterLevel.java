package uk.co.lgs.domain.graph.iscatter.schema;

import uk.co.lgs.domain.graph.iscatter.schema.exception.IScatterSchemaException;

/**
 * I encapsulate the valid values for the Level attribute in an iScatter schema
 * entry.
 * 
 * @see http://michel.wermelinger.ws/chezmichel/iscatter/
 * @author bouncysteve
 *
 */
public enum IScatterLevel {

    NOMINAL("nominal"), ORDINAL("ordinal"), INTERVAL("interval"), RATIO("ratio");

    private String name;

    private IScatterLevel(String name) {
        this.name = name;
    }

    public static IScatterLevel get(String name) throws IScatterSchemaException {
        for (IScatterLevel type : IScatterLevel.values()) {
            if (name.equalsIgnoreCase(type.name)) {
                return type;
            }
        }
        throw new IScatterSchemaException("Couldn't match level: " + name);
    }
}