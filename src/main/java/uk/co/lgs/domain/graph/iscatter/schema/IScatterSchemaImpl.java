package uk.co.lgs.domain.graph.iscatter.schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import uk.co.lgs.domain.graph.iscatter.schema.exception.IScatterSchemaException;

public class IScatterSchemaImpl implements IScatterSchema {

    private List<IScatterAttribute> attributes;
    private List<String> expectedHeaders;
    private List<String> mandatoryAttributeValues;

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String UNIT = "unit";
    private static final String TYPE = "type";
    private static final String LEVEL = "level";

    public IScatterSchemaImpl(List<List<String>> schemaRows) throws IScatterSchemaException {
        this.attributes = new ArrayList<>();
        this.expectedHeaders = Arrays.asList(ID, NAME, DESCRIPTION, UNIT, TYPE, LEVEL);
        this.mandatoryAttributeValues = Arrays.asList(ID, NAME, TYPE, LEVEL);
        validateHeader(schemaRows.remove(0));
        processAndValidate(schemaRows);
    }

    private void validateHeader(List<String> inputRow) throws IScatterSchemaException {
        for (int i = 0; i < this.expectedHeaders.size(); i++) {
            if (!this.expectedHeaders.get(i).equalsIgnoreCase(inputRow.get(i))) {
                throw new IScatterSchemaException(
                        "Invalid header, expected: " + this.expectedHeaders.get(i) + " but was : " + inputRow.get(i));
            }
        }
    }

    private void processAndValidate(List<List<String>> inputRecords) throws IScatterSchemaException {
        for (List<String> inputRecord : inputRecords) {
            checkRecordSize(inputRecord);
            for (String mandatoryAttributeValue : this.mandatoryAttributeValues) {
                String value = inputRecord.get(this.expectedHeaders.indexOf(mandatoryAttributeValue));
                if (StringUtils.isEmpty(value)) {
                    throw new IScatterSchemaException("Invalid attribute, missing mandatory value: " + mandatoryAttributeValue);
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

    private void checkRecordSize(List<String> inputRecord) throws IScatterSchemaException {
        if (inputRecord.size() > this.expectedHeaders.size()) {
            throw new IScatterSchemaException("Attribute has too many columns");
        }
    }

    @Override
    public int getAttributesCount() {
        return this.attributes.size();
    }

    @Override
    public IScatterAttribute getAttribute(int position) {
        return this.attributes.get(position);
    }

    @Override
    public List<IScatterAttribute> getAttributes() {
        return this.attributes;
    }

    @Override
    public String getDescription(String id) {
        if (null != id) {
            for (IScatterAttribute attribute : this.attributes) {
                if (id.equalsIgnoreCase(attribute.getId())) {
                    return attribute.getDescription();
                }
            }
        }
        return null;
    }

    @Override
    public String getUnit(String id) {
        if (null != id) {
            for (IScatterAttribute attribute : this.attributes) {
                if (id.equalsIgnoreCase(attribute.getId())) {
                    return attribute.getUnit();
                }
            }
        }
        return null;
    }
}