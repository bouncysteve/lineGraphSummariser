package uk.co.lgs.text.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import uk.co.lgs.model.graph.GraphModel;
import uk.co.lgs.model.segment.graph.GraphSegment;
import uk.co.lgs.test.AbstractTest;
import uk.co.lgs.text.service.graph.GraphSummaryService;
import uk.co.lgs.text.service.graph.GraphSummaryServiceImpl;
import uk.co.lgs.text.service.segment.graph.GraphSegmentSummaryService;

public class GraphSummaryServiceImplTest extends AbstractTest {

    private static final String GRAPH_TITLE = "I am an important graph";
    private static final String THIS_GRAPH_IS_CALLED = "This graph is called ";
    private static final String THIS_GRAPH_SHOWS = "This graph shows ";
    private static final String BETWEEN = " between ";
    private static final String AND = " and ";
    private static final String GRAPH_START = "August 2012";
    private static final String GRAPH_END = "July 2013";
    private static final String SERIES1_LABEL = "Sales of frogs";
    private static final String SERIES2_LABEL = "Average global temperature";

    private List<GraphSegment> graphSegments;

    @Mock
    private GraphSegmentSummaryService graphSegmentSummaryService;

    @Mock
    private GraphModel mockGraphModel;

    @Mock
    private GraphSegment mockGraphSegment;

    private String graphSummary;

    @InjectMocks
    private GraphSummaryService underTest = new GraphSummaryServiceImpl();

    @Before
    public void setup() {
        this.graphSegments = new ArrayList<GraphSegment>();
        when(this.mockGraphModel.getGraphSegments()).thenReturn(this.graphSegments);
    }

    @Test
    public void testGetSummaryTitleOnlyTitleEndsWithFullStop() {
        givenAGraphWithTitle(GRAPH_TITLE + ".");
        whenTheGraphIsSummarised();
        thenTheSummaryEquals(THIS_GRAPH_IS_CALLED + GRAPH_TITLE + ".");
    }

    @Test
    public void testGetSummaryTitleAndLabels() {
        givenAGraphWithTitle(GRAPH_TITLE);
        givenAGraphWithSeries(SERIES1_LABEL, SERIES2_LABEL);
        givenAGraphStartingAndEndingAt(GRAPH_START, GRAPH_END);
        whenTheGraphIsSummarised();
        thenTheSummaryEquals(THIS_GRAPH_IS_CALLED + GRAPH_TITLE + ". " + THIS_GRAPH_SHOWS + SERIES1_LABEL + AND
                + SERIES2_LABEL + BETWEEN + GRAPH_START + AND + GRAPH_END + ".");
    }

    private void givenAGraphWithSeries(String series1Label, String series2Label) {

        when(this.mockGraphModel.getLabels())
                .thenReturn(java.util.Arrays.asList("timeSeries", series1Label, series2Label));

    }

    private void givenAGraphStartingAndEndingAt(String start, String end) {
        when(this.mockGraphSegment.getStartTime()).thenReturn(start);
        when(this.mockGraphSegment.getEndTime()).thenReturn(end);
        this.graphSegments.add(this.mockGraphSegment);
    }

    private void thenTheSummaryEquals(String summary) {
        assertEquals(summary, this.graphSummary);
    }

    private void whenTheGraphIsSummarised() {
        this.graphSummary = this.underTest.getSummary(this.mockGraphModel);

    }

    private void givenAGraphWithTitle(String graphTitle) {
        when(this.mockGraphModel.getTitle()).thenReturn(graphTitle);
    }

}
