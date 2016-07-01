package uk.co.lgs.domain.graph;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import uk.co.lgs.domain.exception.DomainException;
import uk.co.lgs.domain.graph.iscatter.schema.IScatterAttribute;
import uk.co.lgs.domain.graph.iscatter.schema.IScatterSchema;
import uk.co.lgs.domain.record.Record;

/**
 * I represent the graph as initially loaded from the data file(s).
 *
 * @author bouncysteve
 *
 */
public class GraphDataImpl implements GraphData {

    private static final String MISSING_RECORDS_MESSAGE = "Graph must contain at least two data records";

    private final List<String> header;

    private final List<Record> records;

    private final List<String> units;

    private final IScatterSchema iScatterSchema;

    /**
     * The title of the graph, as present in any original publication.
     *
     */
    private String title;

    /**
     * @param records
     *            The collection of data records which are displayed in the
     *            graph.
     * @param iScatterSchema
     *            the schema from schema.csv
     * @param header
     *            the first row of data.csv
     * @throws DomainException
     *             if the title is missing or empty, or if at least two data
     *             series are not present.
     *
     */
    public GraphDataImpl(final IScatterSchema iScatterSchema, final List<String> header, final List<Record> records)
            throws DomainException {
        this.iScatterSchema = iScatterSchema;
        this.header = parseLabels(header, iScatterSchema);
        this.records = records;
        this.units = parseUnits(iScatterSchema);

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
     * @param iScatterSchema
     *            the schema from schema.csv
     * @param header
     *            the first row of data.csv
     * @throws DomainException
     *             if the title is missing or empty, or if at least two data
     *             series are not present.
     *
     */
    public GraphDataImpl(final String title, final IScatterSchema iScatterSchema, final List<String> header,
            final List<Record> records) throws DomainException {
        this(iScatterSchema, header, records);
        this.title = title;
    }

    private List<String> parseUnits(final IScatterSchema iScatterSchema) {
        final List<String> localUnits = new ArrayList<>();
        for (final IScatterAttribute attribute : iScatterSchema.getAttributes()) {
            final String unit = attribute.getUnit();
            if (StringUtils.isNotEmpty(unit)) {
                localUnits.add(unit);
            } else {
                localUnits.add("");
            }
        }
        return localUnits;
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
    public IScatterSchema getSchema() {
        return this.iScatterSchema;
    }

    @Override
    public int getSchemaAttributeCount() {
        return this.iScatterSchema.getAttributesCount();
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
        final StringBuilder builder = new StringBuilder();
        builder.append("\n");
        builder.append("***************GRAPH DOMAIN OBJECT (RAW DATA)***************").append("\n");
        builder.append("Title: ").append(this.getTitle()).append("\n");
        builder.append("Series Count: ").append(this.getSeriesCount()).append("\n");
        builder.append("Header: ").append(this.getHeader()).append("\n");
        builder.append("Units ").append(this.getUnits()).append("\n");
        builder.append("Records: ").append("\n");
        for (final Record record : this.records) {
            builder.append("\t").append(record).append("\n");
        }
        builder.append("RecordCount: ").append(this.getDataRecordCount()).append("\n");
        builder.append("\n");
        return builder.toString();
    }

    private List<String> parseLabels(final List<String> header, final IScatterSchema iScatterSchema) {
        final List<String> labels = new ArrayList<>();
        if (null != header) {
            for (final String seriesId : header) {
                String description = "";
                if (null != iScatterSchema) {
                    description = iScatterSchema.getDescription(seriesId);
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
