package uk.co.lgs.text.service.segment.graph;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simplenlg.framework.DocumentElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.realiser.english.Realiser;
import uk.co.lgs.model.gradient.GradientType;
import uk.co.lgs.model.segment.exception.SegmentCategoryNotFoundException;
import uk.co.lgs.model.segment.series.SeriesSegment;
import uk.co.lgs.segment.graph.AbstractGraphSegmentTest;
import uk.co.lgs.text.service.label.LabelService;
import uk.co.lgs.text.service.segment.series.SeriesSegmentSummaryService;

public class GraphSegmentSummaryServiceImplTest extends AbstractGraphSegmentTest {

    private static final String FIRST_SERIES_LABEL = "Sales of soap";
    private static final String SECOND_SERIES_LABEL = "Price of pyjamas";

    private static final String START_TIME = "2012";
    private static final String END_TIME = "2013";
    private static final Logger LOG = LoggerFactory.getLogger(GraphSegmentSummaryServiceImplTest.class);

    private static final Lexicon LEXICON = Lexicon.getDefaultLexicon();
    private static final Realiser REALISER = new Realiser(LEXICON);

    private List<PhraseElement> labels;
    private final NLGFactory nlgFactory = new NLGFactory(LEXICON);
    @Mock
    private SeriesSegmentSummaryService seriesSegmentSummaryService;
    @Mock
    private LabelService labelService;

    @InjectMocks
    private final GraphSegmentSummaryService underTest = new GraphSegmentSummaryServiceImpl();

    private DocumentElement summary;
    private String summaryText;

    /*
     * TODO: 1) Add tests for When the gradient types are the same (and
     * non-zero). First series should be steeper, less steep and parallel to
     * second series.
     */

    @Before
    public void beforeEachTest() {
        this.labels = Arrays.asList(this.nlgFactory.createNounPhrase(FIRST_SERIES_LABEL),
                this.nlgFactory.createNounPhrase(SECOND_SERIES_LABEL));
        when(this.labelService.getLabelsForCommonUse(this.graphSegment)).thenReturn(this.labels);
        when(this.firstSeriesSegment.getLabel()).thenReturn(FIRST_SERIES_LABEL);
        when(this.secondSeriesSegment.getLabel()).thenReturn(SECOND_SERIES_LABEL);
        when(this.graphSegment.getSeriesSegment(0)).thenReturn(this.firstSeriesSegment);
        when(this.graphSegment.indexOf(this.firstSeriesSegment)).thenReturn(0);
        when(this.graphSegment.getSeriesSegment(1)).thenReturn(this.secondSeriesSegment);
        when(this.graphSegment.indexOf(this.secondSeriesSegment)).thenReturn(1);
        when(this.graphSegment.getStartTime()).thenReturn(START_TIME);
        when(this.graphSegment.getEndTime()).thenReturn(END_TIME);

    }

    /**
     * "Between start time and end time...
     * "series 1 remains constant at a value and series 2 remains constant at a (different) value"
     * .
     *
     * @throws SegmentCategoryNotFoundException
     */
    @Test
    public void testS1FlatLowerS2Rising() throws SegmentCategoryNotFoundException {
        givenSeriesValues(10, 10, 20, 30);
        whenTheGraphSegmentIsSummarised();
        thenTheSummarySaysThatThisSeriesIsHigher(this.firstSeriesSegment);
        thenTheSummarySaysThatTheSeriesAreDiverging();
    }

    private void thenTheSummarySaysThatTheSeriesAreDiverging() {
        // TODO Auto-generated method stub

    }

    private void thenTheSummarySaysThatThisSeriesIsHigher(final SeriesSegment seriesSegment) {
        when(this.graphSegment.getHigherSeriesAtStart()).thenReturn(seriesSegment);
    }

    private void givenSeriesValues(final double firstSeriesStartValue, final double firstSeriesEndValue,
            final double secondSeriesStartValue, final double secondSeriesEndValue) {
        when(this.firstSeriesSegment.getStartValue()).thenReturn(firstSeriesStartValue);
        when(this.firstSeriesSegment.getEndValue()).thenReturn(firstSeriesEndValue);
        when(this.secondSeriesSegment.getStartValue()).thenReturn(secondSeriesStartValue);
        when(this.secondSeriesSegment.getEndValue()).thenReturn(secondSeriesEndValue);
        when(this.graphSegment.getHigherSeriesAtStart())
                .thenReturn(higherSeriesOf(firstSeriesStartValue, secondSeriesStartValue));
        when(this.graphSegment.getHigherSeriesAtEnd())
                .thenReturn(higherSeriesOf(firstSeriesEndValue, secondSeriesEndValue));
        when(this.graphSegment.getFirstSeriesTrend())
                .thenReturn(trendFromValues(firstSeriesStartValue, firstSeriesEndValue));
        when(this.graphSegment.getSecondSeriesTrend())
                .thenReturn(trendFromValues(secondSeriesStartValue, secondSeriesEndValue));

    }

    private GradientType trendFromValues(final double startValue, final double endValue) {
        GradientType type = GradientType.ZERO;
        final double valueDiff = endValue - startValue;
        if (valueDiff > 0) {
            type = GradientType.POSITIVE;
        } else if (valueDiff < 0) {
            type = GradientType.NEGATIVE;
        }
        return type;
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

    private void whenTheGraphSegmentIsSummarised() throws SegmentCategoryNotFoundException {
        this.summary = this.underTest.getSummary(this.graphSegment);
        this.summaryText = REALISER.realise(this.summary).toString();
        LOG.debug(this.summaryText);
    }

}
