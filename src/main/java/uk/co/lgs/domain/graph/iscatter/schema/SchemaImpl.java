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

	public SchemaImpl(List<List<String>> inputRecords) throws SchemaException {
		attributes = new ArrayList<IScatterAttribute>();
		expectedHeaders = Arrays.asList(ID, NAME, DESCRIPTION, UNIT, TYPE, LEVEL);
		mandatoryAttributeValues = Arrays.asList(ID, NAME, TYPE, LEVEL);
		validateHeader(inputRecords.remove(0));
		processAndValidate(inputRecords);
	}

	private void validateHeader(List<String> inputRecord) throws SchemaException {
		for (int i = 0; i < expectedHeaders.size(); i++) {
			if (!expectedHeaders.get(i).equalsIgnoreCase(inputRecord.get(i))) {
				throw new SchemaException(
						"Invalid header, expected: " + expectedHeaders.get(i) + " but was : " + inputRecord.get(i));
			}
		}
	}

	private void processAndValidate(List<List<String>> inputRecords) throws SchemaException{
		for (List<String> inputRecord: inputRecords){
			for (String mandatoryAttributeValue: mandatoryAttributeValues){
				if (StringUtils.isEmpty(inputRecord.get(expectedHeaders.indexOf(mandatoryAttributeValue)))){
					throw new SchemaException ("Invalid attribute, missing mandatory value: " + mandatoryAttributeValue);
				}
			}
			attributes.add(new IScatterAttributeImpl(inputRecord));
		}
	}

	@Override
	public int getAttributesCount() {
		return attributes.size();
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
