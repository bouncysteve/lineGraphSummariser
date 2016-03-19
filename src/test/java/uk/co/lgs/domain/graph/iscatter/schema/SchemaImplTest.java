package uk.co.lgs.domain.graph.iscatter.schema;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.co.lgs.domain.graph.iscatter.schema.exception.SchemaException;
import uk.co.lgs.domain.loader.exception.LoaderException;

public class SchemaImplTest {

	private static final String INVALID_HEADER_MESSAGE = "Invalid header at position ";

	private List<List<String>> inputRecords;

	private Schema underTest;

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Before
	public void setup() {
		inputRecords = new ArrayList<List<String>>();
	}

	@Test
	public void testConstructor() throws SchemaException {
		givenAStandardSchemaHeader();
		givenARecordWithValues(Arrays.asList("myId", "myName", "myDescription", "myUnit", "string", "interval"));
		givenARecordWithValues(Arrays.asList("myId2", "myName2", "myDescription2", "myUnit2", "number", "ratio"));
		givenARecordWithValues(Arrays.asList("myId3", "myName3", "myDescription3", "myUnit3", "string", "ordinal"));
		givenARecordWithValues(Arrays.asList("myId4", "myName4", "myDescription4", "myUnit4", "number", "nominal"));
		whenICreateASchema();
		thenTheSchemaContainsThisManyAttributes(4);
		thenARecordIsCreatedAtPositionWithValues(0, "myId", "myName", "myDescription", "myUnit", IScatterType.STRING,
				IScatterLevel.INTERVAL);
		thenARecordIsCreatedAtPositionWithValues(1, "myId2", "myName2", "myDescription2", "myUnit2",
				IScatterType.NUMBER, IScatterLevel.RATIO);
		thenARecordIsCreatedAtPositionWithValues(2, "myId3", "myName3", "myDescription3", "myUnit3",
				IScatterType.STRING, IScatterLevel.ORDINAL);
		thenARecordIsCreatedAtPositionWithValues(3, "myId4", "myName4", "myDescription4", "myUnit4",
				IScatterType.NUMBER, IScatterLevel.NOMINAL);
	}

	@Test
	public void testInvalidHeader() throws SchemaException {
		givenAHeaderWithValues(Arrays.asList("id", "name", "randomHeaderName", "unit", "type", "level"));
		givenARecordWithValues(Arrays.asList("myId", "myName", "myDescription", "myUnit", "string", "interval"));
		givenARecordWithValues(Arrays.asList("myId2", "myName2", "myDescription2", "myUnit2", "number", "ratio"));
		expectAComplaintThatTheHeaderIsInvalid();
		whenICreateASchema();
	}

	@Test
	public void testAttributeWithOptionalColumnsMissing() throws SchemaException {
		givenAStandardSchemaHeader();
		givenARecordWithValues(Arrays.asList("myId", "myName", "", "", "string", "interval"));
		whenICreateASchema();
		thenTheSchemaContainsThisManyAttributes(1);
		thenARecordIsCreatedAtPositionWithValues(0, "myId", "myName", "", "", IScatterType.STRING,
				IScatterLevel.INTERVAL);
	}

	@Test
	public void testAttributeWithMissingId() throws SchemaException {
		givenAStandardSchemaHeader();
		givenARecordWithValues(Arrays.asList("", "myName", "", "", "string", "interval"));
		expectAComplaintThatTheAttributeIsInvalid();
		whenICreateASchema();
	}

	@Test
	public void testAttributeWithMissingName() throws SchemaException {
		givenAStandardSchemaHeader();
		givenARecordWithValues(Arrays.asList("myId", "", "", "", "string", "interval"));
		expectAComplaintThatTheAttributeIsInvalid();
		whenICreateASchema();
	}

	@Test
	public void testAttributeWithMissingType() throws SchemaException {
		givenAStandardSchemaHeader();
		givenARecordWithValues(Arrays.asList("myId", "myName", "", "", "", "interval"));
		expectAComplaintThatTheAttributeIsInvalid();
		whenICreateASchema();
	}

	@Test
	public void testAttributeWithMissingLevel() throws SchemaException {
		givenAStandardSchemaHeader();
		givenARecordWithValues(Arrays.asList("myId", "myName", "", "", "string", ""));
		expectAComplaintThatTheAttributeIsInvalid();
		whenICreateASchema();
	}

	@Test
	public void testAttributeWithTooManyColumns() throws SchemaException {
		givenAStandardSchemaHeader();
		givenARecordWithValues(Arrays.asList("myId", "myName", "", "", "string", "interval", "unexpectedExtraValue"));
		expectAComplaintThatTheAttributeRowIsTooLong();
		whenICreateASchema();
	}
	
	@Test
	public void testAttributeWithInvalidType() throws SchemaException {
		givenAStandardSchemaHeader();
		givenARecordWithValues(Arrays.asList("myId", "myName", "myDescription", "myUnit", "invalidType", "interval"));
		expectAComplaintThatTheTypeIsInvalid();
		whenICreateASchema();
	}



	@Test
	public void testAttributeWithInvalidLevel() throws SchemaException {
		givenAStandardSchemaHeader();
		givenARecordWithValues(Arrays.asList("myId", "myName", "myDescription", "myUnit", "number", "invalidLevel"));
		expectAComplaintThatTheLevelIsInvalid();
		whenICreateASchema();
	}
	
	private void expectAComplaintThatTheLevelIsInvalid() {
		expectedEx.expect(SchemaException.class);
		expectedEx.expectMessage("Couldn't match level:");
	}
	
	private void expectAComplaintThatTheTypeIsInvalid() {
		expectedEx.expect(SchemaException.class);
		expectedEx.expectMessage("Couldn't match type:");
	}

	private void expectAComplaintThatTheAttributeRowIsTooLong() {
		expectedEx.expect(SchemaException.class);
		expectedEx.expectMessage("Attribute has too many columns");
	}

	private void expectAComplaintThatTheAttributeIsInvalid() {
		expectedEx.expect(SchemaException.class);
		expectedEx.expectMessage("Invalid attribute");
	}

	private void expectAComplaintThatTheHeaderIsInvalid() {
		expectedEx.expect(SchemaException.class);
		expectedEx.expectMessage("Invalid header");
	}

	private void thenTheSchemaContainsThisManyAttributes(int i) {
		assertEquals(i, underTest.getAttributesCount());
	}

	private void givenAStandardSchemaHeader() {
		givenARecordWithValues(Arrays.asList("id", "name", "description", "unit", "type", "level"));
	}

	private void whenICreateASchema() throws SchemaException {
		underTest = new SchemaImpl(inputRecords);
	}

	private void givenAHeaderWithValues(List<String> values) {
		givenARecordWithValues(values);
	}

	private void givenARecordWithValues(List<String> values) {
		inputRecords.add(values);
	}

	private void thenARecordIsCreatedAtPositionWithValues(int position, String id, String name, String description,
			String unit, IScatterType type, IScatterLevel level) {
		IScatterAttribute attribute = underTest.getAttribute(position);
		assertEquals(id, attribute.getId());
		assertEquals(name, attribute.getName());
		assertEquals(description, attribute.getDescription());
		assertEquals(unit, attribute.getUnit());
		assertEquals(type, attribute.getType());
		assertEquals(level, attribute.getLevel());
	}

}
