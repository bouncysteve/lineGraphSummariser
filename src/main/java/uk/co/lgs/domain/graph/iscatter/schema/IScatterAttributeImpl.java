package uk.co.lgs.domain.graph.iscatter.schema;

import java.util.List;

import uk.co.lgs.domain.graph.iscatter.schema.exception.IScatterSchemaException;

public class IScatterAttributeImpl implements IScatterAttribute {

    private IScatterLevel level;
    private IScatterType type;
    private String unit;
    private String description;
    private String name;
    private String id;

    public IScatterAttributeImpl(List<String> inputRecord) throws IScatterSchemaException {
        int i = 0;
        this.id = inputRecord.get(i++);
        this.name = inputRecord.get(i++);
        this.description = inputRecord.get(i++);
        this.unit = inputRecord.get(i++);
        this.type = IScatterType.get(inputRecord.get(i++));
        this.level = IScatterLevel.get(inputRecord.get(i++));
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getUnit() {
        return this.unit;
    }

    @Override
    public IScatterType getType() {
        return this.type;
    }

    @Override
    public IScatterLevel getLevel() {
        return this.level;
    }

}
