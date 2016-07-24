package uk.co.lgs.domain.graph.iscatter.schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import uk.co.lgs.domain.graph.iscatter.schema.exception.IScatterSchemaException;

public class IScatterSchemaImpl implements IScatterSchema {

    private final List<IScatterAttribute> attributes;
    private final List<String> expectedHeaders;
    private final List<String> mandatoryAttributeValues;

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String UNIT = "unit";
    private static final String TYPE = "type";
    private static final String LEVEL = "level";

    public IScatterSchemaImpl(final List<List<String>> schemaRows) throws IScatterSchemaException {
        this.attributes = new ArrayList<>();
        this.expectedHeaders = Arrays.asList(ID, NAME, DESCRIPTION, UNIT, TYPE, LEVEL);
        this.mandatoryAttributeValues = Arrays.asList(ID, NAME, TYPE, LEVEL);
        validateHeader(schemaRows.remove(0));
        processAndValidate(schemaRows);
    }

    private void validateHeader(final List<String> inputRow) throws IScatterSchemaException {
        for (int i = 0; i < this.expectedHeaders.size(); i++) {
            if (!this.expectedHeaders.get(i).equalsIgnoreCase(inputRow.get(i))) {
                throw new IScatterSchemaException(
                        "Invalid header, expected: " + this.expectedHeaders.get(i) + " but was : " + inputRow.get(i));
            }
        }
    }

    private void processAndValidate(final List<List<String>> inputRecords) throws IScatterSchemaException {
        for (final List<String> inputRecord : inputRecords) {
            checkRecordSize(inputRecord);
            for (final String mandatoryAttributeValue : this.mandatoryAttributeValues) {
                final String value = inputRecord.get(this.expectedHeaders.indexOf(mandatoryAttributeValue));
                if (StringUtils.isEmpty(value)) {
                    throw new IScatterSchemaException(
                            "Invalid attribute, missing mandatory value: " + mandatoryAttributeValue);
                }
                if (TYPE.equals(mandatoryAttributeValue)) {
                    IScatterType.get(value);
                    continue;
                }
                if (LEVEL.equals(mandatoryAttributeValue)) {
                    IScatterLevel.get(value);
                }
            }
            this.attributes.add(new IScatterAttributeImpl(inputRecord));
        }
    }

    private void checkRecordSize(final List<String> inputRecord) throws IScatterSchemaException {
        if (inputRecord.size() > this.expectedHeaders.size()) {
            throw new IScatterSchemaException("Attribute has too many columns");
        }
    }

    @Override
    public int getAttributesCount() {
        return this.attributes.size();
    }

    @Override
    public IScatterAttribute getAttribute(final int position) {
        return this.attributes.get(position);
    }

    @Override
    public List<IScatterAttribute> getAttributes() {
        return this.attributes;
    }

    @Override
    public String getDescription(final String name) {
        if (null != name) {
            for (final IScatterAttribute attribute : this.attributes) {
                if (name.equalsIgnoreCase(attribute.getName())) {
                    return attribute.getDescription();
                }
            }
        }
        return null;
    }

    @Override
    public String getUnit(final String name) {
        if (null != name) {
            for (final IScatterAttribute attribute : this.attributes) {
                if (name.equalsIgnoreCase(attribute.getName())) {
                    return attribute.getUnit();
                }
            }
        }
        return null;
    }
}
