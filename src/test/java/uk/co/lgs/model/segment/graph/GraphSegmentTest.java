package uk.co.lgs.model.segment.graph;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import uk.co.lgs.model.gradient.GradientType;
import uk.co.lgs.model.segment.exception.SegmentCategoryNotFoundException;
import uk.co.lgs.model.segment.graph.GraphSegmentImpl;
import uk.co.lgs.model.segment.graph.category.GraphSegmentCategory;
import uk.co.lgs.model.segment.series.SeriesSegment;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphSegmentTest {

	private GraphSegmentImpl underTest;
	/**The first series has an initial value of 5, and a gradient of either -1, 0 or 1.
	 * The second series has a gradient of either -2, 0 or 2, and a start value is calculated for it
	 * to intersect with the first series at the required position.
	 */
	private static final Double FIRST_SERIES_START = 5d;
	private static final int FIRST_SERIES_GRADIENT_FACTOR = 1;
	private static final int SECOND_SERIES_GRADIENT_FACTOR = 2;

	@Mock
	private SeriesSegment firstSeriesSegment;
	@Mock
	private SeriesSegment secondSeriesSegment;

	@Before
	public void setup() {
		when(firstSeriesSegment.getStartValue()).thenReturn(FIRST_SERIES_START);
	}

	@Test
	public void testZERO_ZERO() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.ZERO);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.ZERO, Intersection.NEVER);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesNotContainIntersection();
		andTheSeriesAreParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.ZERO_ZERO);
	}

	@Test
	public void testNEGATIVE_ZERO() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.NEGATIVE);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.ZERO, Intersection.NEVER);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesNotContainIntersection();
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.NEGATIVE_ZERO);
	}

	@Test
	public void testPOSITIVE_ZERO() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.POSITIVE);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.ZERO, Intersection.NEVER);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesNotContainIntersection();
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.POSITIVE_ZERO);
	}

	@Test
	public void testZERO_POSITIVE() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.ZERO);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.POSITIVE, Intersection.NEVER);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesNotContainIntersection();
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.ZERO_POSITIVE);
	}

	@Test
	public void testNEGATIVE_POSITIVE() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.NEGATIVE);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.POSITIVE, Intersection.NEVER);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesNotContainIntersection();
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.NEGATIVE_POSITIVE);
	}

	@Test
	public void testPOSITIVE_POSITIVE() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.POSITIVE);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.POSITIVE, Intersection.NEVER);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesNotContainIntersection();
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.POSITIVE_POSITIVE);
	}

	@Test
	public void testZERO_NEGATIVE() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.ZERO);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.NEGATIVE, Intersection.NEVER);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesNotContainIntersection();
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.ZERO_NEGATIVE);
	}

	@Test
	public void testNEGATIVE_NEGATIVE() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.NEGATIVE);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.NEGATIVE, Intersection.NEVER);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesNotContainIntersection();
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.NEGATIVE_NEGATIVE);
	}

	@Test
	public void testPOSITIVE_NEGATIVE() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.POSITIVE);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.NEGATIVE, Intersection.NEVER);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesNotContainIntersection();
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.POSITIVE_NEGATIVE);
	}
	
	@Test
	public void testZERO_ZERO_INTERSECTING() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.ZERO);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.ZERO, Intersection.START);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesContainIntersection();
		andTheValueAtTheIntersectionIs(FIRST_SERIES_START);
		andTheSeriesAreParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.ZERO_ZERO_INTERSECTING);
	}

	@Test
	public void testNEGATIVE_ZERO_INTERSECTING_START() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.NEGATIVE);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.ZERO, Intersection.START);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesContainIntersection();
		andTheValueAtTheIntersectionIs(FIRST_SERIES_START);
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.NEGATIVE_ZERO_INTERSECTING);
	}

	@Test
	public void testPOSITIVE_ZERO_INTERSECTING_START() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.POSITIVE);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.ZERO, Intersection.START);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesContainIntersection();
		andTheValueAtTheIntersectionIs(FIRST_SERIES_START);
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.POSITIVE_ZERO_INTERSECTING);
	}

	@Test
	public void testZERO_POSITIVE_INTERSECTING_START() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.ZERO);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.POSITIVE, Intersection.START);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesContainIntersection();
		andTheValueAtTheIntersectionIs(FIRST_SERIES_START);
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.ZERO_POSITIVE_INTERSECTING);
	}

	@Test
	public void testNEGATIVE_POSITIVE_INTERSECTING_START() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.NEGATIVE);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.POSITIVE, Intersection.START);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesContainIntersection();
		andTheValueAtTheIntersectionIs(FIRST_SERIES_START);
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.NEGATIVE_POSITIVE_INTERSECTING);
	}

	@Test
	public void testPOSITIVE_POSITIVE_INTERSECTING_START() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.POSITIVE);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.POSITIVE, Intersection.START);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesContainIntersection();
		andTheValueAtTheIntersectionIs(FIRST_SERIES_START);
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.POSITIVE_POSITIVE_INTERSECTING);
	}

	@Test
	public void testZERO_NEGATIVE_INTERSECTING_START() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.ZERO);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.NEGATIVE, Intersection.START);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesContainIntersection();
		andTheValueAtTheIntersectionIs(FIRST_SERIES_START);
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.ZERO_NEGATIVE_INTERSECTING);
	}

	@Test
	public void testNEGATIVE_NEGATIVE_INTERSECTING_START() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.NEGATIVE);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.NEGATIVE, Intersection.START);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesContainIntersection();
		andTheValueAtTheIntersectionIs(FIRST_SERIES_START);
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.NEGATIVE_NEGATIVE_INTERSECTING);
	}

	@Test
	public void testPOSITIVE_NEGATIVE_INTERSECTING_START() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.POSITIVE);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.NEGATIVE, Intersection.START);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesContainIntersection();
		andTheValueAtTheIntersectionIs(FIRST_SERIES_START);
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.POSITIVE_NEGATIVE_INTERSECTING);
	}
	
	
	@Test
	public void testNEGATIVE_ZERO_INTERSECTING_END() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.NEGATIVE);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.ZERO, Intersection.END);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesContainIntersection();
		andTheValueAtTheIntersectionIs(FIRST_SERIES_START-1);
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.NEGATIVE_ZERO_INTERSECTING);
	}

	@Test
	public void testPOSITIVE_ZERO_INTERSECTING_END() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.POSITIVE);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.ZERO, Intersection.END);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesContainIntersection();
		andTheValueAtTheIntersectionIs(FIRST_SERIES_START+1);
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.POSITIVE_ZERO_INTERSECTING);
	}

	@Test
	public void testZERO_POSITIVE_INTERSECTING_END() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.ZERO);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.POSITIVE, Intersection.END);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesContainIntersection();
		andTheValueAtTheIntersectionIs(FIRST_SERIES_START);
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.ZERO_POSITIVE_INTERSECTING);
	}

	@Test
	public void testNEGATIVE_POSITIVE_INTERSECTING_END() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.NEGATIVE);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.POSITIVE, Intersection.END);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesContainIntersection();
		andTheValueAtTheIntersectionIs(FIRST_SERIES_START-1);
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.NEGATIVE_POSITIVE_INTERSECTING);
	}

	@Test
	public void testPOSITIVE_POSITIVE_INTERSECTING_END() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.POSITIVE);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.POSITIVE, Intersection.END);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesContainIntersection();
		andTheValueAtTheIntersectionIs(FIRST_SERIES_START+1);
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.POSITIVE_POSITIVE_INTERSECTING);
	}

	@Test
	public void testZERO_NEGATIVE_INTERSECTING_END() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.ZERO);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.NEGATIVE, Intersection.END);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesContainIntersection();
		andTheValueAtTheIntersectionIs(FIRST_SERIES_START);
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.ZERO_NEGATIVE_INTERSECTING);
	}

	@Test
	public void testNEGATIVE_NEGATIVE_INTERSECTING_END() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.NEGATIVE);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.NEGATIVE, Intersection.END);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesContainIntersection();
		andTheValueAtTheIntersectionIs(FIRST_SERIES_START-1);
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.NEGATIVE_NEGATIVE_INTERSECTING);
	}

	@Test
	public void testPOSITIVE_NEGATIVE_INTERSECTING_END() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.POSITIVE);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.NEGATIVE, Intersection.END);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesContainIntersection();
		andTheValueAtTheIntersectionIs(FIRST_SERIES_START+1);
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.POSITIVE_NEGATIVE_INTERSECTING);
	}
	
	@Test
	public void testNEGATIVE_ZERO_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.NEGATIVE);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.ZERO, Intersection.WITHIN);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesContainIntersection();
		andTheValueAtTheIntersectionIs(firstSeriesSegment.getStartValue()+ (firstSeriesSegment.getEndValue()-firstSeriesSegment.getStartValue())/2);
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.NEGATIVE_ZERO_INTERSECTING);
	}

	@Test
	public void testPOSITIVE_ZERO_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.POSITIVE);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.ZERO, Intersection.WITHIN);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesContainIntersection();
		andTheValueAtTheIntersectionIs(firstSeriesSegment.getStartValue()+ (firstSeriesSegment.getEndValue()-firstSeriesSegment.getStartValue())/2);
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.POSITIVE_ZERO_INTERSECTING);
	}

	@Test
	public void testZERO_POSITIVE_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.ZERO);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.POSITIVE, Intersection.WITHIN);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesContainIntersection();
		andTheValueAtTheIntersectionIs(FIRST_SERIES_START);
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.ZERO_POSITIVE_INTERSECTING);
	}

	@Test
	public void testNEGATIVE_POSITIVE_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.NEGATIVE);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.POSITIVE, Intersection.WITHIN);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesContainIntersection();
		andTheValueAtTheIntersectionIs(firstSeriesSegment.getStartValue()+ (firstSeriesSegment.getEndValue()-firstSeriesSegment.getStartValue())/2);
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.NEGATIVE_POSITIVE_INTERSECTING);
	}

	@Test
	public void testPOSITIVE_POSITIVE_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.POSITIVE);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.POSITIVE, Intersection.WITHIN);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesContainIntersection();
		andTheValueAtTheIntersectionIs(firstSeriesSegment.getStartValue()+ (firstSeriesSegment.getEndValue()-firstSeriesSegment.getStartValue())/2);
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.POSITIVE_POSITIVE_INTERSECTING);
	}

	@Test
	public void testZERO_NEGATIVE_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.ZERO);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.NEGATIVE, Intersection.WITHIN);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesContainIntersection();
		andTheValueAtTheIntersectionIs(FIRST_SERIES_START);
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.ZERO_NEGATIVE_INTERSECTING);
	}

	@Test
	public void testNEGATIVE_NEGATIVE_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.NEGATIVE);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.NEGATIVE, Intersection.WITHIN);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesContainIntersection();
		andTheValueAtTheIntersectionIs(firstSeriesSegment.getStartValue()+ (firstSeriesSegment.getEndValue()-firstSeriesSegment.getStartValue())/2);
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.NEGATIVE_NEGATIVE_INTERSECTING);
	}

	@Test
	public void testPOSITIVE_NEGATIVE_INTERSECTING_WITHIN() throws SegmentCategoryNotFoundException {
		givenFirstSeriesWithGradient(GradientType.POSITIVE);
		givenSecondSeriesWithGradientThatIntersectsAt(GradientType.NEGATIVE, Intersection.WITHIN);
		whenTheGraphSegmentIsConstructed();
		thenTheSegmentDoesContainIntersection();
		andTheValueAtTheIntersectionIs(firstSeriesSegment.getStartValue()+ (firstSeriesSegment.getEndValue()-firstSeriesSegment.getStartValue())/2);
		andTheSeriesAreNotParallel();
		andTheGraphSegmentCategoryIs(GraphSegmentCategory.POSITIVE_NEGATIVE_INTERSECTING);
	}

	private void whenTheGraphSegmentIsConstructed() throws SegmentCategoryNotFoundException {
		underTest = new GraphSegmentImpl(firstSeriesSegment, secondSeriesSegment);
	}

	private void givenSecondSeriesWithGradientThatIntersectsAt(GradientType gradientType, Intersection intersection) {
		when(secondSeriesSegment.getGradientType()).thenReturn(gradientType);

		double startValue = calculateStartOfSecondSeriesGivenIntersect(gradientType, intersection);
		when(secondSeriesSegment.getStartValue()).thenReturn(startValue);
		switch (gradientType) {
		case NEGATIVE:
			when(secondSeriesSegment.getEndValue()).thenReturn(startValue - SECOND_SERIES_GRADIENT_FACTOR);
			break;
		case ZERO:
			when(secondSeriesSegment.getEndValue()).thenReturn(startValue);
			break;
		case POSITIVE:
			when(secondSeriesSegment.getEndValue()).thenReturn(startValue + SECOND_SERIES_GRADIENT_FACTOR);
			break;
		default:
			fail ("gradient type not specified");
		}
	}

	private double calculateStartOfSecondSeriesGivenIntersect(GradientType gradientType, Intersection intersection) {
		double secondSeriesStartValue = 0;
		switch (intersection) {
		case NEVER:
			// Make the start value more than 2 away from both the start and end
			// value of series 1.
			secondSeriesStartValue = firstSeriesSegment.getStartValue() * 2;
			break;
		case START:
			// Same start value as 1
			secondSeriesStartValue = firstSeriesSegment.getStartValue();
			break;
		case END:
			// Set the start value so that the end values will be the same
			secondSeriesStartValue = firstSeriesSegment.getEndValue() - 2 * gradientType.getMultiplier();
			break;
		case WITHIN:
			switch (firstSeriesSegment.getGradientType()) {
			case ZERO:
				secondSeriesStartValue = firstSeriesSegment.getStartValue() - gradientType.getMultiplier();
				break;
			case POSITIVE:
				secondSeriesStartValue = firstSeriesSegment.getStartValue() + .5 - gradientType.getMultiplier();
				break;
			case NEGATIVE:
				secondSeriesStartValue = firstSeriesSegment.getStartValue() - .5 - gradientType.getMultiplier();
				break;
			default:
				fail ("gradient type not specified");
				break;
			}
			break;
		default:
			fail("intersection not specified");
			break;
		}

		return secondSeriesStartValue;
	}

	private void givenFirstSeriesWithGradient(GradientType gradientType) {
		double startValue = firstSeriesSegment.getStartValue();

		when(firstSeriesSegment.getGradientType()).thenReturn(gradientType);
		switch (gradientType) {
		case NEGATIVE:
			when(firstSeriesSegment.getEndValue()).thenReturn(startValue - FIRST_SERIES_GRADIENT_FACTOR);
			break;
		case ZERO:
			when(firstSeriesSegment.getEndValue()).thenReturn(startValue);
			break;
		case POSITIVE:
			when(firstSeriesSegment.getEndValue()).thenReturn(startValue + FIRST_SERIES_GRADIENT_FACTOR);
			break;
		default:
			fail ("gradient type not specified");
			break;
		}
	}

	private void thenTheSegmentDoesNotContainIntersection() {
		assertFalse("Series should not intersect", underTest.isIntersecting());
		andTheValueAtTheIntersectionIs(null);
	}

	private void thenTheSegmentDoesContainIntersection() {
		assertTrue("Series don't intersect", underTest.isIntersecting());
	}
	
	private void andTheValueAtTheIntersectionIs(Double intersectionValue){
		assertEquals(intersectionValue, underTest.getPointOfIntersection());
	}
	
	private void andTheSeriesAreParallel(){
		assertTrue("Series are not parallel", underTest.isParallel());
	}
	

	private void andTheGraphSegmentCategoryIs(GraphSegmentCategory category) {
		assertEquals("incorrect segment category returned", category, underTest.getSegmentCategory());
	}
	
	private void andTheSeriesAreNotParallel(){
		assertFalse("Series are parallel", underTest.isParallel());
	}
	
}
