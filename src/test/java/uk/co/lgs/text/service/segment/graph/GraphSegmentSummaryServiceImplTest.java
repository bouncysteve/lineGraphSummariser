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
                "Until April 2016 Cost of sunglasses rises but Sales of doughnuts fall, so the gap between them increases.");
    }

    @Test
    public void testOppositeTrendsDivergingToGlobalMaximumGap02() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(0d, 0d, 0d, 90d, true, false);
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(50d, 100d, 20d, 10d, true, false);
        whenTheSegmentIsSummarised(1);
        thenTheSummaryIs(
                "Until April 2016 Cost of sunglasses rises but Sales of doughnuts fall, so the gap between them increases to 90.");
    }

    @Test
    public void testOppositeTrendsDivergingToGlobalMaximumGapFirstTimeMentioned03() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(0d, 0d, 0d, 0d, false, false);
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(50d, 100d, 20d, 10d, true, false);
        whenTheSegmentIsSummarised(1);
        thenTheSummaryIs(
                "Until April 2016 Cost of sunglasses rises but Sales of doughnuts fall, so the gap between them increases to 90, its maximum value.");
    }

    @Test
    public void testOppositeTrendsConverging04() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(20d, 10d, -20d, 0d, false, false);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs(
                "Until April 2016 Cost of sunglasses falls but Sales of doughnuts rise, so the gap between them decreases.");
    }

    @Test
    public void testOppositeTrendsConvergingToGlobalMinimumGap05() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(20d, 10d, -20d, 0d, false, true);
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(20d, 10d, -20d, 0d, false, true);
        whenTheSegmentIsSummarised(1);
        thenTheSummaryIs(
                "Until April 2016 Cost of sunglasses falls but Sales of doughnuts rise, so the gap between them decreases to 10.");

    }

    @Test
    public void testOppositeTrendsConvergingToGlobalMinimumGapFirstTimeMentioned06() {
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(20d, 10d, -20d, 0d, false, false);
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(20d, 10d, -20d, 0d, false, true);
        whenTheSegmentIsSummarised(1);
        thenTheSummaryIs(
                "Until April 2016 Cost of sunglasses falls but Sales of doughnuts rise, so the gap between them decreases to 10, its minimum value.");
    }

    @Test
    public void testOppositeTrendsConvergingToGlobalMinimumGapFirstTimeMentionedGraphHasIntersection07() {
        givenTheGraphHasIntersections();
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(20d, 10d, -20d, 0d, false, false);
        givenASeriesWithValuesHasMaximumGapAndMinimumGap(20d, 10d, -20d, 0d, false, true);
        whenTheSegmentIsSummarised(1);
        thenTheSummaryIs(
                "Until April 2016 Cost of sunglasses falls but Sales of doughnuts rise, so the gap between them decreases to 10.");
    }

    /**
     * <higher series> falls [conjunction] <lower series> rises, and they cross,
     * so that at END <higherSeriesAtEnd> is higher with <value>, while
     * <otherSeries> has <value>
     */
    @Test
    public void testOppositeTrendsConvergingToIntersectionDuringSection08() {

    }

    /**
     * Until <END><seriesA> rises to value [conjunction] <seriesB> falls to the
     * same value.
     */
    @Test
    public void testOppositeTrendsConvergingToIntersectionAtEndOfSection09() {

    }

    /*********************
     * Same trends falling
     *************************************/
    /**
     * Until <END> both <higher series> and <lower series> fall. Because <series
     * with steepest difference> falls more steeply, the gap between them
     * decreases.
     */
    @Test
    public void testBothFallingConverging10a() {
        //

    }

    /**
     * Until <END> both <higher series> and <lower series> fall. Because <series
     * with steepest difference> falls more steeply, the gap between them
     * decreases to <value>
     */
    @Test
    public void testBothFallingConvergingToGlobalMinimumGap11a() {
        //

    }

    /**
     * Until <END> both <higher series> and <lower series> fall. Because <series
     * with steepest difference> falls more steeply, the gap between them
     * decreases to <value>, its minimum value {NB. Do not mention minimum gap
     * if there are any intersections in the graph}
     */
    @Test
    public void testBothFallingConvergingToGlobalMinimumGapFirstTimeMentioned12a() {
        //

    }

    /**
     * Until <END> both <higher series> and <lower series> fall. Because <series
     * with steepest difference> falls more steeply, the gap between them
     * decreases to <value>.
     */
    @Test
    public void testBothFallingConvergingToGlobalMinimumGapWhenGraphHasIntersections13a() {
        givenTheGraphHasIntersections();

    }

    /**
     * Until <END> both <higher series> and <lower series> fall. Because <series
     * with steepest difference> falls more steeply, the gap between them
     * increases.
     */
    @Test
    public void testBothFallingDiverging14a() {
        //

    }

    /**
     * Until <END> both <higher series> and <lower series> fall. Because <series
     * with steepest difference> falls more steeply, the gap between them
     * increases to <value>.
     */
    @Test
    public void testBothFallingDivergingToGlobalMaximumGap15a() {

    }

    /**
     * Until <END> both <higher series> and <lower series> fall. Because <series
     * with steepest difference> falls more steeply, the gap between them
     * increases to <value>, its maximum value.
     */
    @Test
    public void testBothFallingDivergingToGlobalMaximumGapFirstTimeMentioned16a() {
        //

    }

    /**
     * Until <END> both <higherSeries> and <lowerSeries> fall [at the same
     * rate], consequently the gap between them remains <value>.
     */
    @Test
    public void testBothFallingConstant17a() {
        //
    }

    /**
     * Both <seriesA> and <SeriesB> fall to <value> at <END>.
     */
    @Test
    public void testBothFallingConvergingToIntersectionAtEndOfSection18a() {
        //
    }

    /**
     * Both <higher series> and <lower series> fall. Because <series with
     * steepest difference> falls more steeply they cross, so that at END
     * <higherSeriesAtEnd> is higher with <value>, while <otherSeries> has
     * <value>
     */
    @Test
    public void testBothFallingConvergingToIntersectionDuringSection19a() {

    }

    /***************
     * Same trends rising
     ***********************************************************/

    /**
     * Until <END> both <higher series> and <lower series> rise. Because <series
     * with steepest difference> rises more steeply, the gap between them
     * decreases.
     */
    @Test
    public void testBothRisingConverging10b() {
        //

    }

    /**
     * Until <END> both <higher series> and <lower series> rise. Because <series
     * with steepest difference> rises more steeply, the gap between them
     * decreases to <value>
     */
    @Test
    public void testBothRisingConvergingToGlobalMinimumGap11b() {
        //

    }

    /**
     * Until <END> both <higher series> and <lower series> rise. Because <series
     * with steepest difference> rises more steeply, the gap between them
     * decreases to <value>, its minimum value {NB. Do not mention minimum gap
     * if there are any intersections in the graph}
     */
    @Test
    public void testBothRisingConvergingToGlobalMinimumGapFirstTimeMentioned12b() {
        //

    }

    /**
     * Until <END> both <higher series> and <lower series> rise. Because <series
     * with steepest difference> rises more steeply, the gap between them
     * decreases to <value>.
     */
    @Test
    public void testBothRisingConvergingToGlobalMinimumGapWhenGraphHasIntersections13b() {
        givenTheGraphHasIntersections();

    }

    /**
     * Until <END> both <higher series> and <lower series> rise. Because <series
     * with steepest difference> rises more steeply, the gap between them
     * increases.
     */
    @Test
    public void testBothRisingDiverging14b() {
        //

    }

    /**
     * Until <END> both <higher series> and <lower series> rise. Because <series
     * with steepest difference> rises more steeply, the gap between them
     * increases to <value>.
     */
    @Test
    public void testBothRisingDivergingToGlobalMaximumGap15b() {

    }

    /**
     * Until <END> both <higher series> and <lower series> rise. Because <series
     * with steepest difference> rises more steeply, the gap between them
     * increases to <value>, its maximum value.
     */
    @Test
    public void testBothRisingDivergingToGlobalMaximumGapFirstTimeMentioned16b() {
        //

    }

    /**
     * Until <END> both <higherSeries> and <lowerSeries> rise [at the same
     * rate], consequently the gap between them remains <value>.
     */
    @Test
    public void testBothRisingConstant17b() {
        //
    }

    /**
     * Both <seriesA> and <SeriesB> rise to <value> at <END>.
     */
    @Test
    public void testBothRisingConvergingToIntersectionAtEndOfSection18b() {
        //
    }

    /**
     * Both <higher series> and <lower series> rise. Because <series with
     * steepest difference> rises more steeply they cross, so that at END
     * <higherSeriesAtEnd> is higher with <value>, while <otherSeries> has
     * <value>
     */
    @Test
    public void testBothRisingConvergingToIntersectionDuringSection19b() {

    }

    /**
     * Following an intersection at the end of the previous segment there should
     * be an extra sentence: <higherSeriesAtEnd> is higher with <value> at
     * <END>, while <lowerSeriesAtEnd> has <value>.
     */
    @Test
    public void testOppositeTrendsDivergingFromSameValue20() {

    }

    @Test
    public void testOppositeTrendsDivergingFromSameValueToGlobalMaximumGap21() {

    }

    @Test
    public void testOppositeTrendsDivergingFromSameValueToGlobalMaximumGapFirstTimeMentioned22() {

    }

    @Test
    public void testBothFallingDivergingFromSameValue23a() {
        //

    }

    @Test
    public void testBothFallingDivergingToGlobalMaximumGapFromSameValue24a() {

    }

    @Test
    public void testBothFallingDivergingToGlobalMaximumGapFromSameValueFirstTimeMentioned25a() {
        //

    }

    @Test
    public void testBothFallingConstantGapFromSameValue26a() {

    }

    @Test
    public void testBothRisingDivergingFromSameValue23b() {
        //

    }

    @Test
    public void testBothRisingDivergingToGlobalMaximumGapFromSameValue24b() {

    }

    @Test
    public void testBothRisingDivergingToGlobalMaximumGapFromSameValueFirstTimeMentioned25b() {
        //

    }

    @Test
    public void testBothRisingConstantGapFromSameValue26b() {

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
        when(firstSeriesSegment.getUnits()).thenReturn("");

        final SeriesSegment secondSeriesSegment = mock(SeriesSegment.class);
        when(secondSeriesSegment.getStartValue()).thenReturn(secondSeriesStartValue);
        when(secondSeriesSegment.getEndValue()).thenReturn(secondSeriesEndValue);
        when(secondSeriesSegment.getLabel()).thenReturn(SECOND_SERIES_LABEL);
        when(secondSeriesSegment.getGradientType()).thenReturn(secondSeriesTrend);
        when(secondSeriesSegment.getUnits()).thenReturn("");

        final GraphSegment graphSegment = mock(GraphSegment.class);
        when(graphSegment.getHigherSeriesAtStart()).thenReturn(
                higherSeriesOf(firstSeriesSegment, firstSeriesStartValue, secondSeriesSegment, secondSeriesStartValue));
        when(graphSegment.getHigherSeriesAtEnd()).thenReturn(
                higherSeriesOf(firstSeriesSegment, firstSeriesEndValue, secondSeriesSegment, secondSeriesEndValue));
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
