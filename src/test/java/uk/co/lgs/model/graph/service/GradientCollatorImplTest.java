package uk.co.lgs.model.graph.service;

import java.util.Arrays;

import org.junit.Test;
import org.mockito.InjectMocks;

import uk.co.lgs.model.graph.collator.exception.CollatorException;
import uk.co.lgs.model.segment.exception.SegmentAppendException;
import uk.co.lgs.model.segment.exception.SegmentCategoryNotFoundException;
import uk.co.lgs.model.segment.graph.category.GapTrend;
import uk.co.lgs.model.segment.graph.category.GraphSegmentGradient;

public class GradientCollatorImplTest extends AbstractModelCollatorTest {

    @InjectMocks
    private final GradientCollatorImpl underTest = new GradientCollatorImpl();

    @Test
    public void testShouldCollate() throws SegmentCategoryNotFoundException, CollatorException, SegmentAppendException {
        givenAModelContainingSegmentCategoriesAndGapTrends(
                Arrays.asList(GraphSegmentGradient.NEGATIVE_NEGATIVE, GraphSegmentGradient.NEGATIVE_NEGATIVE),
                Arrays.asList(GapTrend.PARALLEL, GapTrend.DIVERGING));
        whenTheModelIsCollated();
        thenTheModelSaysItIsCollated(true);
        thenTheModelHasSegments(Arrays.asList(GraphSegmentGradient.NEGATIVE_NEGATIVE));
        // TODO: This only makes sense if we have a ruleset for recalculating
        // gapTrends of collated segments
        thenTheModelHasGaps(Arrays.asList(GapTrend.PARALLEL));// ,
                                                              // GapTrend.DIVERGING));
        thenTheModelHasLengths(Arrays.asList(2));
    }

    @Test
    public void testGraphSegmentGradientCategoriesNotEqual() throws SegmentAppendException, CollatorException {
        givenAModelContainingSegmentCategoriesAndGapTrends(
                Arrays.asList(GraphSegmentGradient.NEGATIVE_NEGATIVE, GraphSegmentGradient.NEGATIVE_POSITIVE),
                Arrays.asList(GapTrend.DIVERGING, GapTrend.DIVERGING));
        whenTheModelIsCollated();
        thenTheModelSaysItIsCollated(false);
        thenTheModelHasSegments(
                Arrays.asList(GraphSegmentGradient.NEGATIVE_NEGATIVE, GraphSegmentGradient.NEGATIVE_POSITIVE));
        thenTheModelHasGaps(Arrays.asList(GapTrend.DIVERGING, GapTrend.DIVERGING));
        thenTheModelHasLengths(Arrays.asList(1, 1));

    }

    @Test
    public void testSeriesIntersect() throws SegmentAppendException, CollatorException {
        givenAModelContainingSegmentCategoriesAndGapTrends(
                Arrays.asList(GraphSegmentGradient.NEGATIVE_NEGATIVE_INTERSECTING,
                        GraphSegmentGradient.NEGATIVE_NEGATIVE_INTERSECTING),
                Arrays.asList(GapTrend.DIVERGING, GapTrend.DIVERGING));
        whenTheModelIsCollated();
        thenTheModelSaysItIsCollated(false);
        thenTheModelHasSegments(Arrays.asList(GraphSegmentGradient.NEGATIVE_NEGATIVE_INTERSECTING,
                GraphSegmentGradient.NEGATIVE_NEGATIVE_INTERSECTING));
        thenTheModelHasGaps(Arrays.asList(GapTrend.DIVERGING, GapTrend.DIVERGING));
        thenTheModelHasLengths(Arrays.asList(1, 1));
    }

    @Override
    protected void whenTheModelIsCollated() throws SegmentCategoryNotFoundException, CollatorException {
        this.outputGraphModel = this.underTest.collate(this.inputGraphModel);
    }

}
