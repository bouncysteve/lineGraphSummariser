package uk.co.lgs.domain.graph;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import uk.co.lgs.domain.exception.DomainException;
import uk.co.lgs.domain.graph.iscatter.schema.Schema;
import uk.co.lgs.domain.record.Record;

/**
 * Domain class representing a multi-series line graph. A graph consists of a
 * title, and multiple records. From the records the series can be derived.
 * 
 * @author bouncysteve
 *
 */
public class GraphDataImpl implements GraphData {

    private static String MISSING_RECORDS_MESSAGE = "Graph must contain at least two data records";

    private List<String> header;

    private List<Record> records;

    private List<String> units;

    private Schema schema;

    /**
     * The title of the graph, as present in any original publication.
     * 
     */
    private String title;

    public GraphDataImpl(Schema schema, List<String> header, List<Record> records) throws DomainException {
        this.schema = schema;
        this.header = parseLabels(header, schema);
        this.records = records;
        this.units = parseUnits(header, schema);

        if (null == records || records.size() < 2) {
            throw new DomainException(MISSING_RECORDS_MESSAGE);
        }
    }

    private List<String> parseUnits(List<String> header, Schema schema) {
        List<String> units = new ArrayList<>();
        if (null != header) {
            for (String seriesId : header) {
                String unit = "";
                if (null != schema) {
                    unit = schema.getUnit(seriesId);
                }
                if (StringUtils.isNotEmpty(unit)) {
                    units.add(unit);
                } else {
                    units.add("");
                }
            }
        }
        return units;
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
    public GraphDataImpl(String title, Schema schema, List<String> header, List<Record> records)
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
    public List<Record> getRecords() {
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
        builder.append("Series Count: ").append(this.getSeriesCount()).append("\n");
        builder.append("Header: ").append(this.getHeader()).append("\n");
        builder.append("Units ").append(this.getUnits()).append("\n");
        builder.append("Records: ").append("\n");
        for (Record record : this.records) {
            builder.append("\t").append(record).append("\n");
        }
        builder.append("RecordCount: ").append(this.getDataRecordCount()).append("\n");
        builder.append("\n");
        return builder.toString();
    }

    private List<String> parseLabels(List<String> header, Schema schema) {
        List<String> labels = new ArrayList<>();
        if (null != header) {
            for (String seriesId : header) {
                String description = "";
                if (null != schema) {
                    description = schema.getDescription(seriesId);
                }
                if (StringUtils.isNotEmpty(description)) {
                    labels.add(description);
                } else {
                    labels.add(seriesId);
                }
            }
        }
        return labels;
    }

    @Override
    public List<String> getUnits() {
        return this.units;
    }
}
