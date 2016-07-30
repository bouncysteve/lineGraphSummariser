package uk.co.lgs.model.graph.service;

import java.util.Arrays;

import org.junit.Test;
import org.mockito.InjectMocks;

import uk.co.lgs.model.graph.collator.exception.CollatorException;
import uk.co.lgs.model.segment.exception.SegmentAppendException;
import uk.co.lgs.model.segment.exception.SegmentCategoryNotFoundException;
import uk.co.lgs.model.segment.graph.category.GapTrend;
import uk.co.lgs.model.segment.graph.category.GraphSegmentGradient;

public class GapAndGradientCollatorImplTest extends AbstractModelCollatorTest {

    @InjectMocks
    private final GapAndGradientCollatorImpl underTest = new GapAndGradientCollatorImpl();

    @Test
    public void testGraphSegmentTrendsNotEqual() throws SegmentAppendException, CollatorException {
        givenAModelContainingSegmentCategoriesAndGapTrends(
                Arrays.asList(GraphSegmentGradient.NEGATIVE_NEGATIVE, GraphSegmentGradient.NEGATIVE_NEGATIVE),
                Arrays.asList(GapTrend.CONVERGING, GapTrend.PARALLEL));
        whenTheModelIsCollated();
        thenTheModelSaysItIsCollated(false);
        thenTheModelHasSegments(
                Arrays.asList(GraphSegmentGradient.NEGATIVE_NEGATIVE, GraphSegmentGradient.NEGATIVE_NEGATIVE));
        thenTheModelHasGaps(Arrays.asList(GapTrend.CONVERGING, GapTrend.PARALLEL));
        thenTheModelHasLengths(Arrays.asList(1, 1));
    }

    @Test
    public void testGraphSegmentGradientCategoriesNotEqual() throws SegmentAppendException, CollatorException {
        givenAModelContainingSegmentCategoriesAndGapTrends(
                Arrays.asList(GraphSegmentGradient.NEGATIVE_NEGATIVE, GraphSegmentGradient.POSITIVE_ZERO),
                Arrays.asList(GapTrend.CONVERGING, GapTrend.DIVERGING));
        whenTheModelIsCollated();
        thenTheModelSaysItIsCollated(false);
        thenTheModelHasSegments(
                Arrays.asList(GraphSegmentGradient.NEGATIVE_NEGATIVE, GraphSegmentGradient.POSITIVE_ZERO));
        thenTheModelHasGaps(Arrays.asList(GapTrend.CONVERGING, GapTrend.DIVERGING));
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

    @Test
    public void testShouldCollate() throws SegmentAppendException, CollatorException {
        givenAModelContainingSegmentCategoriesAndGapTrends(
                Arrays.asList(GraphSegmentGradient.NEGATIVE_NEGATIVE, GraphSegmentGradient.NEGATIVE_NEGATIVE),
                Arrays.asList(GapTrend.DIVERGING, GapTrend.DIVERGING));
        whenTheModelIsCollated();
        thenTheModelSaysItIsCollated(true);
        thenTheModelHasSegments(Arrays.asList(GraphSegmentGradient.NEGATIVE_NEGATIVE));
        thenTheModelHasGaps(Arrays.asList(GapTrend.DIVERGING));
        thenTheModelHasLengths(Arrays.asList(2));
    }

    @Override
    protected void whenTheModelIsCollated() throws SegmentCategoryNotFoundException, CollatorException {
        this.outputGraphModel = this.underTest.collate(this.inputGraphModel);
    }

}
