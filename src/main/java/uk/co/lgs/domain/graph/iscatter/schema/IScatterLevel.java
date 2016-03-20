package uk.co.lgs.domain.graph.iscatter.schema;

import uk.co.lgs.domain.graph.iscatter.schema.exception.SchemaException;

public enum IScatterLevel {

    NOMINAL("nominal"), ORDINAL("ordinal"), INTERVAL("interval"), RATIO("ratio");

    private String name;

    private IScatterLevel(String name) {
        this.name = name;
    }

    public static IScatterLevel get(String name) throws SchemaException {
        for (IScatterLevel type : IScatterLevel.values()) {
            if (name.equalsIgnoreCase(type.name)) {
                return type;
            }
        }
        throw new SchemaException("Couldn't match level: " + name);
    }
}