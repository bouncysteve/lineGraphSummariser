package uk.co.lgs.domain.graph.iscatter.schema;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.co.lgs.domain.graph.iscatter.schema.exception.SchemaException;

public class SchemaImplTest {

	private List <List<String>> inputRecords;
	
	private Schema underTest;
	
	@Before
	public void setup(){
		inputRecords = new ArrayList<List<String>>();
	}
	
	@Test
	public void testConstructor() throws SchemaException{
		givenAStandardSchemaHeader();
		givenARecordWithValues(Arrays.asList("myId", "myName", "myDescription", "myUnit", "string", "interval"));
		givenARecordWithValues(Arrays.asList("myId2", "myName2", "myDescription2", "myUnit2", "number", "ratio"));
		givenARecordWithValues(Arrays.asList("myId3", "myName3", "myDescription3", "myUnit3", "string", "ordinal"));
		givenARecordWithValues(Arrays.asList("myId4", "myName4", "myDescription4", "myUnit4", "number", "nominal"));
		whenICreateASchema();
		thenTheHeaderIsValid();
		thenTheSchemaContainsThisManyAttributes(4);
		thenARecordIsCreatedAtPositionWithValues(0, "myId", "myName", "myDescription", "myUnit", IScatterType.STRING, IScatterLevel.INTERVAL );
		thenARecordIsCreatedAtPositionWithValues(1, "myId2", "myName2", "myDescription2", "myUnit2", IScatterType.NUMBER, IScatterLevel.RATIO );
		thenARecordIsCreatedAtPositionWithValues(2, "myId3", "myName3", "myDescription3", "myUnit3", IScatterType.STRING, IScatterLevel.ORDINAL );
		thenARecordIsCreatedAtPositionWithValues(3, "myId4", "myName4", "myDescription4", "myUnit4", IScatterType.NUMBER, IScatterLevel.NOMINAL );
	}

	private void thenTheSchemaContainsThisManyAttributes(int i) {
		assertEquals(i, underTest.getAttributesCount());
		
	}

	private void givenAStandardSchemaHeader() {
		givenARecordWithValues(Arrays.asList("id", "name", "description", "unit", "type", "level"));
	}

	private void thenTheHeaderIsValid() {
		// TODO Auto-generated method stub
	}

	private void whenICreateASchema() throws SchemaException {
		underTest = new SchemaImpl(inputRecords);
	}

	private void givenARecordWithValues(List<String> values) {
		inputRecords.add(values);
	}
	
	private void thenARecordIsCreatedAtPositionWithValues(int position, String id, String name, String description, String unit,
			IScatterType type, IScatterLevel level) {
		IScatterAttribute attribute = underTest.getAttribute(position);
		assertEquals(id, attribute.getId());
		assertEquals(name, attribute.getName());
		assertEquals(description, attribute.getDescription());
		assertEquals(unit, attribute.getUnit());
		assertEquals(type, attribute.getType());
		assertEquals(level, attribute.getLevel());
	}
	
}
