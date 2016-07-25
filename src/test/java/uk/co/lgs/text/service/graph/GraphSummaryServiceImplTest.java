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

/**
 * FIXME: Although the actual values of the series are mentioned at the start of
 * the graph, if these constitute a global minimum or maximum this is not
 * explicitly mentioned in the summary. This requires many of the features of
 * the GraphSegmentSummaryService, which may be possible once that service is
 * broken up into multiple classes.
 * 
 * @author bouncysteve
 *
 */
public class GraphSummaryServiceImplTest extends AbstractTest {

    private static final Lexicon LEXICON = Lexicon.getDefaultLexicon();

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

        final NPPhraseSpec firstSeriesLabel = this.nlgFactory.createNounPhrase("Sales of frogs");
        final NPPhraseSpec secondSeriesLabel = this.nlgFactory.createNounPhrase("Average global temperature");
        this.labels = Arrays.asList(firstSeriesLabel, secondSeriesLabel);
        when(this.labelService.getLabelsForCommonUse(this.mockGraphModel)).thenReturn(this.labels);
        when(this.labelService.getLabelForCommonUse(this.graphSegment, this.firstSeriesSegment))
                .thenReturn(firstSeriesLabel);
        when(this.labelService.getLabelForCommonUse(this.graphSegment, this.secondSeriesSegment))
                .thenReturn(secondSeriesLabel);

        when(this.firstSeriesSegment.getLabel()).thenReturn("Sales of frogs");
        when(this.secondSeriesSegment.getLabel()).thenReturn("Average global temperature");

        when(this.graphSegment.getSeriesSegment(0)).thenReturn(this.firstSeriesSegment);
        when(this.graphSegment.indexOf(this.firstSeriesSegment)).thenReturn(0);
        when(this.graphSegment.getSeriesSegment(1)).thenReturn(this.secondSeriesSegment);
        when(this.graphSegment.indexOf(this.secondSeriesSegment)).thenReturn(1);
        when(this.graphSegment.getStartTime()).thenReturn("August 2012");

        when(this.valueService.formatValueWithUnits(anyDouble(), anyString())).thenReturn("20%");
    }

    @Test
    public void testGetSummaryTitleAndLabelsWhenTitleEndsWithFullStop() {
        givenAGraphWithTitle("I am an important graph.");
        givenAGraphWithSeriesAndDescriptions("Sales of frogs", "Average global temperature");
        givenAGraphStartingAndEndingAt("August 2012", "July 2013");
        whenTheGraphIsSummarised();
        // FIXME: this should have a comma after "I am an important graph", but
        // this doesn't
        // seem to work (@see
        // https://groups.google.com/forum/#!topic/simplenlg/S5lhANTBo70)
        thenTheSummaryStartsWith("This graph is called \"I am an important graph\". It shows "
                + "Sales of frogs and Average global temperature between August 2012 and July 2013.");
    }

    @Test
    public void testFirstSeriesInitiallyHigher() {
        givenAGraphWithTitle("I am an important graph");
        givenAGraphWithSeriesAndDescriptions("Sales of frogs", "Average global temperature");
        givenTheInitialValuesOfTheSeriesAre(120d, -50d);
        givenAGraphStartingAndEndingAt("August 2012", "July 2013");
        whenTheGraphIsSummarised();
        // FIXME: this should have a comma after "I am an important graph", but
        // this doesn't
        // seem to work (@see
        // https://groups.google.com/forum/#!topic/simplenlg/S5lhANTBo70)
        thenTheSummaryStartsWith("This graph is called \"I am an important graph\". It shows "
                + "Sales of frogs and Average global temperature between August 2012 and July 2013.");
        thenTheSummaryContains(getHigherPhrase("Sales of frogs", "Average global temperature"));

    }

    @Test
    public void testSecondSeriesInitiallyHigher() {
        givenAGraphWithTitle("I am an important graph");
        givenAGraphWithSeriesAndDescriptions("Sales of frogs", "Average global temperature");
        givenTheInitialValuesOfTheSeriesAre(-120d, -76.4d);
        givenAGraphStartingAndEndingAt("August 2012", "July 2013");
        whenTheGraphIsSummarised();
        // FIXME: this should have a comma after "I am an important graph", but
        // this doesn't
        // seem to work (@see
        // https://groups.google.com/forum/#!topic/simplenlg/S5lhANTBo70)
        thenTheSummaryStartsWith("This graph is called \"I am an important graph\". It shows "
                + "Sales of frogs and Average global temperature between August 2012 and July 2013.");
        thenTheSummaryContains(getHigherPhrase("Average global temperature", "Sales of frogs"));
    }

    @Test
    public void testBothSeriesInitiallyHaveTheSameValue() {
        givenAGraphWithTitle("I am an important graph");
        givenAGraphWithSeriesAndDescriptions("Sales of frogs", "Average global temperature");
        givenTheInitialValuesOfTheSeriesAre(-3.14d, -3.14d);
        givenAGraphStartingAndEndingAt("August 2012", "July 2013");
        whenTheGraphIsSummarised();
        // FIXME: this should have a comma after "I am an important graph", but
        // this doesn't
        // seem to work (@see
        // https://groups.google.com/forum/#!topic/simplenlg/S5lhANTBo70)
        thenTheSummaryStartsWith(
                "This graph is called \"I am an important graph\". It shows Sales of frogs and Average global temperature between August 2012 and July 2013.");
        thenTheSummaryContains("Both Sales of frogs and Average global temperature in August 2012 have 20%.");
    }

    private String getHigherPhrase(final String higherSeriesLabel, final String lowerSeriesLabel) {
        return higherSeriesLabel + " is higher with 20% in August 2012 while " + lowerSeriesLabel + " has 20%.";
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
