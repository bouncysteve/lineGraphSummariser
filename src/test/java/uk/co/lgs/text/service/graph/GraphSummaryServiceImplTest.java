package uk.co.lgs.text.service.graph;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import uk.co.lgs.model.graph.GraphModel;
import uk.co.lgs.model.segment.graph.GraphSegment;
import uk.co.lgs.test.AbstractTest;
import uk.co.lgs.text.service.label.LabelService;
import uk.co.lgs.text.service.segment.graph.GraphSegmentSummaryService;

public class GraphSummaryServiceImplTest extends AbstractTest {

    private static final Lexicon LEXICON = Lexicon.getDefaultLexicon();

    private static final String GRAPH_TITLE = "I am an important graph";
    private static final String THIS_GRAPH_IS_CALLED = "This graph is called ";
    private static final String THIS_GRAPH_SHOWS = "This graph shows ";
    private static final String BETWEEN = " between ";
    private static final String AND = " and ";
    private static final String GRAPH_START = "August 2012";
    private static final String GRAPH_END = "July 2013";
    private static final String SERIES1_LABEL = "Sales of frogs";
    private static final String SERIES2_LABEL = "Average global temperature";

    private static final String IT_SHOWS = "It shows ";

    private List<GraphSegment> graphSegments;
    private final NLGFactory nlgFactory = new NLGFactory(LEXICON);

    @Mock
    private GraphSegmentSummaryService graphSegmentSummaryService;

    @Mock
    private LabelService labelService;

    @Mock
    private GraphModel mockGraphModel;

    @Mock
    private GraphSegment mockGraphSegment;

    private List<NPPhraseSpec> modelLabelList;

    @Mock
    private PhraseElement phraseElement;

    private String graphSummary;

    @InjectMocks
    private final GraphSummaryService underTest = new GraphSummaryServiceImpl();

    @Before
    public void setup() {
        this.graphSegments = new ArrayList<GraphSegment>();
        when(this.mockGraphModel.getGraphSegments()).thenReturn(this.graphSegments);
        this.modelLabelList = new ArrayList<>();

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
        // FIXME: this should have a comma after GRAPH_TITLE, but this doesn't
        // seem to work (@see
        // https://groups.google.com/forum/#!topic/simplenlg/S5lhANTBo70)
        thenTheSummaryEquals(THIS_GRAPH_IS_CALLED + GRAPH_TITLE + ". " + IT_SHOWS + SERIES1_LABEL + AND + SERIES2_LABEL
                + BETWEEN + GRAPH_START + AND + GRAPH_END + ".");
    }

    private void givenAGraphWithSeries(final String series1Label, final String series2Label) {
        when(this.mockGraphModel.getLabels()).thenReturn(new ArrayList<String>());
        this.modelLabelList = new ArrayList<>();
        this.modelLabelList.add(this.nlgFactory.createNounPhrase(series1Label));
        this.modelLabelList.add(this.nlgFactory.createNounPhrase(series2Label));
        when(this.labelService.getLabelsForInitialUse(anyListOf(String.class))).thenReturn(this.modelLabelList);
    }

    private void givenAGraphStartingAndEndingAt(final String start, final String end) {
        when(this.mockGraphSegment.getStartTime()).thenReturn(start);
        when(this.mockGraphSegment.getEndTime()).thenReturn(end);
        this.graphSegments.add(this.mockGraphSegment);
    }

    private void thenTheSummaryEquals(final String summary) {
        assertEquals(summary, this.graphSummary);
    }

    private void whenTheGraphIsSummarised() {
        this.graphSummary = this.underTest.getSummary(this.mockGraphModel);

    }

    private void givenAGraphWithTitle(final String graphTitle) {
        when(this.mockGraphModel.getTitle()).thenReturn(graphTitle);
    }

}
