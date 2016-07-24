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

    private List<String> labels;

    private List<String> descriptions;

    private final List<Record> records;

    private List<String> units;

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
        parseLabels(header, iScatterSchema);
        this.records = records;
        parseUnits(iScatterSchema);

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

    private void parseUnits(final IScatterSchema iScatterSchema) {
        final List<String> localUnits = new ArrayList<>();
        for (final IScatterAttribute attribute : iScatterSchema.getAttributes()) {
            final String unit = attribute.getUnit();
            if (StringUtils.isNotEmpty(unit)) {
                localUnits.add(unit);
            } else {
                localUnits.add("");
            }
        }
        this.units = localUnits;
    }

    @Override
    public int getDataRecordCount() {
        return this.records.size();
    }

    @Override
    public List<String> getLabels() {
        return this.labels;
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
        return this.labels.size() - 1;
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
        builder.append("Labels: ").append(this.getLabels()).append("\n");
        builder.append("Descriptions: ").append(this.getDescriptions()).append("\n");
        builder.append("Units ").append(this.getUnits()).append("\n");
        builder.append("Records: ").append("\n");
        for (final Record record : this.records) {
            builder.append("\t").append(record).append("\n");
        }
        builder.append("RecordCount: ").append(this.getDataRecordCount()).append("\n");
        builder.append("\n");
        return builder.toString();
    }

    private void parseLabels(final List<String> header, final IScatterSchema iScatterSchema) {
        final List<String> localLabels = new ArrayList<>();
        final List<String> localDescriptions = new ArrayList<>();
        if (null != header) {
            for (final String name : header) {
                if (null != iScatterSchema) {
                    localDescriptions.add(iScatterSchema.getDescription(name));
                    localLabels.add(name);
                }
            }
        }
        this.labels = localLabels;
        this.descriptions = localDescriptions;
    }

    @Override
    public List<String> getUnits() {
        return this.units;
    }

    @Override
    public List<String> getDescriptions() {
        return this.descriptions;
    }
}
