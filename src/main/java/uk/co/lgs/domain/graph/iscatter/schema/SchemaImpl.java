package uk.co.lgs.domain.graph.iscatter.schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.co.lgs.domain.graph.iscatter.schema.exception.SchemaException;

public class SchemaImpl implements Schema{
	
	private List<IScatterAttribute> attributes;
	
	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String DESCRIPTION = "description";
	private static final String UNIT = "unit";
	private static final String TYPE = "type";
	private static final String LEVEL = "level";
	
	public SchemaImpl(List<List<String>> inputRecords) throws SchemaException {
		attributes = new ArrayList<IScatterAttribute>();
		boolean gotHeader = false;
		for (List<String> inputRecord : inputRecords){
			if (!gotHeader){
				validateHeader(inputRecord);
				gotHeader = true;
			} else {
				attributes.add(new IScatterAttributeImpl(inputRecord));
			}
		}
	}

	private void validateHeader(List<String> inputRecord) throws SchemaException {
		List<String> expectedHeaders = Arrays.asList(ID, NAME, DESCRIPTION, UNIT, TYPE, LEVEL);
		for (int i = 0; i<expectedHeaders.size(); i++){
			if (!expectedHeaders.get(i).equalsIgnoreCase(inputRecord.get(i))){
				throw new SchemaException ("Invalid header, expected: " + expectedHeaders.get(i)
				+ " but was : " + inputRecord.get(i));
			}
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
	public List<IScatterAttribute> getAttributes(){
		return this.attributes;
	}
}
