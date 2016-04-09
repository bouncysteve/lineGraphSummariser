package uk.co.lgs.domain.graph;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import uk.co.lgs.domain.exception.DomainException;
import uk.co.lgs.domain.graph.iscatter.schema.Schema;
import uk.co.lgs.domain.record.Record;
import uk.co.lgs.test.AbstractTest;

public class GraphTest extends AbstractTest {

    private static String MISSING_RECORD_MESSAGE = "Graph must contain at least two data records";

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    private List<String> header;
    private Record record1, record2;

    private List<Record> records;

    @Mock
    private Schema schema;

    private void expectDomainExceptionWithMissingRecordMessage() {
        this.expectedEx.expect(DomainException.class);
        this.expectedEx.expectMessage(MISSING_RECORD_MESSAGE);
    }

    @Before
    public void setup() {
        this.records = new ArrayList<Record>();
    }

    @Test
    public void testEmptySeriesCollectionThrowsException() throws DomainException {
        expectDomainExceptionWithMissingRecordMessage();
        whenAGraphIsCreatedWithNoRecords();
    }

    @Test
    public void testNullSeriesThrowsException() throws DomainException {
        expectDomainExceptionWithMissingRecordMessage();
        whenAGraphIsCreatedWithRecords(null);
    }

    @Test
    public void testSingleRecordCollectionThrowsException() throws DomainException {
        expectDomainExceptionWithMissingRecordMessage();
        whenAGraphIsCreatedWithRecords(this.record1);
    }

    @Test
    public void testSunnyDay() throws DomainException {
        whenAGraphIsCreatedWithRecords(this.record1, this.record2);
    }

    private void whenAGraphIsCreatedWithNoRecords() throws DomainException {
        new GraphDataImpl(this.schema, this.header, this.records);
    }

    private void whenAGraphIsCreatedWithRecords(Record record) throws DomainException {
        this.records.add(record);
        new GraphDataImpl(this.schema, this.header, this.records);
    }

    private void whenAGraphIsCreatedWithRecords(Record record1, Record record2) throws DomainException {
        this.records.add(record1);
        this.records.add(record2);
        new GraphDataImpl(this.schema, this.header, this.records);
    }
}
