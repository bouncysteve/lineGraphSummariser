package uk.co.lgs.domain;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import uk.co.lgs.domain.Graph;
import uk.co.lgs.domain.Series;
import uk.co.lgs.domain.exception.DomainException;

@RunWith(MockitoJUnitRunner.class)
public class GraphTest {

	private static String MISSING_TITLE_MESSAGE = "Graph without title";
	private static String MISSING_SERIES_MESSAGE = "Graph must contain at least two data series";
	
	private Graph underTest;

	@Mock
	private Series mockSeries;

	@Mock
	private Series mockSeries2;
	
	private Set<Series> seriesSet;

	private String title;

	@Before
	public void setup() {
		title = "This is a graph";
		seriesSet = new HashSet<Series>();
		seriesSet.add(mockSeries);
		seriesSet.add(mockSeries2);
		System.out.println(seriesSet.size());
	}

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Test
	public void testSunnyDay() throws DomainException {
		underTest = new Graph(title, seriesSet);
		assertEquals(seriesSet, underTest.getSeries());
		assertEquals(title, underTest.getTitle());
	}

	@Test(expected = DomainException.class)
	public void testNullTitleThrowsException() throws DomainException {
		underTest = new Graph(null, seriesSet);
	}

	@Test
	public void testEmptyTitleThrowsException() throws DomainException {
		expectedEx.expect(DomainException.class);
	    expectedEx.expectMessage(MISSING_TITLE_MESSAGE);
		underTest = new Graph("", seriesSet);
	}

	
	@Test
	public void testNullSeriesThrowsException() throws DomainException {
		expectedEx.expect(DomainException.class);
	    expectedEx.expectMessage(MISSING_SERIES_MESSAGE);
		underTest = new Graph(title, null);
	}
	
	@Test
	public void testEmptySeriesCollectionThrowsException() throws DomainException {
		expectedEx.expect(DomainException.class);
	    expectedEx.expectMessage(MISSING_SERIES_MESSAGE);
	    seriesSet = new HashSet<Series>();
		underTest = new Graph(title, seriesSet);
	}
	
	@Test
	public void testSingleSeriesCollectionThrowsException() throws DomainException {
		expectedEx.expect(DomainException.class);
	    expectedEx.expectMessage(MISSING_SERIES_MESSAGE);
	    seriesSet.remove(mockSeries);
		underTest = new Graph(title, seriesSet);
	}
}
