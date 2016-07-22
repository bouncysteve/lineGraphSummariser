package uk.co.lgs.text.service.segment.graph;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simplenlg.framework.DocumentElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.realiser.english.Realiser;
import uk.co.lgs.model.gradient.GradientType;
import uk.co.lgs.model.graph.GraphModel;
import uk.co.lgs.model.segment.graph.GraphSegment;
import uk.co.lgs.model.segment.graph.category.GapTrend;
import uk.co.lgs.model.segment.series.SeriesSegment;
import uk.co.lgs.segment.graph.AbstractGraphSegmentTest;
import uk.co.lgs.text.service.label.LabelService;
import uk.co.lgs.text.service.synonym.SynonymService;
import uk.co.lgs.text.service.synonym.SynonymServiceImpl;
import uk.co.lgs.text.service.value.ValueService;
import uk.co.lgs.text.service.value.ValueServiceImpl;

public class GraphSegmentSummaryServiceImplTest extends AbstractGraphSegmentTest {
    private static final Lexicon LEXICON = Lexicon.getDefaultLexicon();
    private static final Realiser REALISER = new Realiser(LEXICON);
    private static final String START = "March 2016";
    private static final String END = "April 2016";
    private static final String FIRST_SERIES_LABEL = "Cost of sunglasses";
    private static final String SECOND_SERIES_LABEL = "Sales of doughnuts";
    private static final Logger LOG = LoggerFactory.getLogger(GraphSegmentSummaryServiceImplTest.class);

    private String summaryText;
    private DocumentElement summary;

    @Mock
    private LabelService labelService;
    @Spy
    private final SynonymService synonymService = new SynonymServiceImpl();

    @Spy
    private final ValueService valueService = new ValueServiceImpl();

    @Mock
    private GraphModel model;

    @InjectMocks
    private GraphSegmentSummaryServiceImpl graphSegmentSummaryService;

    private List<NPPhraseSpec> labels;
    private final NLGFactory nlgFactory = new NLGFactory(LEXICON);
    private List<GraphSegment> graphSegments;

    @Before
    public void beforeEachTest() {
        this.synonymService.setRandomise(false);
        REALISER.setCommaSepCuephrase(true);
        final NPPhraseSpec firstSeriesLabel = this.nlgFactory.createNounPhrase(FIRST_SERIES_LABEL);
        final NPPhraseSpec secondSeriesLabel = this.nlgFactory.createNounPhrase(SECOND_SERIES_LABEL);
        secondSeriesLabel.setPlural(true);
        this.labels = Arrays.asList(firstSeriesLabel, secondSeriesLabel);

        when(this.labelService.getLabelsForCommonUse(this.model)).thenReturn(this.labels);
        this.graphSegments = new ArrayList<>();
        when(this.model.getGraphSegments()).thenReturn(this.graphSegments);
    }

    @Test
    public void testOppositeTrendsDiverging01() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(50d, 100d, 20d, 10d, false, false);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs(
                "Until April 2016, Cost of sunglasses increases but Sales of doughnuts decrease, so the gap between them increases.");
    }

    @Test
    public void testOppositeTrendsDivergingToGlobalMaximumGap02() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(0d, 0d, 0d, 90d, true, false);
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(50d, 100d, 20d, 10d, true, false);
        whenTheSegmentIsSummarised(1);
        thenTheSummaryIs(
                "Until April 2016, Cost of sunglasses increases but Sales of doughnuts decrease, so the gap between them increases to 90%.");
    }

    @Test
    public void testOppositeTrendsDivergingToGlobalMaximumGapFirstTimeMentioned03() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(50d, 100d, 20d, 10d, true, false);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs(
                "Until April 2016, Cost of sunglasses increases but Sales of doughnuts decrease, so the gap between them increases to 90%, its maximum value.");
    }

    @Test
    public void testOppositeTrendsConverging04() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(20d, 10d, -20d, 0d, false, false);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs(
                "Until April 2016, Cost of sunglasses decreases but Sales of doughnuts increase, so the gap between them decreases.");
    }

    @Test
    public void testOppositeTrendsConvergingToGlobalMinimumGap05() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(20d, 10d, -20d, 0d, false, true);
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(20d, 10d, -20d, 0d, false, true);
        whenTheSegmentIsSummarised(1);
        thenTheSummaryIs(
                "Until April 2016, Cost of sunglasses decreases but Sales of doughnuts increase, so the gap between them decreases to 10%.");
    }

    @Test
    public void testOppositeTrendsConvergingToGlobalMinimumGapFirstTimeMentioned06() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(20d, 10d, -20d, 0d, false, true);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs(
                "Until April 2016, Cost of sunglasses decreases but Sales of doughnuts increase, so the gap between them decreases to 10%, its minimum value.");
    }

    @Test
    public void testOppositeTrendsConvergingToGlobalMinimumGapFirstTimeMentionedGraphHasIntersection07() {
        givenTheGraphHasIntersections();
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(20d, 10d, -20d, 0d, false, true);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs(
                "Until April 2016, Cost of sunglasses decreases but Sales of doughnuts increase, so the gap between them decreases to 10%.");
    }

    @Test
    public void testOppositeTrendsConvergingToIntersectionDuringSection08() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(20d, 10d, -20d, 15d, false, false);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs(
                "Next, Cost of sunglasses decreases but Sales of doughnuts increase, and they cross, so that by April 2016 Sales of doughnuts are higher with 15% while Cost of sunglasses has 10%.");

    }

    @Test
    public void testOppositeTrendsConvergingToIntersectionAtEndOfSection09() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(20d, 10d, -20d, 10d, false, false);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs(
                "By April 2016, Cost of sunglasses decreases to 10% and Sales of doughnuts increase to the same value.");
    }

    /*********************
     * Same trends falling
     *************************************/
    @Test
    public void testBothFallingConverging10a() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(50d, 30d, 20d, 10d, false, false);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs(
                "Until April 2016, both decrease, Cost of sunglasses decreases more steeply, and so, the gap between them decreases.");
    }

    @Test
    public void testBothFallingConvergingToGlobalMinimumGap11a() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(50d, 30d, 20d, 10d, false, true);
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(50d, 30d, 20d, 10d, false, true);
        whenTheSegmentIsSummarised(1);
        thenTheSummaryIs(
                "Until April 2016, both decrease, Cost of sunglasses decreases more steeply and so, the gap between them decreases to 20%.");
    }

    @Test
    public void testBothFallingConvergingToGlobalMinimumGapFirstTimeMentioned12a() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(50d, 30d, 20d, 10d, false, true);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs(
                "Until April 2016, both decrease, Cost of sunglasses decreases more steeply and so, the gap between them decreases to 20%, its minimum value.");
    }

    @Test
    public void testBothFallingConvergingToGlobalMinimumGapWhenGraphHasIntersections13a() {
        givenTheGraphHasIntersections();
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(50d, 30d, 20d, 10d, false, true);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs(
                "Until April 2016, both decrease, Cost of sunglasses decreases more steeply and so, the gap between them decreases to 20%.");
    }

    @Test
    public void testBothFallingDiverging14a() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(50d, -200d, 60d, 40d, false, false);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs(
                "Until April 2016, both decrease, Cost of sunglasses decreases more steeply and so, the gap between them increases.");
    }

    @Test
    public void testBothFallingDivergingToGlobalMaximumGap15a() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(50d, -200d, 60d, 40d, true, false);
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(50d, -200d, 60d, 40d, true, false);
        whenTheSegmentIsSummarised(1);
        thenTheSummaryIs(
                "Until April 2016, both decrease, Cost of sunglasses decreases more steeply and so, the gap between them increases to 240%.");
    }

    @Test
    public void testBothFallingDivergingToGlobalMaximumGapFirstTimeMentioned16a() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(50d, -200d, 60d, 40d, true, false);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs(
                "Until April 2016, both decrease, Cost of sunglasses decreases more steeply and so, the gap between them increases to 240%, its maximum value.");
    }

    @Test
    public void testBothFallingConstant17a() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(100d, 50d, 200d, 150d, false, false);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs(
                "Until April 2016, both decrease at the same rate, and so, the gap between them remains 100%.");
    }

    @Test
    public void testBothFallingConvergingToIntersectionAtEndOfSection18a() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(50d, -200d, 60d, -200d, false, false);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs("By April 2016, both decrease to -200%.");
    }

    @Test
    public void testBothFallingConvergingToIntersectionDuringSection19a() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(50d, -100d, 60d, -200d, false, false);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs(
                "Next, both decrease, Sales of doughnuts decrease more steeply, so they cross and by April 2016 Cost of sunglasses is higher with -100%, while Sales of doughnuts have -200%.");
    }

    /***************
     * Same trends rising
     ***********************************************************/

    @Test
    public void testBothRisingConverging10b() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(50d, 60d, 20d, 50d, false, false);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs(
                "Until April 2016, both increase, Sales of doughnuts increase more steeply, and so, the gap between them decreases.");
    }

    @Test
    public void testBothRisingConvergingToGlobalMinimumGap11b() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(50d, 130d, 20d, 110d, false, true);
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(50d, 130d, 20d, 110d, false, true);
        whenTheSegmentIsSummarised(1);
        thenTheSummaryIs(
                "Until April 2016, both increase, Sales of doughnuts increase more steeply, and so, the gap between them decreases to 20%.");

    }

    @Test
    public void testBothRisingConvergingToGlobalMinimumGapFirstTimeMentioned12b() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(50d, 130d, 20d, 110d, false, true);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs(
                "Until April 2016, both increase, Sales of doughnuts increase more steeply, and so, the gap between them decreases to 20%, its minimum value.");
    }

    @Test
    public void testBothRisingConvergingToGlobalMinimumGapWhenGraphHasIntersections13b() {
        givenTheGraphHasIntersections();
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(50d, 130d, 20d, 110d, false, true);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs(
                "Until April 2016, both increase, Sales of doughnuts increase more steeply, and so, the gap between them decreases to 20%.");
    }

    @Test
    public void testBothRisingDiverging14b() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(-250d, -200d, 60d, 340d, false, false);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs(
                "Until April 2016, both increase, Sales of doughnuts increase more steeply and so, the gap between them increases.");
    }

    @Test
    public void testBothRisingDivergingToGlobalMaximumGap15b() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(-250d, -200d, 60d, 340d, true, false);
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(-250d, -200d, 60d, 340d, true, false);
        whenTheSegmentIsSummarised(1);
        thenTheSummaryIs(
                "Until April 2016, both increase, Sales of doughnuts increase more steeply and so, the gap between them increases to 540%.");
    }

    @Test
    public void testBothRisingDivergingToGlobalMaximumGapFirstTimeMentioned16b() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(-250d, -200d, 60d, 340d, true, false);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs(
                "Until April 2016, both increase, Sales of doughnuts increase more steeply and so, the gap between them increases to 540%, its maximum value.");
    }

    @Test
    public void testBothRisingConstant17b() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(50d, 100d, 150d, 200d, false, false);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs(
                "Until April 2016, both increase at the same rate, and so, the gap between them remains 100%.");
    }

    @Test
    public void testBothRisingConvergingToIntersectionAtEndOfSection18b() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(-29450d, -200d, -1298721948760d, -200d, false, false);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs("By April 2016, both increase to -200%.");
    }

    @Test
    public void testBothRisingConvergingToIntersectionDuringSection19b() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(-1550d, -100d, -1603660d, -10d, false, false);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs(
                "Next, both increase, Sales of doughnuts increase more steeply, so they cross and by April 2016 Sales of doughnuts are higher with -10%, while Cost of sunglasses has -100%.");
    }

    @Test
    public void testOppositeTrendsDivergingFromSameValue20() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(10d, 50d, 10d, 0d, false, false);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs(
                "Until April 2016, Cost of sunglasses increases but Sales of doughnuts decrease, so the gap between them increases, so that Cost of sunglasses is higher with 50%, while Sales of doughnuts have 0%.");
    }

    @Test
    public void testOppositeTrendsDivergingFromSameValueToGlobalMaximumGap21() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(-1550d, 10d, -1603660d, 10d, true, false);
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(10d, 50d, 10d, 0d, true, false);
        whenTheSegmentIsSummarised(1);
        thenTheSummaryIs(
                "Until April 2016, Cost of sunglasses increases but Sales of doughnuts decrease, so the gap between them increases to 50%, so that Cost of sunglasses is higher with 50%, while Sales of doughnuts have 0%.");
    }

    @Test
    public void testOppositeTrendsDivergingFromSameValueToGlobalMaximumGapFirstTimeMentioned22() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(10d, 50d, 10d, 0d, true, false);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs(
                "Until April 2016, Cost of sunglasses increases but Sales of doughnuts decrease, so the gap between them increases to 50%, its maximum value, so that Cost of sunglasses is higher with 50%, while Sales of doughnuts have 0%.");
    }

    @Test
    public void testBothFallingDivergingFromSameValue23a() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(60d, -200d, 60d, 40d, false, false);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs(
                "Until April 2016, both decrease, Cost of sunglasses decreases more steeply and so, the gap between them increases, so that Sales of doughnuts are higher with 40%, while Cost of sunglasses has -200%.");
    }

    @Test
    public void testBothFallingDivergingToGlobalMaximumGapFromSameValue24a() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(-1550d, 10d, -1603660d, 10d, true, false);
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(60d, -200d, 60d, 40d, true, false);
        whenTheSegmentIsSummarised(1);
        thenTheSummaryIs(
                "Until April 2016, both decrease, Cost of sunglasses decreases more steeply and so, the gap between them increases to 240%, so that Sales of doughnuts are higher with 40%, while Cost of sunglasses has -200%.");
    }

    @Test
    public void testBothFallingDivergingToGlobalMaximumGapFromSameValueFirstTimeMentioned25a() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(60d, -200d, 60d, 40d, true, false);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs(
                "Until April 2016, both decrease, Cost of sunglasses decreases more steeply and so, the gap between them increases to 240%, its maximum value, so that Sales of doughnuts are higher with 40%, while Cost of sunglasses has -200%.");
    }

    /**
     * TODO: Does this need a bit more?
     */
    @Test
    public void testBothFallingConstantGapFromSameValue26a() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(100d, 50d, 100d, 50d, false, false);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs("Until April 2016, both decrease at the same rate, and so, the gap between them remains 0%.");
    }

    @Test
    public void testBothRisingDivergingFromSameValue23b() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(-250d, -200d, -250d, 340d, false, false);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs(
                "Until April 2016, both increase, Sales of doughnuts increase more steeply and so, the gap between them increases, so that Sales of doughnuts are higher with 340%, while Cost of sunglasses has -200%.");
    }

    @Test
    public void testBothRisingDivergingToGlobalMaximumGapFromSameValue24b() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(-250d, -200d, -250d, 340d, true, false);
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(-250d, -200d, -250d, 340d, true, false);
        whenTheSegmentIsSummarised(1);
        thenTheSummaryIs(
                "Until April 2016, both increase, Sales of doughnuts increase more steeply and so, the gap between them increases to 540%, so that Sales of doughnuts are higher with 340%, while Cost of sunglasses has -200%.");
    }

    @Test
    public void testBothRisingDivergingToGlobalMaximumGapFromSameValueFirstTimeMentioned25b() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(-250d, -200d, -250d, 340d, true, false);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs(
                "Until April 2016, both increase, Sales of doughnuts increase more steeply and so, the gap between them increases to 540%, its maximum value, so that Sales of doughnuts are higher with 340%, while Cost of sunglasses has -200%.");
    }

    @Test
    public void testBothRisingConstantGapFromSameValue26b() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(100d, 200d, 100d, 200d, false, false);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs("Until April 2016, both increase at the same rate, and so, the gap between them remains 0%.");
    }

    private void givenASeriesWithValuesHasMaximumGapAndMinimumGap(final double firstSeriesStartValue,
            final double firstSeriesEndValue, final double secondSeriesStartValue, final double secondSeriesEndValue,
            final boolean maximumGap, final boolean minimumGap) {
        final GradientType firstSeriesTrend = trendFromValues(firstSeriesStartValue, firstSeriesEndValue);
        final GradientType secondSeriesTrend = trendFromValues(secondSeriesStartValue, secondSeriesEndValue);

        final SeriesSegment firstSeriesSegment = mock(SeriesSegment.class);
        when(firstSeriesSegment.getStartValue()).thenReturn(firstSeriesStartValue);
        when(firstSeriesSegment.getEndValue()).thenReturn(firstSeriesEndValue);
        when(firstSeriesSegment.getLabel()).thenReturn(FIRST_SERIES_LABEL);
        when(firstSeriesSegment.getGradientType()).thenReturn(firstSeriesTrend);
        when(firstSeriesSegment.getUnits()).thenReturn("%");
        when(firstSeriesSegment.getGradient()).thenReturn(firstSeriesEndValue - firstSeriesStartValue);

        final SeriesSegment secondSeriesSegment = mock(SeriesSegment.class);
        when(secondSeriesSegment.getStartValue()).thenReturn(secondSeriesStartValue);
        when(secondSeriesSegment.getEndValue()).thenReturn(secondSeriesEndValue);
        when(secondSeriesSegment.getLabel()).thenReturn(SECOND_SERIES_LABEL);
        when(secondSeriesSegment.getGradientType()).thenReturn(secondSeriesTrend);
        when(secondSeriesSegment.getUnits()).thenReturn("%");
        when(secondSeriesSegment.getGradient()).thenReturn(secondSeriesEndValue - secondSeriesStartValue);

        final GraphSegment graphSegment = mock(GraphSegment.class);
        final SeriesSegment higherSeriesAtStart = higherSeriesOf(firstSeriesSegment, firstSeriesStartValue,
                secondSeriesSegment, secondSeriesStartValue);
        when(graphSegment.getHigherSeriesAtStart()).thenReturn(higherSeriesAtStart);
        final SeriesSegment higherSeriesAtEnd = higherSeriesOf(firstSeriesSegment, firstSeriesEndValue,
                secondSeriesSegment, secondSeriesEndValue);
        when(graphSegment.getHigherSeriesAtEnd()).thenReturn(higherSeriesAtEnd);
        when(graphSegment.isIntersecting())
                .thenReturn(null != higherSeriesAtStart && !higherSeriesAtStart.equals(higherSeriesAtEnd));
        when(graphSegment.getFirstSeriesTrend()).thenReturn(firstSeriesTrend);
        when(graphSegment.getSecondSeriesTrend()).thenReturn(secondSeriesTrend);
        when(graphSegment.getGapTrend()).thenReturn(trendFromValues(firstSeriesStartValue, firstSeriesEndValue,
                secondSeriesStartValue, secondSeriesEndValue));
        when(graphSegment.getGapBetweenSeriesEndValues())
                .thenReturn(Math.abs(secondSeriesEndValue - firstSeriesEndValue));
        when(graphSegment.isGlobalMaximumGapAtSegmentEnd()).thenReturn(maximumGap);
        when(graphSegment.isGlobalMinimumGapAtSegmentEnd()).thenReturn(minimumGap);
        prepareGraphSegment(graphSegment, firstSeriesSegment, secondSeriesSegment);
        this.graphSegments.add(graphSegment);
    }

    private void givenTheGraphHasIntersections() {
        when(this.model.isIntersecting()).thenReturn(true);
    }

    private SeriesSegment higherSeriesOf(final SeriesSegment firstSeriesSegment, final double firstSeriesValue,
            final SeriesSegment secondSeriesSegment, final double secondSeriesValue) {
        SeriesSegment seriesSegment = null;
        if (firstSeriesValue > secondSeriesValue) {
            seriesSegment = firstSeriesSegment;
        } else if (secondSeriesValue > firstSeriesValue) {
            seriesSegment = secondSeriesSegment;
        }
        return seriesSegment;
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
        final double differenceAtStart = Math.abs(secondSeriesStartValue - firstSeriesStartValue);
        final double differenceAtEnd = Math.abs(secondSeriesEndValue - firstSeriesEndValue);
        if (differenceAtStart > differenceAtEnd) {
            gapTrend = GapTrend.CONVERGING;
        } else if (differenceAtStart < differenceAtEnd) {
            gapTrend = GapTrend.DIVERGING;
        }
        return gapTrend;
    }

    private void whenTheSegmentIsSummarised() {
        whenTheSegmentIsSummarised(0);
    }

    private void whenTheSegmentIsSummarised(final int index) {
        this.summary = this.graphSegmentSummaryService.getSegmentSummaries(this.model).get(index);
        this.summaryText = REALISER.realise(this.summary).toString();
        LOG.debug(this.summaryText);
    }

    private void prepareGraphSegment(final GraphSegment graphSegment, final SeriesSegment firstSeriesSegment,
            final SeriesSegment secondSeriesSegment) {
        when(graphSegment.getSeriesSegment(0)).thenReturn(firstSeriesSegment);
        when(graphSegment.indexOf(firstSeriesSegment)).thenReturn(0);
        when(graphSegment.getSeriesSegment(1)).thenReturn(secondSeriesSegment);
        when(graphSegment.indexOf(secondSeriesSegment)).thenReturn(1);
        when(graphSegment.getStartTime()).thenReturn(START);
        when(graphSegment.getEndTime()).thenReturn(END);
    }

    private void thenTheSummaryIs(final String expectedString) {
        final String expectedWithoutCommas = expectedString.replaceAll(",", "");
        final String summaryWithOutCommas = this.summaryText.replaceAll(",", "");
        assertEquals(expectedWithoutCommas, summaryWithOutCommas);
        // assertEquals(expectedString, this.summaryText);
    }
}
