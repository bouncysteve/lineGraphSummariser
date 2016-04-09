package uk.co.lgs.domain.graph.iscatter.schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import uk.co.lgs.domain.graph.iscatter.schema.exception.SchemaException;

public class SchemaImpl implements Schema {

    private List<IScatterAttribute> attributes;
    private List<String> expectedHeaders;
    private List<String> mandatoryAttributeValues;

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String UNIT = "unit";
    private static final String TYPE = "type";
    private static final String LEVEL = "level";

    public SchemaImpl(List<List<String>> schemaRows) throws SchemaException {
        this.attributes = new ArrayList<IScatterAttribute>();
        this.expectedHeaders = Arrays.asList(ID, NAME, DESCRIPTION, UNIT, TYPE, LEVEL);
        this.mandatoryAttributeValues = Arrays.asList(ID, NAME, TYPE, LEVEL);
        validateHeader(schemaRows.remove(0));
        processAndValidate(schemaRows);
    }

    private void validateHeader(List<String> inputRow) throws SchemaException {
        for (int i = 0; i < this.expectedHeaders.size(); i++) {
            if (!this.expectedHeaders.get(i).equalsIgnoreCase(inputRow.get(i))) {
                throw new SchemaException("Invalid header, expected: " + this.expectedHeaders.get(i) + " but was : "
                        + inputRow.get(i));
            }
        }
    }

    private void processAndValidate(List<List<String>> inputRecords) throws SchemaException {
        for (List<String> inputRecord : inputRecords) {
            if (inputRecord.size() > this.expectedHeaders.size()) {
                throw new SchemaException("Attribute has too many columns");
            }
            for (String mandatoryAttributeValue : this.mandatoryAttributeValues) {
                String value = inputRecord.get(this.expectedHeaders.indexOf(mandatoryAttributeValue));
                if (StringUtils.isEmpty(value)) {
                    throw new SchemaException("Invalid attribute, missing mandatory value: " + mandatoryAttributeValue);
                } else {
                    if (TYPE.equals(mandatoryAttributeValue)) {
                        IScatterType.get(value);
                    } else {
                        if (LEVEL.equals(mandatoryAttributeValue)) {
                            IScatterLevel.get(value);
                        }
                    }
                }
            }
            this.attributes.add(new IScatterAttributeImpl(inputRecord));
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
}
