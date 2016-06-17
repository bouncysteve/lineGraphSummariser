package uk.co.lgs.domain.graph.iscatter.schema;

import uk.co.lgs.domain.graph.iscatter.schema.exception.SchemaException;

/**
 * I encapsulate the valid values for the Level attribute in an iScatter type
 * entry.
 * 
 * @see http://michel.wermelinger.ws/chezmichel/iscatter/
 * @author bouncysteve
 *
 */
public enum IScatterType {
    STRING("string"), NUMBER("number");

    private String name;

    private IScatterType(String name) {
        this.name = name;
    }

    public static IScatterType get(String name) throws SchemaException {
        for (IScatterType type : IScatterType.values()) {
            if (name.equalsIgnoreCase(type.name)) {
                return type;
            }
        }
        throw new SchemaException("Couldn't match type: " + name);
    }
}
