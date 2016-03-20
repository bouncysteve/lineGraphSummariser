package uk.co.lgs.domain.graph;

import java.util.List;

import uk.co.lgs.domain.exception.DomainException;
import uk.co.lgs.domain.graph.iscatter.schema.Schema;

/**
 * Domain class representing a multi-series line graph. A graph consists of a
 * title, and multiple records. From the records the series can be derived.
 * 
 * @author bouncysteve
 *
 */
public class GraphImpl implements Graph {

    private static String MISSING_RECORDS_MESSAGE = "Graph must contain at least two data records";

    private List<String> header;

    private List<List<String>> records;

    private Schema schema;

    /**
     * The title of the graph, as present in any original publication.
     * 
     */
    private String title;

    public GraphImpl(Schema schema, List<List<String>> records) throws DomainException {
        this(schema, records.remove(0), records);
    }

    public GraphImpl(Schema schema, List<String> header, List<List<String>> records) throws DomainException {
        this.schema = schema;
        this.header = header;
        this.records = records;

        if (null == records || records.size() < 2) {
            throw new DomainException(MISSING_RECORDS_MESSAGE);
        }
    }

    /**
     * @param title
     *            The graph title (as present in the original presentation of
     *            the graph.
     * @param records
     *            The collection of data records which are displayed in the
     *            graph.
     * @throws DomainException
     *             if the title is missing or empty, or if at least two data
     *             series are not present.
     */
    public GraphImpl(String title, Schema schema, List<String> header, List<List<String>> records)
            throws DomainException {
        this(schema, header, records);
        this.title = title;
    }

    @Override
    public int getDataRecordCount() {
        return this.records.size();
    }

    @Override
    public List<String> getHeader() {
        return this.header;
    }

    @Override
    public List<List<String>> getRecords() {
        return this.records;
    }

    @Override
    public Schema getSchema() {
        return this.schema;
    }

    @Override
    public int getSchemaAttributeCount() {
        return this.schema.getAttributesCount();
    }

    @Override
    public int getSeriesCount() {
        return this.header.size() - 1;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        builder.append("***************GRAPH DOMAIN OBJECT (RAW DATA)***************").append("\n");
        builder.append("Title: ").append(this.getTitle()).append("\n");
        builder.append("Series: ").append(this.getSeriesCount()).append("\n");
        builder.append("Header: ").append(this.getHeader()).append("\n");
        builder.append("Records: ").append(this.getRecords().toString()).append("\n");
        builder.append("RecordCount: ").append(this.getDataRecordCount()).append("\n");
        builder.append("\n");
        return builder.toString();
    }
}
