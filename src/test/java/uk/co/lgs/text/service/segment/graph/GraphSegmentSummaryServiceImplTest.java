package uk.co.lgs.text.service.segment.graph;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.text.DecimalFormat;
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
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.realiser.english.Realiser;
import uk.co.lgs.model.gradient.GradientType;
import uk.co.lgs.model.segment.exception.SegmentCategoryNotFoundException;
import uk.co.lgs.model.segment.graph.category.GapTrend;
import uk.co.lgs.model.segment.series.SeriesSegment;
import uk.co.lgs.segment.graph.AbstractGraphSegmentTest;
import uk.co.lgs.text.service.label.LabelService;
import uk.co.lgs.text.service.segment.series.SeriesSegmentSummaryService;
import uk.co.lgs.text.service.synonym.Constants;
import uk.co.lgs.text.service.synonym.SynonymService;
import uk.co.lgs.text.service.value.ValueService;

public class GraphSegmentSummaryServiceImplTest extends AbstractGraphSegmentTest {

    private static final String FIRST_SERIES_LABEL = "Sales of soap";
    private static final String SECOND_SERIES_LABEL = "Price of pyjamas";

    private static final String START_TIME = "2012";
    private static final String END_TIME = "2013";
    private static final Logger LOG = LoggerFactory.getLogger(GraphSegmentSummaryServiceImplTest.class);

    private static final Lexicon LEXICON = Lexicon.getDefaultLexicon();
    private static final Realiser REALISER = new Realiser(LEXICON);

    private List<NPPhraseSpec> labels;
    private final NLGFactory nlgFactory = new NLGFactory(LEXICON);
    @Mock
    private SeriesSegmentSummaryService seriesSegmentSummaryService;
    @Mock
    private LabelService labelService;
    @Mock
    private SynonymService synonymService;
    @Mock
    private ValueService valueService;

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
        final NPPhraseSpec firstSeriesLabel = this.nlgFactory.createNounPhrase(FIRST_SERIES_LABEL);
        final NPPhraseSpec secondSeriesLabel = this.nlgFactory.createNounPhrase(SECOND_SERIES_LABEL);
        this.labels = Arrays.asList(firstSeriesLabel, secondSeriesLabel);
        when(this.labelService.getLabelsForCommonUse(this.graphSegment)).thenReturn(this.labels);
        when(this.labelService.getLabelForCommonUse(this.graphSegment, this.firstSeriesSegment))
                .thenReturn(firstSeriesLabel);
        when(this.labelService.getLabelForCommonUse(this.graphSegment, this.secondSeriesSegment))
                .thenReturn(secondSeriesLabel);

        when(this.firstSeriesSegment.getLabel()).thenReturn(FIRST_SERIES_LABEL);
        when(this.secondSeriesSegment.getLabel()).thenReturn(SECOND_SERIES_LABEL);

        when(this.graphSegment.getSeriesSegment(0)).thenReturn(this.firstSeriesSegment);
        when(this.graphSegment.indexOf(this.firstSeriesSegment)).thenReturn(0);
        when(this.graphSegment.getSeriesSegment(1)).thenReturn(this.secondSeriesSegment);
        when(this.graphSegment.indexOf(this.secondSeriesSegment)).thenReturn(1);
        when(this.graphSegment.getStartTime()).thenReturn(START_TIME);
        when(this.graphSegment.getEndTime()).thenReturn(END_TIME);

        when(this.valueService.formatValueWithUnits(10, null)).thenReturn("10");

        when(this.synonymService.getSynonym(Constants.FALL)).thenReturn("fall");
        when(this.synonymService.getSynonym(Constants.RISE)).thenReturn("rise");
        when(this.synonymService.getSynonym(Constants.CONSTANT)).thenReturn("be constant");
        when(this.synonymService.getSynonym(Constants.CONVERGE)).thenReturn("decrease");
        when(this.synonymService.getSynonym(Constants.DIVERGE)).thenReturn("increase");
        when(this.synonymService.getSynonym(Constants.PARALLEL)).thenReturn("stay the same");

    }

    @Test
    public void testDivergingSeriesWithOneConstant() throws SegmentCategoryNotFoundException {
        givenSeriesValues(10, 10, 20, 30);
        whenTheGraphSegmentIsSummarised();
        thenTheSummarySaysThatThisSeriesIsHigher(this.secondSeriesSegment, null);
        thenTheSummarySaysThatThisSeriesIsConstant(this.firstSeriesSegment, 10D);
        thenTheSummarySaysThatThisSeriesIsRising(this.secondSeriesSegment);
        thenTheSummarySaysThatTheSeriesAreDiverging();
    }

    @Test
    public void testDivergingSeriesStartingAtSameValue() throws SegmentCategoryNotFoundException {
        givenSeriesValues(10, 20, 10, 30);
        whenTheGraphSegmentIsSummarised();
        thenTheSummarySaysThatThisSeriesIsHigher(null, 10D);
        thenTheSummarySaysThatBothSeries("rise");
        thenTheSummarySaysThatTheSeriesAreDiverging();
    }

    @Test
    public void testSeriesWithDifferentGradientTypes() throws SegmentCategoryNotFoundException {
        givenSeriesValues(10, 20, 40, 30);
        whenTheGraphSegmentIsSummarised();
        thenTheSummarySaysThatThisSeriesIsHigher(this.secondSeriesSegment, null);
        thenTheSummarySaysThatThisSeriesIsRising(this.firstSeriesSegment);
        thenTheSummarySaysThatThisSeriesIsFalling(this.secondSeriesSegment);
        thenTheSummarySaysThatTheSeriesAreConverging();
    }

    @Test
    public void testFallingSeriesWithSteadyGap() throws SegmentCategoryNotFoundException {
        givenSeriesValues(20, 0, 40, 20);
        whenTheGraphSegmentIsSummarised();
        thenTheSummarySaysThatThisSeriesIsHigher(this.secondSeriesSegment, null);
        thenTheSummarySaysThatBothSeries("fall");
        thenTheSummarySaysThatTheSeriesAreParallel();
    }

    @Test
    public void testConvergingSeriesWithDifferentGradientTypesIntersecting() throws SegmentCategoryNotFoundException {
        givenSeriesValues(10, 20, 40, 10);
        whenTheGraphSegmentIsSummarised();
        thenTheSummarySaysThatThisSeriesIsHigher(this.secondSeriesSegment, null);
        thenTheSummarySaysThatThisSeriesIsRising(this.firstSeriesSegment);
        thenTheSummarySaysThatThisSeriesIsFalling(this.secondSeriesSegment);
        thenTheSummarySaysThatTheSeriesAreConverging();
        thenTheSummarySaysThatTheSeriesIntersect();
    }

    private void thenTheSummarySaysThatBothSeries(final String trend) {
        final String description = FIRST_SERIES_LABEL + " and " + SECOND_SERIES_LABEL + " ";
        assertTrue(this.summaryText.contains(description + trend));
    }

    private void thenTheSummarySaysThatTheSeriesAreConverging() {
        assertTrue(this.summaryText.contains("the difference between the two decreases"));
    }

    private void thenTheSummarySaysThatTheSeriesAreDiverging() {
        assertTrue(this.summaryText.contains("the difference between the two increases"));
    }

    private void thenTheSummarySaysThatTheSeriesAreParallel() {
        assertTrue(this.summaryText.contains("the difference between the two stays the same"));
    }

    private void thenTheSummarySaysThatTheSeriesIntersect() {
        // TODO Auto-generated method stub

    }

    private void thenTheSummarySaysThatTheSeriesAchievesGlobalMaximum() {
        // TODO Auto-generated method stub
    }

    private void thenTheSummarySaysThatTheSeriesAchievesGlobalMinimum() {
        // TODO Auto-generated method stub
    }

    private void thenTheSummarySaysThatThisSeriesIsRising(final SeriesSegment seriesSegment) {
        assertTrue(this.summaryText.contains(getLabelForSeries(seriesSegment) + " rises"));
    }

    private void thenTheSummarySaysThatThisSeriesIsFalling(final SeriesSegment seriesSegment) {
        assertTrue(this.summaryText.contains(getLabelForSeries(seriesSegment) + " falls"));
    }

    private void thenTheSummarySaysThatThisSeriesIsConstant(final SeriesSegment seriesSegment, final Double value) {
        final DecimalFormat f = new DecimalFormat("0.##");
        assertTrue(this.summaryText
                .contains(String.format(getLabelForSeries(seriesSegment) + " is constant at %1$s", f.format(value))));
    }

    private String getLabelForSeries(final SeriesSegment seriesSegment) {
        String label;
        if (this.firstSeriesSegment.equals(seriesSegment)) {
            label = FIRST_SERIES_LABEL;
        } else {
            label = SECOND_SERIES_LABEL;
        }
        return label;
    }

    private void thenTheSummarySaysThatThisSeriesIsHigher(final SeriesSegment seriesSegment, final Double startValue) {
        String description;
        final DecimalFormat f = new DecimalFormat("0.##");

        if (null == seriesSegment) {
            description = String.format(
                    FIRST_SERIES_LABEL + " and " + SECOND_SERIES_LABEL + " both have value %1$s at ",
                    f.format(startValue));
        } else if (seriesSegment.equals(this.firstSeriesSegment)) {
            description = FIRST_SERIES_LABEL + " is higher at ";
        } else {
            description = SECOND_SERIES_LABEL + " is higher at ";
        }
        assertTrue(this.summaryText.contains(description + START_TIME));
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
        final GradientType firstSeriesTrend = trendFromValues(firstSeriesStartValue, firstSeriesEndValue);

        when(this.graphSegment.getFirstSeriesTrend()).thenReturn(firstSeriesTrend);
        when(this.firstSeriesSegment.getGradientType()).thenReturn(firstSeriesTrend);

        final GradientType secondSeriesTrend = trendFromValues(secondSeriesStartValue, secondSeriesEndValue);
        when(this.graphSegment.getSecondSeriesTrend()).thenReturn(secondSeriesTrend);
        when(this.secondSeriesSegment.getGradientType()).thenReturn(secondSeriesTrend);

        when(this.graphSegment.getGraphSegmentTrend()).thenReturn(trendFromValues(firstSeriesStartValue,
                firstSeriesEndValue, secondSeriesStartValue, secondSeriesEndValue));

    }

    /**
     * NB This is a naive reading of gradient trends, and does not yet consider
     * intersection. When considering intersection, care needs to be taken about
     * whether the intersection is at the start, end, or within the segment.
     *
     * @param firstSeriesStartValue
     * @param firstSeriesEndValue
     * @param secondSeriesStartValue
     * @param secondSeriesEndValue
     * @return
     */
    private GapTrend trendFromValues(final double firstSeriesStartValue, final double firstSeriesEndValue,
            final double secondSeriesStartValue, final double secondSeriesEndValue) {
        GapTrend gapTrend = GapTrend.PARALLEL;
        final double differenceAtStart = secondSeriesStartValue - firstSeriesStartValue;
        final double differenceAtEnd = secondSeriesEndValue - firstSeriesEndValue;
        if (differenceAtStart > differenceAtEnd) {
            gapTrend = GapTrend.CONVERGING;
        } else if (differenceAtStart < differenceAtEnd) {
            gapTrend = GapTrend.DIVERGING;
        }
        return gapTrend;
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
