package uk.co.lgs.domain.graph;

import java.util.List;

import org.apache.commons.csv.CSVRecord;

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

	/**
	 * The title of the graph, as present in any original publication.
	 * 
	 */
	private String title;
	
	private Schema schema;

	private CSVRecord header;

	private List<CSVRecord> records;

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
	public GraphImpl(String title, Schema schema, CSVRecord header, List<CSVRecord> records) throws DomainException {
		this(schema, header, records);
		this.title = title;
	}

	public GraphImpl(Schema schema, CSVRecord header, List<CSVRecord> records) throws DomainException {
		this.schema= schema;
		this.header = header;
		this.records = records;
		
		if (null == records || records.size() < 2) {
			throw new DomainException(MISSING_RECORDS_MESSAGE);
		}
	}

	@Override
	public CSVRecord getHeader() {
		return this.header;
	}

	@Override
	public Schema getSchema() {
		return this.schema;
	}

	@Override
	public int getSeriesCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getRecordCount() {
		return this.records.size();
	}

	@Override
	public List<CSVRecord> getRecords() {
		return this.records;
	}

	public String getTitle() {
		return title;
	}
}
