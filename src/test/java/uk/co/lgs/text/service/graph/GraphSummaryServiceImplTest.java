package uk.co.lgs.text.service.graph;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import uk.co.lgs.model.graph.GraphModel;
import uk.co.lgs.model.segment.graph.GraphSegment;
import uk.co.lgs.model.segment.series.SeriesSegment;
import uk.co.lgs.test.AbstractTest;
import uk.co.lgs.text.service.label.LabelService;
import uk.co.lgs.text.service.segment.graph.GraphSegmentSummaryService;
import uk.co.lgs.text.service.synonym.SynonymService;
import uk.co.lgs.text.service.synonym.SynonymServiceImpl;
import uk.co.lgs.text.service.value.ValueService;

public class GraphSummaryServiceImplTest extends AbstractTest {

    private static final Lexicon LEXICON = Lexicon.getDefaultLexicon();

    private static final String GRAPH_TITLE = "I am an important graph";
    private static final String THIS_GRAPH_IS_CALLED = "This graph is called ";
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
    private ValueService valueService;

    @Spy
    private final SynonymService synonymService = new SynonymServiceImpl();

    @Mock
    private GraphModel mockGraphModel;

    @Mock
    private GraphSegment mockGraphSegment;

    private List<NPPhraseSpec> modelLabelList;

    @Mock
    private GraphSegment graphSegment;

    @Mock
    protected SeriesSegment firstSeriesSegment;

    @Mock
    protected SeriesSegment secondSeriesSegment;

    @Mock
    private PhraseElement phraseElement;

    @Captor
    private ArgumentCaptor<Double> captor;

    private String graphSummary;

    private List<NPPhraseSpec> labels;

    @InjectMocks
    private final GraphSummaryService underTest = new GraphSummaryServiceImpl();

    @Before
    public void beforeEachTest() {
        this.synonymService.setRandomise(false);
        this.graphSegments = new ArrayList<GraphSegment>();
        this.graphSegments.add(this.graphSegment);
        when(this.mockGraphModel.getGraphSegments()).thenReturn(this.graphSegments);
        this.modelLabelList = new ArrayList<>();

        final NPPhraseSpec firstSeriesLabel = this.nlgFactory.createNounPhrase(SERIES1_LABEL);
        final NPPhraseSpec secondSeriesLabel = this.nlgFactory.createNounPhrase(SERIES2_LABEL);
        this.labels = Arrays.asList(firstSeriesLabel, secondSeriesLabel);
        when(this.labelService.getLabelsForCommonUse(this.mockGraphModel)).thenReturn(this.labels);
        when(this.labelService.getLabelForCommonUse(this.graphSegment, this.firstSeriesSegment))
                .thenReturn(firstSeriesLabel);
        when(this.labelService.getLabelForCommonUse(this.graphSegment, this.secondSeriesSegment))
                .thenReturn(secondSeriesLabel);

        when(this.firstSeriesSegment.getLabel()).thenReturn(SERIES1_LABEL);
        when(this.secondSeriesSegment.getLabel()).thenReturn(SERIES2_LABEL);

        when(this.graphSegment.getSeriesSegment(0)).thenReturn(this.firstSeriesSegment);
        when(this.graphSegment.indexOf(this.firstSeriesSegment)).thenReturn(0);
        when(this.graphSegment.getSeriesSegment(1)).thenReturn(this.secondSeriesSegment);
        when(this.graphSegment.indexOf(this.secondSeriesSegment)).thenReturn(1);
        when(this.graphSegment.getStartTime()).thenReturn(GRAPH_START);

        when(this.valueService.formatValueWithUnits(anyDouble(), anyString())).thenReturn("20%");
    }

    @Test
    public void testGetSummaryTitleAndLabelsWhenTitleEndsWithFullStop() {
        givenAGraphWithTitle(GRAPH_TITLE + ".");
        givenAGraphWithSeriesAndDescriptions(SERIES1_LABEL, SERIES2_LABEL);
        givenAGraphStartingAndEndingAt(GRAPH_START, GRAPH_END);
        whenTheGraphIsSummarised();
        // FIXME: this should have a comma after GRAPH_TITLE, but this doesn't
        // seem to work (@see
        // https://groups.google.com/forum/#!topic/simplenlg/S5lhANTBo70)
        thenTheSummaryStartsWith(THIS_GRAPH_IS_CALLED + "\"" + GRAPH_TITLE + "\". " + IT_SHOWS + SERIES1_LABEL + AND
                + SERIES2_LABEL + BETWEEN + GRAPH_START + AND + GRAPH_END + ".");
    }

    @Test
    public void testFirstSeriesInitiallyHigher() {
        givenAGraphWithTitle(GRAPH_TITLE);
        givenAGraphWithSeriesAndDescriptions(SERIES1_LABEL, SERIES2_LABEL);
        givenTheInitialValuesOfTheSeriesAre(120d, -50d);
        givenAGraphStartingAndEndingAt(GRAPH_START, GRAPH_END);
        whenTheGraphIsSummarised();
        // FIXME: this should have a comma after GRAPH_TITLE, but this doesn't
        // seem to work (@see
        // https://groups.google.com/forum/#!topic/simplenlg/S5lhANTBo70)
        thenTheSummaryStartsWith(THIS_GRAPH_IS_CALLED + "\"" + GRAPH_TITLE + "\". " + IT_SHOWS + SERIES1_LABEL + AND
                + SERIES2_LABEL + BETWEEN + GRAPH_START + AND + GRAPH_END + ".");
        thenTheSummaryContains(getHigherPhrase(SERIES1_LABEL, SERIES2_LABEL));

    }

    @Test
    public void testSecondSeriesInitiallyHigher() {
        givenAGraphWithTitle(GRAPH_TITLE);
        givenAGraphWithSeriesAndDescriptions(SERIES1_LABEL, SERIES2_LABEL);
        givenTheInitialValuesOfTheSeriesAre(-120d, -76.4d);
        givenAGraphStartingAndEndingAt(GRAPH_START, GRAPH_END);
        whenTheGraphIsSummarised();
        // FIXME: this should have a comma after GRAPH_TITLE, but this doesn't
        // seem to work (@see
        // https://groups.google.com/forum/#!topic/simplenlg/S5lhANTBo70)
        thenTheSummaryStartsWith(THIS_GRAPH_IS_CALLED + "\"" + GRAPH_TITLE + "\". " + IT_SHOWS + SERIES1_LABEL + AND
                + SERIES2_LABEL + BETWEEN + GRAPH_START + AND + GRAPH_END + ".");
        thenTheSummaryContains(getHigherPhrase(SERIES2_LABEL, SERIES1_LABEL));
    }

    @Test
    public void testBothSeriesInitiallyHaveTheSameValue() {
        givenAGraphWithTitle(GRAPH_TITLE);
        givenAGraphWithSeriesAndDescriptions(SERIES1_LABEL, SERIES2_LABEL);
        givenTheInitialValuesOfTheSeriesAre(-3.14d, -3.14d);
        givenAGraphStartingAndEndingAt(GRAPH_START, GRAPH_END);
        whenTheGraphIsSummarised();
        // FIXME: this should have a comma after GRAPH_TITLE, but this doesn't
        // seem to work (@see
        // https://groups.google.com/forum/#!topic/simplenlg/S5lhANTBo70)
        thenTheSummaryStartsWith(THIS_GRAPH_IS_CALLED + "\"" + GRAPH_TITLE + "\". " + IT_SHOWS + SERIES1_LABEL + AND
                + SERIES2_LABEL + BETWEEN + GRAPH_START + AND + GRAPH_END + ".");
        thenTheSummaryContains("Both " + SERIES1_LABEL + " and " + SERIES2_LABEL + " at " + GRAPH_START + " have 20%.");
    }

    private String getHigherPhrase(final String higherSeriesLabel, final String lowerSeriesLabel) {
        return higherSeriesLabel + " is higher with 20% at " + GRAPH_START + " while " + lowerSeriesLabel + " has 20%.";
    }

    private void givenAGraphWithSeriesAndDescriptions(final String series1Label, final String series2Label) {
        when(this.mockGraphModel.getLabels()).thenReturn(new ArrayList<String>());
        this.modelLabelList = new ArrayList<>();
        this.modelLabelList.add(this.nlgFactory.createNounPhrase(series1Label));
        this.modelLabelList.add(this.nlgFactory.createNounPhrase(series2Label));
        when(this.labelService.getLabelsForInitialUse(this.mockGraphModel)).thenReturn(this.modelLabelList);
    }

    private void givenTheInitialValuesOfTheSeriesAre(final double firstSeriesStartValue,
            final double secondSeriesStartValue) {
        when(this.firstSeriesSegment.getStartValue()).thenReturn(firstSeriesStartValue);
        when(this.secondSeriesSegment.getStartValue()).thenReturn(secondSeriesStartValue);
        when(this.graphSegment.getHigherSeriesAtStart())
                .thenReturn(higherSeriesOf(firstSeriesStartValue, secondSeriesStartValue));
    }

    private void givenAGraphStartingAndEndingAt(final String start, final String end) {
        when(this.mockGraphSegment.getStartTime()).thenReturn(start);
        when(this.mockGraphSegment.getEndTime()).thenReturn(end);
        this.graphSegments.add(this.mockGraphSegment);
    }

    private void thenTheSummaryStartsWith(final String summary) {
        assertTrue(this.graphSummary.startsWith(summary));
        // assertEquals(summary, this.graphSummary);
    }

    private void thenTheSummaryContains(final String string) {
        assertTrue(this.graphSummary.contains(string));
        // assertEquals(string, this.graphSummary);
    }

    private void whenTheGraphIsSummarised() {
        this.graphSummary = this.underTest.getSummary(this.mockGraphModel);

    }

    private void givenAGraphWithTitle(final String graphTitle) {
        when(this.mockGraphModel.getTitle()).thenReturn(graphTitle);
    }

    private SeriesSegment higherSeriesOf(final double firstSeriesValue, final double secondSeriesValue) {
        SeriesSegment seriesSegment = null;
        if (firstSeriesValue > secondSeriesValue) {
            seriesSegment = this.firstSeriesSegment;
        } else if (secondSeriesValue > firstSeriesValue) {
            seriesSegment = this.secondSeriesSegment;
        }
        return seriesSegment;
    }

}
