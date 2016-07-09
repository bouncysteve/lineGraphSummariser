package uk.co.lgs.text.service.segment.graph;

import static org.junit.Assert.assertEquals;
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
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.realiser.english.Realiser;
import uk.co.lgs.model.graph.GraphModel;
import uk.co.lgs.model.segment.graph.GraphSegment;
import uk.co.lgs.model.segment.series.SeriesSegment;
import uk.co.lgs.segment.graph.AbstractGraphSegmentTest;
import uk.co.lgs.text.service.label.LabelService;
import uk.co.lgs.text.service.synonym.Constants;
import uk.co.lgs.text.service.synonym.SynonymService;
import uk.co.lgs.text.service.value.ValueService;

public class GraphSegmentSummaryServiceImplTest extends AbstractGraphSegmentTest {
    private static final Lexicon LEXICON = Lexicon.getDefaultLexicon();
    private static final Realiser REALISER = new Realiser(LEXICON);
    private static final String END = "April 2016";
    private static final String FIRST_SERIES_LABEL = "Cost of sunglasses";
    private static final String SECOND_SERIES_LABEL = "Sales of doughnuts";
    private static final Logger LOG = LoggerFactory.getLogger(GraphSegmentSummaryServiceImplTest.class);
    private String summaryText;
    private DocumentElement summary;

    @Mock
    private LabelService labelService;
    @Mock
    private SynonymService synonymService;
    @Mock
    private ValueService valueService;

    @InjectMocks
    private GraphSegmentSummaryService graphSegmentSummaryService;

    @Mock
    private GraphModel model;

    @Mock
    private SeriesSegment firstSeriesSegment;
    @Mock
    private SeriesSegment secondSeriesSegment;

    @Mock
    private GraphSegment graphSegment;

    private List<NPPhraseSpec> labels;
    private final NLGFactory nlgFactory = new NLGFactory(LEXICON);

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

        prepareGraphSegment();
        prepareSynonymService();
    }

    /**
     * Until <END> <higher series> rises [conjunction] <lower series> falls,
     * [so] the gap between them increases to <value>,(if first time mentioned:)
     * its maximum value].
     */
    @Test
    public void testOppositeTrendsDiverging() {
        givenFirstSeriesWithValues(50, 70);
        givenSecondSeriesWithValues(30, -100);
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
    public void testOppositeTrendsDivergingToGlobalMaximumGap() { //
    }

    /**
     * Until <END> <higher series> rises [conjunction] <lower series> falls,
     * [so] the gap between them increases to <value>,(if first time mentioned:)
     * its maximum value]. {NB. Need to think about an equal gap with opposite
     * order of series}
     */
    @Test
    public void testOppositeTrendsDivergingToGlobalMaximumGapFirstTimeMentioned() {
        //
    }

    /**
     * Until <END><higher series> falls [conjunction] <lower series> rises, [so]
     * the gap between them decreases to <value>,(if first time mentioned:) its
     * minimum value] {NB. Do not mention minimum gap if there are any
     * intersections}
     */
    @Test
    public void testOppositeTrendsConverging() {
        //
    }

    /**
     * Until <END><higher series> falls [conjunction] <lower series> rises, [so]
     * the gap between them decreases to <value>,(if first time mentioned:) its
     * minimum value] {NB. Do not mention minimum gap if there are any
     * intersections}
     */
    @Test
    public void testOppositeTrendsConvergingToGlobalMinimumGap() { //
    }

    /**
     * Until <END><higher series> falls [conjunction] <lower series> rises, [so]
     * the gap between them decreases to <value>,(if first time mentioned:) its
     * minimum value] {NB. Do not mention minimum gap if there are any
     * intersections}
     */
    @Test
    public void testOppositeTrendsConvergingToGlobalMinimumGapFirstTimeMentioned() {
        //
    }

    /**
     * Until <END><seriesA> rises to value [conjunction] <seriesB> falls to the
     * same value.
     */
    @Test
    public void testOppositeTrendsConvergingToIntersectionAtEndOfSection() {

    }

    /**
     * <higher series> falls [conjunction] <lower series> rises, and they cross,
     * so that at END <higherSeriesAtEnd> is higher with <value>, while
     * <otherSeries> has <value>
     */
    @Test
    public void testOppositeTrendsConvergingToIntersectionDuringSection() {

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
    public void testBothFallingConverging() {
        //

    }

    /**
     * Until <END> both <higher series> and <lower series> fall. Because <series
     * with steepest difference> falls more steeply, the gap between them
     * decreases to <value>
     */
    @Test
    public void testBothFallingConvergingToGlobalMinimumGap() {
        //

    }

    /**
     * Until <END> both <higher series> and <lower series> fall. Because <series
     * with steepest difference> falls more steeply, the gap between them
     * decreases to <value>, its minimum value {NB. Do not mention minimum gap
     * if there are any intersections in the graph}
     */
    @Test
    public void testBothFallingConvergingToGlobalMinimumGapFirstTimeMentioned() {
        //

    }

    /**
     * Until <END> both <higher series> and <lower series> fall. Because <series
     * with steepest difference> falls more steeply, the gap between them
     * decreases to <value>.
     */
    @Test
    public void testBothFallingConvergingToGlobalMinimumGapWhenGraphHasIntersections() {
        //

    }

    /**
     * Until <END> both <higher series> and <lower series> fall. Because <series
     * with steepest difference> falls more steeply, the gap between them
     * increases.
     */
    @Test
    public void testBothFallingDiverging() {
        //

    }

    /**
     * Until <END> both <higher series> and <lower series> fall. Because <series
     * with steepest difference> falls more steeply, the gap between them
     * increases to <value>.
     */
    @Test
    public void testBothFallingDivergingToGlobalMinimumGap() {

    }

    /**
     * Until <END> both <higher series> and <lower series> fall. Because <series
     * with steepest difference> falls more steeply, the gap between them
     * increases to <value>, its maximum value.
     */
    @Test
    public void testBothFallingDivergingToGlobalMinimumGapFirstTimeMentioned() {
        //

    }

    /**
     * Until <END> both <higherSeries> and <lowerSeries> fall [at the same
     * rate], consequently the gap between them remains <value>.
     */
    @Test
    public void testBothFallingConstant() {
        //
    }

    /**
     * Both <seriesA> and <SeriesB> fall to <value> at <END>.
     */
    @Test
    public void testBothFallingConvergingToIntersectionAtEndOfSection() {
        //
    }

    /**
     * Both <higher series> and <lower series> fall. Because <series with
     * steepest difference> falls more steeply they cross, so that at END
     * <higherSeriesAtEnd> is higher with <value>, while <otherSeries> has
     * <value>
     */
    @Test
    public void testBothFallingConvergingToIntersectionDuringSection() {

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
    public void testBothRisingConverging() {
        //

    }

    /**
     * Until <END> both <higher series> and <lower series> rise. Because <series
     * with steepest difference> rises more steeply, the gap between them
     * decreases to <value>
     */
    @Test
    public void testBothRisingConvergingToGlobalMinimumGap() {
        //

    }

    /**
     * Until <END> both <higher series> and <lower series> rise. Because <series
     * with steepest difference> rises more steeply, the gap between them
     * decreases to <value>, its minimum value {NB. Do not mention minimum gap
     * if there are any intersections in the graph}
     */
    @Test
    public void testBothRisingConvergingToGlobalMinimumGapFirstTimeMentioned() {
        //

    }

    /**
     * Until <END> both <higher series> and <lower series> rise. Because <series
     * with steepest difference> rises more steeply, the gap between them
     * decreases to <value>.
     */
    @Test
    public void testBothRisingConvergingToGlobalMinimumGapWhenGraphHasIntersections() {
        //

    }

    /**
     * Until <END> both <higher series> and <lower series> rise. Because <series
     * with steepest difference> rises more steeply, the gap between them
     * increases.
     */
    @Test
    public void testBothRisingDiverging() {
        //

    }

    /**
     * Until <END> both <higher series> and <lower series> rise. Because <series
     * with steepest difference> rises more steeply, the gap between them
     * increases to <value>.
     */
    @Test
    public void testBothRisingDivergingToGlobalMinimumGap() {

    }

    /**
     * Until <END> both <higher series> and <lower series> rise. Because <series
     * with steepest difference> rises more steeply, the gap between them
     * increases to <value>, its maximum value.
     */
    @Test
    public void testBothRisingDivergingToGlobalMinimumGapFirstTimeMentioned() {
        //

    }

    /**
     * Until <END> both <higherSeries> and <lowerSeries> rise [at the same
     * rate], consequently the gap between them remains <value>.
     */
    @Test
    public void testBothRisingConstant() {
        //
    }

    /**
     * Both <seriesA> and <SeriesB> rise to <value> at <END>.
     */
    @Test
    public void testBothRisingConvergingToIntersectionAtEndOfSection() {
        //
    }

    /**
     * Both <higher series> and <lower series> rise. Because <series with
     * steepest difference> rises more steeply they cross, so that at END
     * <higherSeriesAtEnd> is higher with <value>, while <otherSeries> has
     * <value>
     */
    @Test
    public void testBothRisingConvergingToIntersectionDuringSection() {

    }

    /**
     * Following an intersection at the end of the previous segment there should
     * be an extra sentence: <higherSeriesAtEnd> is higher with <value> at
     * <END>, while <lowerSeriesAtEnd> has <value>.
     */
    @Test
    public void testSectionWhereBothSeriesStartOnSameValue() {

    }

    private void givenFirstSeriesWithValues(final double startValue, final double endValue) {
        when(this.firstSeriesSegment.getStartValue()).thenReturn(startValue);
        when(this.firstSeriesSegment.getEndValue()).thenReturn(endValue);
    }

    private void givenSecondSeriesWithValues(final double startValue, final double endValue) {
        when(this.secondSeriesSegment.getStartValue()).thenReturn(startValue);
        when(this.secondSeriesSegment.getEndValue()).thenReturn(endValue);
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
        when(this.graphSegment.getEndTime()).thenReturn(END);
    }

    private void prepareSynonymService() {
        when(this.synonymService.getSynonym(Constants.FALL)).thenReturn("fall");
        when(this.synonymService.getSynonym(Constants.RISE)).thenReturn("rise");
        when(this.synonymService.getSynonym(Constants.CONSTANT)).thenReturn("be constant");
        when(this.synonymService.getSynonym(Constants.CONVERGE)).thenReturn("decrease");
        when(this.synonymService.getSynonym(Constants.DIVERGE)).thenReturn("increase");
        when(this.synonymService.getSynonym(Constants.PARALLEL)).thenReturn("stay the same");
    }

    private void thenTheSummaryIs(final String string) {
        assertEquals(string, this.summaryText);
    }
}
