package uk.co.lgs.text.service.segment.graph;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simplenlg.framework.DocumentElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.realiser.english.Realiser;
import uk.co.lgs.model.gradient.GradientType;
import uk.co.lgs.model.segment.exception.SegmentCategoryNotFoundException;
import uk.co.lgs.model.segment.graph.GraphSegment;
import uk.co.lgs.model.segment.graph.Intersection;
import uk.co.lgs.model.segment.series.SeriesSegment;
import uk.co.lgs.segment.graph.AbstractGraphSegmentTest;
import uk.co.lgs.text.service.segment.series.SeriesSegmentSummaryService;

public class GraphSegmentSummaryServiceImplTest extends AbstractGraphSegmentTest {

    private static final String FIRST_SERIES_LABEL = "Sales of soap";
    private static final String SECOND_SERIES_LABEL = "Price of pyjamas";
    private static double LOW_VALUE = 2.5;
    private static double HIGH_VALUE = 7.0;
    private static final String START_TIME = "2012";
    private static final String END_TIME = "2013";
    private static final Logger LOG = LoggerFactory.getLogger(GraphSegmentSummaryServiceImplTest.class);
    private static Lexicon lexicon = Lexicon.getDefaultLexicon();
    private static NLGFactory nlgFactory = new NLGFactory(lexicon);
    private static Realiser realiser = new Realiser(lexicon);

    @Mock
    private SeriesSegmentSummaryService seriesSegmentSummaryService;

    @Mock
    private GraphSegment graphSegment;

    @InjectMocks
    private GraphSegmentSummaryService underTest = new GraphSegmentSummaryServiceImpl();

    private DocumentElement summary;
    private String summaryText;

    /*
     * TODO: 1) Add tests for When the gradient types are the same (and
     * non-zero). First series should be steeper, less steep and parallel to
     * second series.
     */

    @Before
    public void beforeEachTest() {
        when(this.firstSeriesSegment.getLabel()).thenReturn(FIRST_SERIES_LABEL);
        when(this.secondSeriesSegment.getLabel()).thenReturn(SECOND_SERIES_LABEL);
        when(this.graphSegment.getSeriesSegment(0)).thenReturn(this.firstSeriesSegment);
        when(this.graphSegment.getSeriesSegment(1)).thenReturn(this.secondSeriesSegment);
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
    public void testZERO_ZERO() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithConstantValue(LOW_VALUE);
        givenSecondSeriesWithConstantValue(HIGH_VALUE);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryDescribesTheSeriesRemainingConstantAtValue(this.firstSeriesSegment, LOW_VALUE);
        thenTheSummaryDescribesTheSeriesRemainingConstantAtValue(this.secondSeriesSegment, HIGH_VALUE);
    }

    private void givenSecondSeriesWithConstantValue(double value) {
        when(this.seriesSegmentSummaryService.getSummary(this.firstSeriesSegment))
                .thenReturn(nlgFactory.createClause(SECOND_SERIES_LABEL, "is", "constant at " + value));
    }

    private void givenFirstSeriesWithConstantValue(double value) {
        when(this.seriesSegmentSummaryService.getSummary(this.secondSeriesSegment))
                .thenReturn(nlgFactory.createClause(FIRST_SERIES_LABEL, "is", "constant at " + value));
    }

    @Test
    public void testNEGATIVE_ZERO() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.NEVER);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryContains(FIRST_SERIES_LABEL + "fall from " + LOW_VALUE + " to " + HIGH_VALUE);
        thenTheSummaryContains(SECOND_SERIES_LABEL + "constant at" + LOW_VALUE);
    }

    private void thenTheSummaryContains(String string) {
        // assertTrue(realiser.realise(this.summary).toString().contains(string));

    }

    @Test
    public void testPOSITIVE_ZERO() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.NEVER);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
    }

    @Test
    public void testZERO_POSITIVE() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.NEVER);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
    }

    @Test
    public void testNEGATIVE_POSITIVE() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.NEVER);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
    }

    @Test
    public void testPOSITIVE_STEEP_POSITIVE() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.NEVER);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.firstSeriesSegment);

    }

    @Test
    public void testPOSITIVE_POSITIVE_STEEP() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.NEVER);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.secondSeriesSegment);

    }

    @Test
    public void testPOSITIVE_POSITIVE_PARALLEL() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.NEVER);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

    }

    @Test
    public void testZERO_NEGATIVE() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.NEVER);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

    }

    @Test
    public void testNEGATIVE_STEEP_NEGATIVE() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.NEVER);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.firstSeriesSegment);

    }

    @Test
    public void testNEGATIVE_NEGATIVE_STEEP() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.NEVER);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.secondSeriesSegment);

    }

    @Test
    public void testNEGATIVE_NEGATIVE_PARALLEL() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.NEVER);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

    }

    @Test
    public void testPOSITIVE_NEGATIVE() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.NEVER);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

    }

    @Test
    public void testZERO_ZERO_INTERSECTING() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.START);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

        thenTheSummaryMentionsTheSeriesIntersectAtTheStart();
    }

    @Test
    public void testNEGATIVE_ZERO_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.START);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

        thenTheSummaryMentionsTheSeriesIntersectAtTheStart();
    }

    @Test
    public void testPOSITIVE_ZERO_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.START);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

        thenTheSummaryMentionsTheSeriesIntersectAtTheStart();
    }

    @Test
    public void testZERO_POSITIVE_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.START);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

        thenTheSummaryMentionsTheSeriesIntersectAtTheStart();
    }

    @Test
    public void testNEGATIVE_POSITIVE_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.START);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

        thenTheSummaryMentionsTheSeriesIntersectAtTheStart();
    }

    @Test
    public void testPOSITIVE_STEEP_POSITIVE_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.START);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

        thenTheSummaryMentionsTheSeriesIntersectAtTheStart();
        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.firstSeriesSegment);
    }

    @Test
    public void testPOSITIVE_POSITIVE_STEEP_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.START);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.secondSeriesSegment);
        thenTheSummaryMentionsTheSeriesIntersectAtTheStart();
    }

    @Test
    public void testPOSITIVE_POSITIVE_PARALLEL_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.START);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

        thenTheSummaryMentionsTheSeriesIntersectAtTheStart();
    }

    @Test
    public void testZERO_NEGATIVE_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.START);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

        thenTheSummaryMentionsTheSeriesIntersectAtTheStart();
    }

    @Test
    public void testNEGATIVE_STEEP_NEGATIVE_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.START);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

        thenTheSummaryMentionsTheSeriesIntersectAtTheStart();
        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.firstSeriesSegment);

    }

    @Test
    public void testNEGATIVE_NEGATIVE_STEEP_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.START);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

        thenTheSummaryMentionsTheSeriesIntersectAtTheStart();
        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.secondSeriesSegment);

    }

    @Test
    public void testNEGATIVE_NEGATIVE_PARALLEL_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.START);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

        thenTheSummaryMentionsTheSeriesIntersectAtTheStart();
    }

    @Test
    public void testPOSITIVE_NEGATIVE_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.START);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

        thenTheSummaryMentionsTheSeriesIntersectAtTheStart();

    }

    @Test
    public void testNEGATIVE_ZERO_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.END);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

        thenTheSummaryMentionsTheSeriesIntersectAtTheEnd();

    }

    @Test
    public void testPOSITIVE_ZERO_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.END);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

        thenTheSummaryMentionsTheSeriesIntersectAtTheEnd();

    }

    @Test
    public void testZERO_POSITIVE_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.END);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

        thenTheSummaryMentionsTheSeriesIntersectAtTheEnd();
    }

    @Test
    public void testNEGATIVE_POSITIVE_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.END);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

        thenTheSummaryMentionsTheSeriesIntersectAtTheEnd();
    }

    @Test
    public void testPOSITIVE_STEEP_POSITIVE_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.END);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

        thenTheSummaryMentionsTheSeriesIntersectAtTheEnd();
        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.firstSeriesSegment);

    }

    @Test
    public void testPOSITIVE_POSITIVE__STEEP_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.END);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

        thenTheSummaryMentionsTheSeriesIntersectAtTheEnd();
        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.secondSeriesSegment);

    }

    @Test
    public void testPOSITIVE_POSITIVE_PARALLEL_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.END);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

        thenTheSummaryMentionsTheSeriesIntersectAtTheEnd();

    }

    @Test
    public void testZERO_NEGATIVE_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.END);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

        thenTheSummaryMentionsTheSeriesIntersectAtTheEnd();
    }

    @Test
    public void testNEGATIVE_STEEP_NEGATIVE_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.END);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

        thenTheSummaryMentionsTheSeriesIntersectAtTheEnd();
        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.firstSeriesSegment);

    }

    @Test
    public void testNEGATIVE_NEGATIVE_STEEP_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.END);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

        thenTheSummaryMentionsTheSeriesIntersectAtTheEnd();
        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.secondSeriesSegment);

    }

    @Test
    public void testNEGATIVE_NEGATIVE_PARALLEL_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.END);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

        thenTheSummaryMentionsTheSeriesIntersectAtTheEnd();
    }

    @Test
    public void testPOSITIVE_NEGATIVE_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.END);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);

        thenTheSummaryMentionsTheSeriesIntersectAtTheEnd();

    }

    @Test
    public void testNEGATIVE_ZERO_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.WITHIN);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheSeriesIntersectWithin();
    }

    @Test
    public void testPOSITIVE_ZERO_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.WITHIN);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheSeriesIntersectWithin();
    }

    @Test
    public void testZERO_POSITIVE_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.WITHIN);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheSeriesIntersectWithin();
    }

    @Test
    public void testNEGATIVE_POSITIVE_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.WITHIN);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheSeriesIntersectWithin();
    }

    @Test
    public void testPOSITIVE_STEEP_POSITIVE_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.WITHIN);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.firstSeriesSegment);
        thenTheSummaryMentionsTheSeriesIntersectWithin();
    }

    @Test
    public void testPOSITIVE_POSITIVE_STEEP_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.WITHIN);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.secondSeriesSegment);
        thenTheSummaryMentionsTheSeriesIntersectWithin();

    }

    @Test
    public void testPOSITIVE_POSITIVE_PARALLEL_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.WITHIN);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheSeriesIntersectWithin();
    }

    @Test
    public void testZERO_NEGATIVE_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.WITHIN);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheSeriesIntersectWithin();
    }

    @Test
    public void testNEGATIVE_STEEP_NEGATIVE_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.WITHIN);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.firstSeriesSegment);
        thenTheSummaryMentionsTheSeriesIntersectWithin();
    }

    @Test
    public void testNEGATIVE_NEGATIVE_STEEP_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.WITHIN);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.secondSeriesSegment);
        thenTheSummaryMentionsTheSeriesIntersectWithin();
    }

    @Test
    public void testNEGATIVE_NEGATIVE_PARALLEL_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.WITHIN);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
    }

    @Test
    public void testPOSITIVE_NEGATIVE_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.WITHIN);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheSeriesIntersectWithin();
    }

    private void whenTheGraphSegmentIsSummarised() throws SegmentCategoryNotFoundException {
        this.summary = this.underTest.getSummary(this.graphSegment);
        this.summaryText = realiser.realise(this.summary).toString();
        LOG.debug(this.summaryText);
    }

    private void thenTheSummaryStartsByDescribingTheTimescale(String startTime, String endTime) {
        assertTrue(this.summaryText.startsWith("Between " + startTime + " and " + endTime));
    }

    private void thenTheSummaryDescribesTheSeriesRemainingConstantAtValue(SeriesSegment seriesSegment, double value) {
        assertTrue(this.summaryText.contains(seriesSegment.getLabel() + " is constant at " + value));
    }

    private void thenTheSummaryMentionsTheSeriesIntersectAtTheStart() {
        // TODO Auto-generated method stub
    }

    private void thenTheSummaryMentionsTheSeriesIntersectAtTheEnd() {
        // TODO Auto-generated method stub
    }

    private void thenTheSummaryMentionsTheSeriesIntersectWithin() {
        // TODO Auto-generated method stub
    }

    private void thenTheSummaryMentionsThatTheSeriesIsSteeper(SeriesSegment firstSeriesSegment) {
        // TODO Auto-generated method stub

    }
}
