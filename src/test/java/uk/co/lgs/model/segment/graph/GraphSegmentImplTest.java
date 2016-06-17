package uk.co.lgs.model.segment.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Test;

import uk.co.lgs.model.gradient.GradientType;
import uk.co.lgs.model.segment.exception.SegmentAppendException;
import uk.co.lgs.model.segment.exception.SegmentCategoryNotFoundException;
import uk.co.lgs.model.segment.graph.category.GraphSegmentGradient;
import uk.co.lgs.segment.graph.AbstractGraphSegmentTest;

public class GraphSegmentImplTest extends AbstractGraphSegmentTest {

    private GraphSegmentImpl underTest;

    @Test
    public void testZERO_ZERO() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.NEVER);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesNotContainIntersection();
        andTheSeriesAreParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.ZERO_ZERO);
    }

    @Test
    public void testNEGATIVE_ZERO() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.NEVER);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesNotContainIntersection();
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.NEGATIVE_ZERO);
    }

    @Test
    public void testPOSITIVE_ZERO() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.ZERO, Intersection.NEVER);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesNotContainIntersection();
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.POSITIVE_ZERO);
    }

    @Test
    public void testZERO_POSITIVE() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.NEVER);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesNotContainIntersection();
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.ZERO_POSITIVE);
    }

    @Test
    public void testNEGATIVE_POSITIVE() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.NEVER);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesNotContainIntersection();
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.NEGATIVE_POSITIVE);
    }

    @Test
    public void testPOSITIVE_POSITIVE() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.POSITIVE, Intersection.NEVER);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesNotContainIntersection();
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.POSITIVE_POSITIVE);
    }

    @Test
    public void testZERO_NEGATIVE() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.ZERO);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.NEVER);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesNotContainIntersection();
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.ZERO_NEGATIVE);
    }

    @Test
    public void testNEGATIVE_NEGATIVE() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.NEGATIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.NEVER);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesNotContainIntersection();
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.NEGATIVE_NEGATIVE);
    }

    @Test
    public void testPOSITIVE_NEGATIVE() throws SegmentCategoryNotFoundException {
        givenFirstSeriesWithGradient(this.firstSeriesSegment, GradientType.POSITIVE);
        givenSecondSeriesWithGradientThatIntersectsAt(this.firstSeriesSegment, this.secondSeriesSegment,
                GradientType.NEGATIVE, Intersection.NEVER);
        whenTheGraphSegmentIsConstructed();
        thenTheSegmentDoesNotContainIntersection();
        andTheSeriesAreNotParallel();
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.POSITIVE_NEGATIVE);
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
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.ZERO_ZERO_INTERSECTING);
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
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.NEGATIVE_ZERO_INTERSECTING);
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
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.POSITIVE_ZERO_INTERSECTING);
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
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.ZERO_POSITIVE_INTERSECTING);
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
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.NEGATIVE_POSITIVE_INTERSECTING);
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
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.POSITIVE_POSITIVE_INTERSECTING);
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
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.ZERO_NEGATIVE_INTERSECTING);
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
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.NEGATIVE_NEGATIVE_INTERSECTING);
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
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.POSITIVE_NEGATIVE_INTERSECTING);
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
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.NEGATIVE_ZERO_INTERSECTING);
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
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.POSITIVE_ZERO_INTERSECTING);
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
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.ZERO_POSITIVE_INTERSECTING);
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
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.NEGATIVE_POSITIVE_INTERSECTING);
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
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.POSITIVE_POSITIVE_INTERSECTING);
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
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.ZERO_NEGATIVE_INTERSECTING);
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
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.NEGATIVE_NEGATIVE_INTERSECTING);
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
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.POSITIVE_NEGATIVE_INTERSECTING);
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
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.NEGATIVE_ZERO_INTERSECTING);
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
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.POSITIVE_ZERO_INTERSECTING);
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
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.ZERO_POSITIVE_INTERSECTING);
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
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.NEGATIVE_POSITIVE_INTERSECTING);
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
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.POSITIVE_POSITIVE_INTERSECTING);
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
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.ZERO_NEGATIVE_INTERSECTING);
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
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.NEGATIVE_NEGATIVE_INTERSECTING);
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
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.POSITIVE_NEGATIVE_INTERSECTING);
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
        andTheGraphSegmentCategoryIs(GraphSegmentGradient.POSITIVE_POSITIVE);
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

    private void expectAnExceptionOfType(Class<SegmentAppendException> clazz) {
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
        when(this.firstSeriesSegment.getSegmentLength()).thenReturn(2);
        this.underTest.append(new GraphSegmentImpl(this.appendFirstSeriesSegment, this.appendSecondSeriesSegment));
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

    private void andTheGraphSegmentCategoryIs(GraphSegmentGradient category) {
        assertEquals("incorrect segment category returned", category, this.underTest.getGraphSegmentGradientCategory());
    }

    private void andTheSeriesAreNotParallel() {
        assertFalse("Series are parallel", this.underTest.isParallel());
    }

}
