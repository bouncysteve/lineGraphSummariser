package uk.co.lgs.text.service.segment.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

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
     * second series. 2)
     */

    @Before
    public void beforeEachTest() {
        when(this.graphSegment.getStartTime()).thenReturn(START_TIME);
        when(this.graphSegment.getEndTime()).thenReturn(END_TIME);
    }

    @Test
    public void testZERO_ZERO() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.NEVER);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
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
        thenTheSummaryDoesNotMentionAnIntersection();
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testZERO_POSITIVE() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.NEVER);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryDoesNotMentionAnIntersection();
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testNEGATIVE_POSITIVE() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.NEVER);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryDoesNotMentionAnIntersection();
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testPOSITIVE_STEEP_POSITIVE() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.NEVER);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryDoesNotMentionAnIntersection();
        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.firstSeriesSegment);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testPOSITIVE_POSITIVE_STEEP() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.NEVER);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryDoesNotMentionAnIntersection();
        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.secondSeriesSegment);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testPOSITIVE_POSITIVE_PARALLEL() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.NEVER);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryDoesNotMentionAnIntersection();
        andTheSummaryNotesThatTheSeriesAreParallel();
    }

    @Test
    public void testZERO_NEGATIVE() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.NEVER);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryDoesNotMentionAnIntersection();
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testNEGATIVE_STEEP_NEGATIVE() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.NEVER);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryDoesNotMentionAnIntersection();
        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.firstSeriesSegment);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testNEGATIVE_NEGATIVE_STEEP() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.NEVER);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryDoesNotMentionAnIntersection();
        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.secondSeriesSegment);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testNEGATIVE_NEGATIVE_PARALLEL() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.NEVER);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryDoesNotMentionAnIntersection();
        andTheSummaryNotesThatTheSeriesAreParallel();
    }

    @Test
    public void testPOSITIVE_NEGATIVE() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.NEVER);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryDoesNotMentionAnIntersection();
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testZERO_ZERO_INTERSECTING() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.START);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryMentionsTheValueAtTheIntersection(FIRST_SERIES_START);
        andTheSummaryNotesThatTheSeriesAreParallel();
    }

    @Test
    public void testNEGATIVE_ZERO_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.START);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryMentionsTheValueAtTheIntersection(FIRST_SERIES_START);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testPOSITIVE_ZERO_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.START);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryMentionsTheValueAtTheIntersection(FIRST_SERIES_START);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testZERO_POSITIVE_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.START);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryMentionsTheValueAtTheIntersection(FIRST_SERIES_START);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testNEGATIVE_POSITIVE_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.START);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryMentionsTheValueAtTheIntersection(FIRST_SERIES_START);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testPOSITIVE_STEEP_POSITIVE_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.START);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryMentionsTheValueAtTheIntersection(FIRST_SERIES_START);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.firstSeriesSegment);
    }

    @Test
    public void testPOSITIVE_POSITIVE_STEEP_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.START);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.secondSeriesSegment);
        andTheSummaryMentionsTheValueAtTheIntersection(FIRST_SERIES_START);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testPOSITIVE_POSITIVE_PARALLEL_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.START);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryMentionsTheValueAtTheIntersection(FIRST_SERIES_START);
        andTheSummaryNotesThatTheSeriesAreParallel();
    }

    @Test
    public void testZERO_NEGATIVE_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.START);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryMentionsTheValueAtTheIntersection(FIRST_SERIES_START);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testNEGATIVE_STEEP_NEGATIVE_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.START);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryMentionsTheValueAtTheIntersection(FIRST_SERIES_START);
        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.firstSeriesSegment);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testNEGATIVE_NEGATIVE_STEEP_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.START);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryMentionsTheValueAtTheIntersection(FIRST_SERIES_START);
        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.secondSeriesSegment);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testNEGATIVE_NEGATIVE_PARALLEL_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.START);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryMentionsTheValueAtTheIntersection(FIRST_SERIES_START);
        andTheSummaryNotesThatTheSeriesAreParallel();
    }

    @Test
    public void testPOSITIVE_NEGATIVE_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.START);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryMentionsTheValueAtTheIntersection(FIRST_SERIES_START);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testNEGATIVE_ZERO_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.END);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryMentionsTheValueAtTheIntersection(FIRST_SERIES_START - 1);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testPOSITIVE_ZERO_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.END);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryMentionsTheValueAtTheIntersection(FIRST_SERIES_START + 1);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testZERO_POSITIVE_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.END);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryMentionsTheValueAtTheIntersection(FIRST_SERIES_START);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testNEGATIVE_POSITIVE_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.END);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryMentionsTheValueAtTheIntersection(FIRST_SERIES_START - 1);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testPOSITIVE_STEEP_POSITIVE_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.END);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryMentionsTheValueAtTheIntersection(FIRST_SERIES_START + 1);
        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.firstSeriesSegment);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testPOSITIVE_POSITIVE__STEEP_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.END);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryMentionsTheValueAtTheIntersection(FIRST_SERIES_START + 1);
        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.secondSeriesSegment);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testPOSITIVE_POSITIVE_PARALLEL_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.END);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryMentionsTheValueAtTheIntersection(FIRST_SERIES_START + 1);
        andTheSummaryNotesThatTheSeriesAreParallel();
    }

    @Test
    public void testZERO_NEGATIVE_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.END);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryMentionsTheValueAtTheIntersection(FIRST_SERIES_START);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testNEGATIVE_STEEP_NEGATIVE_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.END);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryMentionsTheValueAtTheIntersection(FIRST_SERIES_START - 1);
        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.firstSeriesSegment);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testNEGATIVE_NEGATIVE_STEEP_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.END);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryMentionsTheValueAtTheIntersection(FIRST_SERIES_START - 1);
        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.secondSeriesSegment);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testNEGATIVE_NEGATIVE_PARALLEL_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.END);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryMentionsTheValueAtTheIntersection(FIRST_SERIES_START - 1);
        andTheSummaryNotesThatTheSeriesAreParallel();
    }

    @Test
    public void testPOSITIVE_NEGATIVE_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.END);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryMentionsTheValueAtTheIntersection(FIRST_SERIES_START + 1);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testNEGATIVE_ZERO_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.WITHIN);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryDoesNotMentionTheValueAtTheIntersection(this.firstSeriesSegment.getStartValue()
                + (this.firstSeriesSegment.getEndValue() - this.firstSeriesSegment.getStartValue()) / 2);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testPOSITIVE_ZERO_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.WITHIN);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryDoesNotMentionTheValueAtTheIntersection(this.firstSeriesSegment.getStartValue()
                + (this.firstSeriesSegment.getEndValue() - this.firstSeriesSegment.getStartValue()) / 2);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testZERO_POSITIVE_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.WITHIN);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryDoesNotMentionTheValueAtTheIntersection(FIRST_SERIES_START);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testNEGATIVE_POSITIVE_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.WITHIN);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryDoesNotMentionTheValueAtTheIntersection(this.firstSeriesSegment.getStartValue()
                + (this.firstSeriesSegment.getEndValue() - this.firstSeriesSegment.getStartValue()) / 2);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testPOSITIVE_STEEP_POSITIVE_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.WITHIN);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryDoesNotMentionTheValueAtTheIntersection(this.firstSeriesSegment.getStartValue()
                + (this.firstSeriesSegment.getEndValue() - this.firstSeriesSegment.getStartValue()) / 2);
        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.firstSeriesSegment);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testPOSITIVE_POSITIVE_STEEP_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.WITHIN);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryDoesNotMentionTheValueAtTheIntersection(this.firstSeriesSegment.getStartValue()
                + (this.firstSeriesSegment.getEndValue() - this.firstSeriesSegment.getStartValue()) / 2);
        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.secondSeriesSegment);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testPOSITIVE_POSITIVE_PARALLEL_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.WITHIN);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryDoesNotMentionTheValueAtTheIntersection(this.firstSeriesSegment.getStartValue()
                + (this.firstSeriesSegment.getEndValue() - this.firstSeriesSegment.getStartValue()) / 2);
        andTheSummaryNotesThatTheSeriesAreParallel();
    }

    @Test
    public void testZERO_NEGATIVE_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.WITHIN);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryDoesNotMentionTheValueAtTheIntersection(FIRST_SERIES_START);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testNEGATIVE_STEEP_NEGATIVE_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.WITHIN);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryDoesNotMentionTheValueAtTheIntersection(this.firstSeriesSegment.getStartValue()
                + (this.firstSeriesSegment.getEndValue() - this.firstSeriesSegment.getStartValue()) / 2);
        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.firstSeriesSegment);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testNEGATIVE_NEGATIVE_STEEP_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.WITHIN);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryDoesNotMentionTheValueAtTheIntersection(this.firstSeriesSegment.getStartValue()
                + (this.firstSeriesSegment.getEndValue() - this.firstSeriesSegment.getStartValue()) / 2);
        thenTheSummaryMentionsThatTheSeriesIsSteeper(this.secondSeriesSegment);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    @Test
    public void testNEGATIVE_NEGATIVE_PARALLEL_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.WITHIN);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryDoesNotMentionTheValueAtTheIntersection(this.firstSeriesSegment.getStartValue()
                + (this.firstSeriesSegment.getEndValue() - this.firstSeriesSegment.getStartValue()) / 2);
        andTheSummaryNotesThatTheSeriesAreParallel();
    }

    @Test
    public void testPOSITIVE_NEGATIVE_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.WITHIN);
        whenTheGraphSegmentIsSummarised();
        thenTheSummaryStartsByDescribingTheTimescale(START_TIME, END_TIME);
        thenTheSummaryMentionsTheIntersection();
        andTheSummaryDoesNotMentionTheValueAtTheIntersection(this.firstSeriesSegment.getStartValue()
                + (this.firstSeriesSegment.getEndValue() - this.firstSeriesSegment.getStartValue()) / 2);
        andTheSummaryDoesNotClaimThatTheSeriesAreParallel();
    }

    private void thenTheSummaryMentionsThatTheSeriesIsSteeper(SeriesSegment firstSeriesSegment) {
        // TODO Auto-generated method stub

    }

    private void thenTheSummaryDoesNotMentionAnIntersection() {
        assertFalse("Should not mention intersections",
                realiser.realise(this.summary).toString().contains("intersects"));
    }

    private void andTheSummaryNotesThatTheSeriesAreParallel() {
        // TODO Auto-generated method stub

    }

    private void andTheSummaryDoesNotClaimThatTheSeriesAreParallel() {
        // TODO Auto-generated method stub

    }

    private void andTheSummaryDoesNotMentionTheValueAtTheIntersection(double d) {
        // TODO Auto-generated method stub

    }

    private void andTheSummaryMentionsTheValueAtTheIntersection(double d) {
        // TODO Auto-generated method stub

    }

    private void thenTheSummaryMentionsTheIntersection() {
        // TODO Auto-generated method stub

    }

    private void whenTheGraphSegmentIsSummarised() throws SegmentCategoryNotFoundException {
        this.summary = this.underTest.getSummary(this.graphSegment);
        this.summaryText = realiser.realise(this.summary).toString();
    }

    private void thenTheSummaryStartsByDescribingTheTimescale(String startTime, String endTime) {
        assertEquals("Between " + startTime + " and " + endTime + " nothing happened.", this.summaryText);
    }
}
