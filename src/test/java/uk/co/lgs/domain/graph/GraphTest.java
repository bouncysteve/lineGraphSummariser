package uk.co.lgs.domain.graph;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVRecord;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import uk.co.lgs.domain.exception.DomainException;
import uk.co.lgs.domain.graph.iscatter.schema.Schema;

public class GraphTest {

	private static String MISSING_RECORD_MESSAGE = "Graph must contain at least two data records";

	private Graph underTest;

	private CSVRecord headerRecord;

	private CSVRecord record1;

	private CSVRecord record2;

	private List<CSVRecord> records;

	@Mock
	private Schema schema;

	@Before
	public void setup() {
		records = new ArrayList<CSVRecord>();
	}

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Test
	public void testSunnyDay() throws DomainException {
		whenAGraphIsCreatedWithRecords(record1, record2);
	}

	@Test
	public void testNullSeriesThrowsException() throws DomainException {
		expectDomainExceptionWithMissingRecordMessage();
		whenAGraphIsCreatedWithRecords(null);
	}

	@Test
	public void testEmptySeriesCollectionThrowsException() throws DomainException {
		expectDomainExceptionWithMissingRecordMessage();
		whenAGraphIsCreatedWithNoRecords();
	}

	@Test
	public void testSingleRecordCollectionThrowsException() throws DomainException {
		expectDomainExceptionWithMissingRecordMessage();
		whenAGraphIsCreatedWithRecords(record1);
	}

	private void expectDomainExceptionWithMissingRecordMessage() {
		expectedEx.expect(DomainException.class);
		expectedEx.expectMessage(MISSING_RECORD_MESSAGE);
	}

	private void whenAGraphIsCreatedWithRecords(CSVRecord record) throws DomainException {
		records.add(record);
		underTest = new GraphImpl(schema, headerRecord, records);
	}

	private void whenAGraphIsCreatedWithRecords(CSVRecord record1, CSVRecord record2) throws DomainException {
		records.add(record1);
		records.add(record2);
		underTest = new GraphImpl(schema, headerRecord, records);
	}

	
	private void whenAGraphIsCreatedWithNoRecords() throws DomainException {
		underTest = new GraphImpl(schema, headerRecord, records);
	}
}
