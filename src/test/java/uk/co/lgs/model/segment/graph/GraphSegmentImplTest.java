package uk.co.lgs.model.segment.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import uk.co.lgs.model.gradient.GradientType;
import uk.co.lgs.model.segment.exception.SegmentAppendException;
import uk.co.lgs.model.segment.exception.SegmentCategoryNotFoundException;
import uk.co.lgs.model.segment.graph.category.GraphSegmentCategory;
import uk.co.lgs.model.segment.series.SeriesSegment;
import uk.co.lgs.test.AbstractTest;

public class GraphSegmentImplTest extends AbstractTest {

    private GraphSegmentImpl underTest;
    /**
     * The first series has an initial value of 5, and a gradient of either -1,
     * 0 or 1. The second series has a gradient of either -2, 0 or 2, and a
     * start value is calculated for it to intersect with the first series at
     * the required position.
     */
    private static final int FIRST_SERIES_GRADIENT_FACTOR = 1;
    private static final int SECOND_SERIES_GRADIENT_FACTOR = 2;
    private static final Double FIRST_SERIES_START = 5d;
    private static final Double DUMMY_END_VALUE = 12d;

    @Mock
    private SeriesSegment firstSeriesSegment;
    @Mock
    private SeriesSegment secondSeriesSegment;

    @Mock
    private SeriesSegment appendFirstSeriesSegment;
    @Mock
    private SeriesSegment appendSecondSeriesSegment;

    @Mock
    private SeriesSegment collatedFirstSeriesSegment;

    @Mock
    private SeriesSegment collatedSecondSeriesSegment;

    private double secondSeriesStartValue;
    private double secondSeriesEndValue;
    private double firstSeriesEndValue;

    @Before
    public void setup() {
        when(this.firstSeriesSegment.getStartValue()).thenReturn(FIRST_SERIES_START);
    }

    @Test
    public void testZERO_ZERO() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.NEVER);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesNotContainIntersection();
        andTheSeriesAreParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.ZERO_ZERO);
    }

    @Test
    public void testNEGATIVE_ZERO() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.NEVER);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesNotContainIntersection();
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.NEGATIVE_ZERO);
    }

    @Test
    public void testPOSITIVE_ZERO() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.NEVER);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesNotContainIntersection();
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.POSITIVE_ZERO);
    }

    @Test
    public void testZERO_POSITIVE() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.NEVER);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesNotContainIntersection();
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.ZERO_POSITIVE);
    }

    @Test
    public void testNEGATIVE_POSITIVE() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.NEVER);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesNotContainIntersection();
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.NEGATIVE_POSITIVE);
    }

    @Test
    public void testPOSITIVE_POSITIVE() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.NEVER);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesNotContainIntersection();
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.POSITIVE_POSITIVE);
    }

    @Test
    public void testZERO_NEGATIVE() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.NEVER);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesNotContainIntersection();
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.ZERO_NEGATIVE);
    }

    @Test
    public void testNEGATIVE_NEGATIVE() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.NEVER);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesNotContainIntersection();
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.NEGATIVE_NEGATIVE);
    }

    @Test
    public void testPOSITIVE_NEGATIVE() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.NEVER);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesNotContainIntersection();
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.POSITIVE_NEGATIVE);
    }

    @Test
    public void testZERO_ZERO_INTERSECTING() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.START);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesContainIntersection();
        andTheValueAtTheIntersectionIs(FIRST_SERIES_START);
        andTheSeriesAreParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.ZERO_ZERO_INTERSECTING);
    }

    @Test
    public void testNEGATIVE_ZERO_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.START);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesContainIntersection();
        andTheValueAtTheIntersectionIs(FIRST_SERIES_START);
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.NEGATIVE_ZERO_INTERSECTING);
    }

    @Test
    public void testPOSITIVE_ZERO_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.START);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesContainIntersection();
        andTheValueAtTheIntersectionIs(FIRST_SERIES_START);
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.POSITIVE_ZERO_INTERSECTING);
    }

    @Test
    public void testZERO_POSITIVE_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.START);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesContainIntersection();
        andTheValueAtTheIntersectionIs(FIRST_SERIES_START);
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.ZERO_POSITIVE_INTERSECTING);
    }

    @Test
    public void testNEGATIVE_POSITIVE_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.START);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesContainIntersection();
        andTheValueAtTheIntersectionIs(FIRST_SERIES_START);
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.NEGATIVE_POSITIVE_INTERSECTING);
    }

    @Test
    public void testPOSITIVE_POSITIVE_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.START);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesContainIntersection();
        andTheValueAtTheIntersectionIs(FIRST_SERIES_START);
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.POSITIVE_POSITIVE_INTERSECTING);
    }

    @Test
    public void testZERO_NEGATIVE_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.START);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesContainIntersection();
        andTheValueAtTheIntersectionIs(FIRST_SERIES_START);
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.ZERO_NEGATIVE_INTERSECTING);
    }

    @Test
    public void testNEGATIVE_NEGATIVE_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.START);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesContainIntersection();
        andTheValueAtTheIntersectionIs(FIRST_SERIES_START);
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.NEGATIVE_NEGATIVE_INTERSECTING);
    }

    @Test
    public void testPOSITIVE_NEGATIVE_INTERSECTING_START() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.START);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesContainIntersection();
        andTheValueAtTheIntersectionIs(FIRST_SERIES_START);
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.POSITIVE_NEGATIVE_INTERSECTING);
    }

    @Test
    public void testNEGATIVE_ZERO_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.END);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesContainIntersection();
        andTheValueAtTheIntersectionIs(FIRST_SERIES_START - 1);
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.NEGATIVE_ZERO_INTERSECTING);
    }

    @Test
    public void testPOSITIVE_ZERO_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.END);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesContainIntersection();
        andTheValueAtTheIntersectionIs(FIRST_SERIES_START + 1);
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.POSITIVE_ZERO_INTERSECTING);
    }

    @Test
    public void testZERO_POSITIVE_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.END);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesContainIntersection();
        andTheValueAtTheIntersectionIs(FIRST_SERIES_START);
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.ZERO_POSITIVE_INTERSECTING);
    }

    @Test
    public void testNEGATIVE_POSITIVE_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.END);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesContainIntersection();
        andTheValueAtTheIntersectionIs(FIRST_SERIES_START - 1);
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.NEGATIVE_POSITIVE_INTERSECTING);
    }

    @Test
    public void testPOSITIVE_POSITIVE_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.END);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesContainIntersection();
        andTheValueAtTheIntersectionIs(FIRST_SERIES_START + 1);
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.POSITIVE_POSITIVE_INTERSECTING);
    }

    @Test
    public void testZERO_NEGATIVE_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.END);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesContainIntersection();
        andTheValueAtTheIntersectionIs(FIRST_SERIES_START);
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.ZERO_NEGATIVE_INTERSECTING);
    }

    @Test
    public void testNEGATIVE_NEGATIVE_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.END);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesContainIntersection();
        andTheValueAtTheIntersectionIs(FIRST_SERIES_START - 1);
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.NEGATIVE_NEGATIVE_INTERSECTING);
    }

    @Test
    public void testPOSITIVE_NEGATIVE_INTERSECTING_END() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.END);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesContainIntersection();
        andTheValueAtTheIntersectionIs(FIRST_SERIES_START + 1);
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.POSITIVE_NEGATIVE_INTERSECTING);
    }

    @Test
    public void testNEGATIVE_ZERO_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.WITHIN);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesContainIntersection();
        andTheValueAtTheIntersectionIs(this.firstSeriesSegment.getStartValue()
                + (this.firstSeriesSegment.getEndValue() - this.firstSeriesSegment.getStartValue()) / 2);
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.NEGATIVE_ZERO_INTERSECTING);
    }

    @Test
    public void testPOSITIVE_ZERO_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.WITHIN);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesContainIntersection();
        andTheValueAtTheIntersectionIs(this.firstSeriesSegment.getStartValue()
                + (this.firstSeriesSegment.getEndValue() - this.firstSeriesSegment.getStartValue()) / 2);
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.POSITIVE_ZERO_INTERSECTING);
    }

    @Test
    public void testZERO_POSITIVE_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.WITHIN);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesContainIntersection();
        andTheValueAtTheIntersectionIs(FIRST_SERIES_START);
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.ZERO_POSITIVE_INTERSECTING);
    }

    @Test
    public void testNEGATIVE_POSITIVE_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.WITHIN);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesContainIntersection();
        andTheValueAtTheIntersectionIs(this.firstSeriesSegment.getStartValue()
                + (this.firstSeriesSegment.getEndValue() - this.firstSeriesSegment.getStartValue()) / 2);
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.NEGATIVE_POSITIVE_INTERSECTING);
    }

    @Test
    public void testPOSITIVE_POSITIVE_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.WITHIN);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesContainIntersection();
        andTheValueAtTheIntersectionIs(this.firstSeriesSegment.getStartValue()
                + (this.firstSeriesSegment.getEndValue() - this.firstSeriesSegment.getStartValue()) / 2);
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.POSITIVE_POSITIVE_INTERSECTING);
    }

    @Test
    public void testZERO_NEGATIVE_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.WITHIN);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesContainIntersection();
        andTheValueAtTheIntersectionIs(FIRST_SERIES_START);
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.ZERO_NEGATIVE_INTERSECTING);
    }

    @Test
    public void testNEGATIVE_NEGATIVE_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.WITHIN);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesContainIntersection();
        andTheValueAtTheIntersectionIs(this.firstSeriesSegment.getStartValue()
                + (this.firstSeriesSegment.getEndValue() - this.firstSeriesSegment.getStartValue()) / 2);
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.NEGATIVE_NEGATIVE_INTERSECTING);
    }

    @Test
    public void testPOSITIVE_NEGATIVE_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.WITHIN);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesContainIntersection();
        andTheValueAtTheIntersectionIs(this.firstSeriesSegment.getStartValue()
                + (this.firstSeriesSegment.getEndValue() - this.firstSeriesSegment.getStartValue()) / 2);
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.POSITIVE_NEGATIVE_INTERSECTING);
    }

    @Test
    public void testAppendThrowsExceptionIfGradientTypeDoesNotMatch()
            throws SegmentCategoryNotFoundException, SegmentAppendException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.NEVER);
        whenTheGraphSegmentIsConstructed();
        expectAnExceptionOfType(SegmentAppendException.class);
        whenASegmentWithSeriesGradientsIsAppended(GradientType.POSITIVE, GradientType.NEGATIVE, Intersection.NEVER);
    }

    @Test
    public void testAppendMatchingSegment() throws SegmentCategoryNotFoundException, SegmentAppendException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.NEVER);
        whenTheGraphSegmentIsConstructed();
        whenASegmentWithSeriesGradientsIsAppended(GradientType.POSITIVE, GradientType.POSITIVE, Intersection.NEVER);
        thenTheSegmentHasLength(2);
        andTheGraphSegmentCategoryIs(GraphSegmentCategory.POSITIVE_POSITIVE);
        // TODO: more to test here (gradient, end values, intersection, etc)
    }

    private void thenTheSegmentHasLength(int length) {
        assertEquals(length, this.underTest.getLength());
    }

    private void whenASegmentWithSeriesGradientsIsAppended(GradientType series1GradientType,
            GradientType series2GradientType, Intersection intersection)
                    throws SegmentCategoryNotFoundException, SegmentAppendException {

        givenFirstSeriesWithGradient(this.appendFirstSeriesSegment, series1GradientType);
        givenSecondSeriesWithGradientThatIntersectsAt(this.appendFirstSeriesSegment, this.appendSecondSeriesSegment,
                series2GradientType, intersection);
        whenTheGraphSegmentIsAppended(series1GradientType, series2GradientType);
    }

    private void expectAnExceptionOfType(Class clazz) {
        this.expectedEx.expect(clazz);
    }

    private void whenTheGraphSegmentIsConstructed() throws SegmentCategoryNotFoundException {
        this.underTest = new GraphSegmentImpl(this.firstSeriesSegment, this.secondSeriesSegment);
    }

    private void whenTheGraphSegmentIsAppended(GradientType series1GradientType, GradientType series2GradientType)
            throws SegmentAppendException, SegmentCategoryNotFoundException {
        when(this.collatedFirstSeriesSegment.getEndTime()).thenReturn("2");
        when(this.collatedFirstSeriesSegment.getEndValue()).thenReturn(DUMMY_END_VALUE);
        when(this.collatedFirstSeriesSegment.getGradientType()).thenReturn(series1GradientType);
        when(this.collatedFirstSeriesSegment.getSegmentLength()).thenReturn(2);
        when(this.collatedFirstSeriesSegment.getStartTime()).thenReturn("0");
        when(this.collatedFirstSeriesSegment.getStartValue()).thenReturn(FIRST_SERIES_START);
        when(this.collatedFirstSeriesSegment.getGradient()).thenReturn((DUMMY_END_VALUE - FIRST_SERIES_START) / 2);

        when(this.collatedSecondSeriesSegment.getEndTime()).thenReturn("2");
        when(this.collatedSecondSeriesSegment.getEndValue()).thenReturn(DUMMY_END_VALUE);
        when(this.collatedSecondSeriesSegment.getGradientType()).thenReturn(series2GradientType);
        when(this.collatedSecondSeriesSegment.getSegmentLength()).thenReturn(2);
        when(this.collatedSecondSeriesSegment.getStartTime()).thenReturn("0");
        when(this.collatedSecondSeriesSegment.getStartValue()).thenReturn(this.secondSeriesStartValue);
        when(this.collatedSecondSeriesSegment.getGradient()).thenReturn(((DUMMY_END_VALUE - FIRST_SERIES_START) / 2));

        when(this.firstSeriesSegment.append(this.appendFirstSeriesSegment)).thenReturn(this.collatedFirstSeriesSegment);
        when(this.secondSeriesSegment.append(this.appendSecondSeriesSegment))
                .thenReturn(this.collatedSecondSeriesSegment);
        this.underTest.append(new GraphSegmentImpl(this.appendFirstSeriesSegment, this.appendSecondSeriesSegment));
    }

    private void givenSecondSeriesWithGradientThatIntersectsAt(SeriesSegment firstSeries, SeriesSegment secondSeries,
            GradientType gradientType, Intersection intersection) {
        when(secondSeries.getGradientType()).thenReturn(gradientType);
        this.secondSeriesStartValue = calculateStartOfSecondSeriesGivenIntersect(firstSeries, gradientType,
                intersection);
        when(secondSeries.getStartValue()).thenReturn(this.secondSeriesStartValue);
        this.secondSeriesEndValue = 0;
        switch (gradientType) {
        case NEGATIVE:
            this.secondSeriesEndValue = this.secondSeriesStartValue - SECOND_SERIES_GRADIENT_FACTOR;
            break;
        case ZERO:
            this.secondSeriesEndValue = this.secondSeriesStartValue;
            break;
        case POSITIVE:
            this.secondSeriesEndValue = this.secondSeriesStartValue + SECOND_SERIES_GRADIENT_FACTOR;
            break;
        default:
            fail("gradient type not specified");
        }
        when(secondSeries.getEndValue()).thenReturn(this.secondSeriesEndValue);

    }

    private double calculateStartOfSecondSeriesGivenIntersect(SeriesSegment firstSeries, GradientType gradientType,
            Intersection intersection) {
        this.secondSeriesStartValue = 0d;
        switch (intersection) {
        case NEVER:
            // Make the start value more than 2 away from both the start and end
            // value of series 1.
            this.secondSeriesStartValue = firstSeries.getStartValue() * 2;
            break;
        case START:
            // Same start value as 1
            this.secondSeriesStartValue = firstSeries.getStartValue();
            break;
        case END:
            // Set the start value so that the end values will be the same
            this.secondSeriesStartValue = firstSeries.getEndValue() - 2 * gradientType.getMultiplier();
            break;
        case WITHIN:
            switch (firstSeries.getGradientType()) {
            case ZERO:
                this.secondSeriesStartValue = firstSeries.getStartValue() - gradientType.getMultiplier();
                break;
            case POSITIVE:
                this.secondSeriesStartValue = firstSeries.getStartValue() + .5 - gradientType.getMultiplier();
                break;
            case NEGATIVE:
                this.secondSeriesStartValue = firstSeries.getStartValue() - .5 - gradientType.getMultiplier();
                break;
            default:
                fail("gradient type not specified");
                break;
            }
            break;
        default:
            fail("intersection not specified");
            break;
        }

        return this.secondSeriesStartValue;
    }

    private void givenFirstSeriesWithGradient(SeriesSegment firstSeries, GradientType gradientType) {
        double startValue = firstSeries.getStartValue();
        this.firstSeriesEndValue = 0d;

        when(firstSeries.getGradientType()).thenReturn(gradientType);
        switch (gradientType) {
        case NEGATIVE:
            this.firstSeriesEndValue = startValue - FIRST_SERIES_GRADIENT_FACTOR;
            break;
        case ZERO:
            this.firstSeriesEndValue = startValue;
            break;
        case POSITIVE:
            this.firstSeriesEndValue = startValue + FIRST_SERIES_GRADIENT_FACTOR;
            break;
        default:
            fail("gradient type not specified");
            break;
        }
        when(firstSeries.getEndValue()).thenReturn(this.firstSeriesEndValue);
    }

    private void thenTheSegmentDoesNotContainIntersection() {
        assertFalse("Series should not intersect", this.underTest.isIntersecting());
        andTheValueAtTheIntersectionIs(null);
    }

    private void thenTheSegmentDoesContainIntersection() {
        assertTrue("Series don't intersect", this.underTest.isIntersecting());
    }

    private void andTheValueAtTheIntersectionIs(Double intersectionValue) {
        if (null == intersectionValue) {
            assertNull(this.underTest.getValueAtIntersection());
        } else {
            assertEquals(intersectionValue, this.underTest.getValueAtIntersection(), 0);
        }
    }

    private void andTheSeriesAreParallel() {
        assertTrue("Series are not parallel", this.underTest.isParallel());
    }

    private void andTheGraphSegmentCategoryIs(GraphSegmentCategory category) {
        assertEquals("incorrect segment category returned", category, this.underTest.getSegmentCategory());
    }

    private void andTheSeriesAreNotParallel() {
        assertFalse("Series are parallel", this.underTest.isParallel());
    }

}
