package uk.co.lgs.domain.graph;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import uk.co.lgs.domain.exception.DomainException;
import uk.co.lgs.domain.graph.Graph;
import uk.co.lgs.domain.graph.GraphImpl;
import uk.co.lgs.domain.record.RecordImpl;

@RunWith(MockitoJUnitRunner.class)
public class GraphTest {

	private static String MISSING_TITLE_MESSAGE = "Graph without title";
	private static String MISSING_SERIES_MESSAGE = "Graph must contain at least two data series";
	
	private Graph underTest;

	@Mock
	private RecordImpl mockSeries;

	@Mock
	private RecordImpl mockSeries2;
	
	private List<RecordImpl> records;

	private String title;

	@Before
	public void setup() {
		title = "This is a graph";
		records = new ArrayList<RecordImpl>();
		records.add(mockSeries);
		records.add(mockSeries2);
		System.out.println(records.size());
	}

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Test
	public void testSunnyDay() throws DomainException {
		underTest = new GraphImpl(title, records);
		assertEquals(records, underTest.getRecords());
		assertEquals(title, underTest.getTitle());
	}

	@Test(expected = DomainException.class)
	public void testNullTitleThrowsException() throws DomainException {
		underTest = new GraphImpl(null, records);
	}

	@Test
	public void testEmptyTitleThrowsException() throws DomainException {
		expectedEx.expect(DomainException.class);
	    expectedEx.expectMessage(MISSING_TITLE_MESSAGE);
		underTest = new GraphImpl("", records);
	}

	
	@Test
	public void testNullSeriesThrowsException() throws DomainException {
		expectedEx.expect(DomainException.class);
	    expectedEx.expectMessage(MISSING_SERIES_MESSAGE);
		underTest = new GraphImpl(title, null);
	}
	
	@Test
	public void testEmptySeriesCollectionThrowsException() throws DomainException {
		expectedEx.expect(DomainException.class);
	    expectedEx.expectMessage(MISSING_SERIES_MESSAGE);
	    records = new ArrayList<RecordImpl>();
		underTest = new GraphImpl(title, records);
	}
	
	@Test
	public void testSingleSeriesCollectionThrowsException() throws DomainException {
		expectedEx.expect(DomainException.class);
	    expectedEx.expectMessage(MISSING_SERIES_MESSAGE);
	    records.remove(mockSeries);
		underTest = new GraphImpl(title, records);
	}
}
