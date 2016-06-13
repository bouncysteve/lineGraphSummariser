package uk.co.lgs.domain.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.co.lgs.domain.exception.DomainException;
import uk.co.lgs.domain.graph.iscatter.schema.Schema;
import uk.co.lgs.domain.graph.iscatter.schema.SchemaImpl;
import uk.co.lgs.domain.graph.iscatter.schema.exception.SchemaException;
import uk.co.lgs.domain.record.Record;
import uk.co.lgs.test.AbstractTest;

public class GraphTest extends AbstractTest {

    private static final List<String> LABELS = Arrays.asList("time in years", "series1Description",
            "series2Description");
    private static String MISSING_RECORD_MESSAGE = "Graph must contain at least two data records";
    private static List<String> UNITS = Arrays.asList("year", "series1Units", "series2Units");

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    private List<String> header;
    private Record record1, record2;

    private List<Record> records;

    private GraphData graphData;

    private Schema schema;
    private List<List<String>> schemaRows;

    private void expectDomainExceptionWithMissingRecordMessage() {
        this.expectedEx.expect(DomainException.class);
        this.expectedEx.expectMessage(MISSING_RECORD_MESSAGE);
    }

    @Before
    public void setup() throws SchemaException {
        this.records = new ArrayList<>();
        this.header = Arrays.asList("timeId", "series1Id", "series2Id");
        List<String> schemaHeader = Arrays.asList("id", "name", "description", "unit", "type", "level");
        List<String> timeSchema = Arrays.asList("timeId", "Time", "time in years", "year", "string", "nominal");
        List<String> series1Schema = Arrays.asList("series1Id", "series1Name", "series1Description", "series1Units",
                "string", "nominal");
        List<String> series2Schema = Arrays.asList("series2Id", "series2Name", "series2Description", "series2Units",
                "string", "nominal");
        this.schemaRows = new ArrayList<>();
        for (List<String> schemaRow : Arrays.asList(schemaHeader, timeSchema, series1Schema, series2Schema)) {
            this.schemaRows.add(schemaRow);
        }
        this.schema = new SchemaImpl(this.schemaRows);
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
        assertEquals(this.records.size(), this.graphData.getDataRecordCount());
        assertEquals(LABELS, this.graphData.getHeader());
        assertEquals(this.records, this.graphData.getRecords());
        assertEquals(this.schema, this.graphData.getSchema());
        assertEquals(this.schemaRows.size(), this.graphData.getSchemaAttributeCount());
        assertEquals(this.schemaRows.size() - 1, this.graphData.getSeriesCount());
        assertNull(this.graphData.getTitle());
        assertEquals(UNITS, this.graphData.getUnits());
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
        this.graphData = new GraphDataImpl(this.schema, this.header, this.records);
    }
}
