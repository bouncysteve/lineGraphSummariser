package uk.co.lgs.domain.graph.iscatter.schema;

import java.util.ArrayList;
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

	private void validateHeader(List<String> inputRecord) {
		int i=0;
		assert(ID.equalsIgnoreCase(inputRecord.get(i++)));
		assert(NAME.equalsIgnoreCase(inputRecord.get(i++)));
		assert(DESCRIPTION.equalsIgnoreCase(inputRecord.get(i++)));
		assert(UNIT.equalsIgnoreCase(inputRecord.get(i++)));
		assert(TYPE.equalsIgnoreCase(inputRecord.get(i++)));
		assert(LEVEL.equalsIgnoreCase(inputRecord.get(i++)));
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
