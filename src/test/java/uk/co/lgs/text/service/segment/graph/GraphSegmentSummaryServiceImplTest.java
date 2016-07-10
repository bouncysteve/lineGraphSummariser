package uk.co.lgs.text.service.segment.graph;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
import uk.co.lgs.model.graph.GraphModel;
import uk.co.lgs.model.segment.graph.GraphSegment;
import uk.co.lgs.model.segment.graph.category.GapTrend;
import uk.co.lgs.model.segment.series.SeriesSegment;
import uk.co.lgs.segment.graph.AbstractGraphSegmentTest;
import uk.co.lgs.text.service.label.LabelService;
import uk.co.lgs.text.service.synonym.Constants;
import uk.co.lgs.text.service.synonym.SynonymService;
import uk.co.lgs.text.service.value.ValueService;

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

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @Mock
    private LabelService labelService;
    @Mock
    private SynonymService synonymService;
    @Mock
    private ValueService valueService;

    @Mock
    private GraphModel model;

    @Mock
    private SeriesSegment firstSeriesSegment;
    @Mock
    private SeriesSegment secondSeriesSegment;

    @Mock
    private GraphSegment graphSegment;

    @InjectMocks
    private GraphSegmentSummaryServiceImpl graphSegmentSummaryService;

    private List<NPPhraseSpec> labels;
    private final NLGFactory nlgFactory = new NLGFactory(LEXICON);

    @Before
    public void beforeEachTest() {
        final NPPhraseSpec firstSeriesLabel = this.nlgFactory.createNounPhrase(FIRST_SERIES_LABEL);
        final NPPhraseSpec secondSeriesLabel = this.nlgFactory.createNounPhrase(SECOND_SERIES_LABEL);
        secondSeriesLabel.setPlural(true);
        this.labels = Arrays.asList(firstSeriesLabel, secondSeriesLabel);

        when(this.labelService.getLabelsForCommonUse(this.graphSegment)).thenReturn(this.labels);
        when(this.labelService.getLabelForCommonUse(this.graphSegment, this.firstSeriesSegment))
                .thenReturn(firstSeriesLabel);
        when(this.labelService.getLabelForCommonUse(this.graphSegment, this.secondSeriesSegment))
                .thenReturn(secondSeriesLabel);

        when(this.firstSeriesSegment.getLabel()).thenReturn(FIRST_SERIES_LABEL);
        when(this.secondSeriesSegment.getLabel()).thenReturn(SECOND_SERIES_LABEL);
        when(this.model.getGraphSegments()).thenReturn(Arrays.asList(this.graphSegment));
        prepareGraphSegment();
        prepareSynonymService();
    }

    /**
     * Until <END> <higher series> rises [conjunction] <lower series> falls,
     * [so] the gap between them increases to <value>,(if first time mentioned:)
     * its maximum value].
     */
    @Test
    public void testOppositeTrendsDiverging01() {
        givenSeriesValues(50, 100, 20, 10);
        whenTheSegmentIsSummarised();
        thenTheSummaryIs("Until " + END + " " + FIRST_SERIES_LABEL + " rises but " + SECOND_SERIES_LABEL
                + " fall, so the gap between them increases.");
    }

    /**
     * Until <END> <higher series> rises [conjunction] <lower series> falls,
     * [so] the gap between them increases to <value>,(if first time mentioned:)
     * its maximum value]. {NB. Need to think about an equal gap with opposite
     * order of series}
     */
    @Test
    public void testOppositeTrendsDivergingToGlobalMaximumGap02() { //
    }

    /**
     * Until <END> <higher series> rises [conjunction] <lower series> falls,
     * [so] the gap between them increases to <value>,(if first time mentioned:)
     * its maximum value]. {NB. Need to think about an equal gap with opposite
     * order of series}
     */
    @Test
    public void testOppositeTrendsDivergingToGlobalMaximumGapFirstTimeMentioned03() {
        //
    }

    /**
     * Until <END><higher series> falls [conjunction] <lower series> rises, [so]
     * the gap between them decreases to <value>,(if first time mentioned:) its
     * minimum value] {NB. Do not mention minimum gap if there are any
     * intersections}
     */
    @Test
    public void testOppositeTrendsConverging04() {
        //
    }

    /**
     * Until <END><higher series> falls [conjunction] <lower series> rises, [so]
     * the gap between them decreases to <value>,(if first time mentioned:) its
     * minimum value] {NB. Do not mention minimum gap if there are any
     * intersections}
     */
    @Test
    public void testOppositeTrendsConvergingToGlobalMinimumGap05() { //
    }

    /**
     * Until <END><higher series> falls [conjunction] <lower series> rises, [so]
     * the gap between them decreases to <value>,(if first time mentioned:) its
     * minimum value] {NB. Do not mention minimum gap if there are any
     * intersections}
     */
    @Test
    public void testOppositeTrendsConvergingToGlobalMinimumGapFirstTimeMentioned06() {
        //
    }

    /**
     * Until <END><higher series> falls [conjunction] <lower series> rises, [so]
     * the gap between them decreases to <value>,(if first time mentioned:) its
     * minimum value] {NB. Do not mention minimum gap if there are any
     * intersections}
     */
    @Test
    public void testOppositeTrendsConvergingToGlobalMinimumGapFirstTimeMentionedGraphHasIntersection07() {
        //
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
        //

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
        //

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

        when(this.graphSegment.getGapTrend()).thenReturn(trendFromValues(firstSeriesStartValue, firstSeriesEndValue,
                secondSeriesStartValue, secondSeriesEndValue));
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

        this.summary = this.graphSegmentSummaryService.getSegmentSummaries(this.model).get(0);
        this.summaryText = REALISER.realise(this.summary).toString();
        LOG.debug(this.summaryText);

    }

    private void prepareGraphSegment() {
        when(this.graphSegment.getSeriesSegment(0)).thenReturn(this.firstSeriesSegment);
        when(this.graphSegment.indexOf(this.firstSeriesSegment)).thenReturn(0);
        when(this.graphSegment.getSeriesSegment(1)).thenReturn(this.secondSeriesSegment);
        when(this.graphSegment.indexOf(this.secondSeriesSegment)).thenReturn(1);
        when(this.graphSegment.getStartTime()).thenReturn(START);
        when(this.graphSegment.getEndTime()).thenReturn(END);
    }

    private void prepareSynonymService() {
        when(this.synonymService.getSynonym(Constants.FALL)).thenReturn("fall");
        when(this.synonymService.getSynonym(Constants.RISE)).thenReturn("rise");
        when(this.synonymService.getSynonym(Constants.CONSTANT)).thenReturn("be constant");
        when(this.synonymService.getSynonym(Constants.CONVERGE)).thenReturn("decrease");
        when(this.synonymService.getSynonym(Constants.DIVERGE)).thenReturn("increase");
        when(this.synonymService.getSynonym(Constants.PARALLEL)).thenReturn("stay the same");
        when(this.synonymService.getSynonym(Constants.AT)).thenReturn("at");
        when(this.synonymService.getSynonym(Constants.UNTIL)).thenReturn("until");
        when(this.synonymService.getSynonym(Constants.BUT)).thenReturn("but");

    }

    private void thenTheSummaryIs(final String string) {
        assertEquals(string, this.summaryText);
    }
}
