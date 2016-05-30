package uk.co.lgs.text.service.segment.graph;

import org.junit.Ignore;
import org.junit.Test;

import uk.co.lgs.model.gradient.GradientType;
import uk.co.lgs.model.segment.exception.SegmentCategoryNotFoundException;
import uk.co.lgs.model.segment.graph.Intersection;
import uk.co.lgs.model.segment.graph.category.GraphSegmentCategory;
import uk.co.lgs.segment.graph.AbstractGraphSegmentTest;

@Ignore
public class GraphSegmentSummaryServiceImplTest extends AbstractGraphSegmentTest {

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
}
