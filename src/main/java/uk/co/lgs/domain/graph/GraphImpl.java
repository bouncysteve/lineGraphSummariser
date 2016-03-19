package uk.co.lgs.domain.graph;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import uk.co.lgs.domain.exception.DomainException;
import uk.co.lgs.domain.record.RecordImpl;

/**
 * Domain class representing a multi-series line graph. A graph consists of a
 * title, and multiple records. From the records the series can be derived.
 * 
 * @author bouncysteve
 *
 */
public class GraphImpl implements Graph {

	private static String MISSING_TITLE_MESSAGE = "Graph without title";
	private static String MISSING_SERIES_MESSAGE = "Graph must contain at least two data series";

	/**
	 * The title of the graph, as present in any original publication.
	 * 
	 */
	private String title;

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
	public GraphImpl(String title, List<RecordImpl> records) throws DomainException {
		super();
		if (StringUtils.isEmpty(title)) {
			throw new DomainException(MISSING_TITLE_MESSAGE);
		}
		this.title = title;
		if (null == records || records.size() < 2) {
			throw new DomainException(MISSING_SERIES_MESSAGE);
		}
		this.records = records;
	}

	private List<RecordImpl> records;

	/* (non-Javadoc)
	 * @see uk.co.lgs.domain.GraphI#getTitle()
	 */
	@Override
	public String getTitle() {
		return title;
	}

	/* (non-Javadoc)
	 * @see uk.co.lgs.domain.GraphI#getRecords()
	 */
	@Override
	public Collection<RecordImpl> getRecords() {
		return records;
	}
}
