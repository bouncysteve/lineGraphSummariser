package uk.co.lgs.domain;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import uk.co.lgs.domain.exception.DomainException;

/**
 * Domain class representing a multi-series line graph. A graph consists of a
 * title, two or more series, and a time aspect.
 * 
 * @author bouncysteve
 *
 */
public class Graph {

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
	 * @param multiSeries
	 *            The collection of data series which are displayed in the
	 *            graph.
	 * @throws DomainException
	 *             if the title is missing or empty, or if at least two data
	 *             series are not present.
	 */
	public Graph(String title, Collection<Series> multiSeries) throws DomainException {
		super();
		if (StringUtils.isEmpty(title)) {
			throw new DomainException(MISSING_TITLE_MESSAGE);
		}
		this.title = title;
		if (null == multiSeries || multiSeries.size() < 2) {
			throw new DomainException(MISSING_SERIES_MESSAGE);
		}
		this.multiSeries = multiSeries;
	}

	private Collection<Series> multiSeries;

	public String getTitle() {
		return title;
	}

	public Collection<Series> getSeries() {
		return multiSeries;
	}
}
