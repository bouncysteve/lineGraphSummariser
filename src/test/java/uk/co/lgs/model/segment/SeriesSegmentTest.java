package uk.co.lgs.model.segment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import uk.co.lgs.model.GradientType;

public class SeriesSegmentTest {

	private SeriesSegment underTest;
	
	private static final Double HIGH_VALUE= 23.45d;
	private static final Double LOW_VALUE= 12.34d;
	
	private static final String START_TIME = "2014";

	private static final String END_TIME = "2015";
	
	private Map <String, Double> firstValue;
	private Map <String, Double> secondValue;
	
	@Before
	public void setup(){
		firstValue = new HashMap<String, Double>();
		secondValue = new HashMap<String, Double>();
	}
	
	@Test
	public void testZeroGradient(){
		givenASegmentWithStartAndEndValues(LOW_VALUE, LOW_VALUE);
		thenTheGradientShouldEqualZero();
		thenTheSegmentLengthShouldBe(1);
	}
	
	@Test
	public void testPositiveGradient(){
		givenASegmentWithStartAndEndValues(LOW_VALUE, HIGH_VALUE);
		thenTheGradientShouldBePositive();
		thenTheSegmentLengthShouldBe(1);
	}
	
	@Test
	public void testNegativeGradient(){
		givenASegmentWithStartAndEndValues(HIGH_VALUE, LOW_VALUE);
		thenTheGradientShouldBeNegative();
		thenTheSegmentLengthShouldBe(1);
	}
	
	@Test
	public void testGetSegmentLength(){
		givenASegmentWithStartAndEndValues(HIGH_VALUE, LOW_VALUE);
		thenTheSegmentLengthShouldBe(1);
	}
	
	@Test
	public void testGetSegmentLengthForCombinedSegments(){
		givenASegmentWithStartAndEndValuesAndLength(HIGH_VALUE, LOW_VALUE, 2);
		thenTheSegmentLengthShouldBe(2);
	}

	@Test
	public void testThatTheStartAndEndTimesAreReturned(){
		givenASegmentWithStartAndEndValues(HIGH_VALUE, LOW_VALUE);
		thenTheStartTimeShouldBe(START_TIME);
		thenTheEndTimeShouldBe(END_TIME);
	}

	private void thenTheEndTimeShouldBe(String endTime) {
		assertEquals(endTime, underTest.getEndTime());
	}

	private void thenTheStartTimeShouldBe(String startTime) {
		assertEquals(startTime, underTest.getStartTime());
	}
	

	private void thenTheSegmentLengthShouldBe(int i) {
		assertEquals(i, underTest.getSegmentLength());
	}

	private void thenTheGradientShouldBePositive() {
		assertEquals(GradientType.POSITIVE, underTest.getGradientType());
	}
	
	private void thenTheGradientShouldBeNegative() {
		assertEquals(GradientType.NEGATIVE, underTest.getGradientType());
	}

	private void thenTheGradientShouldEqualZero() {
		assertEquals(GradientType.ZERO, underTest.getGradientType());
	}

	private void givenASegmentWithStartAndEndValues(double startValue, double endValue) {
		firstValue.put(START_TIME, startValue);
		secondValue.put(END_TIME, endValue);
		underTest = new SeriesSegmentImpl(firstValue, secondValue);
	}
	
	private void givenASegmentWithStartAndEndValuesAndLength(double startValue, double endValue, int i) {
		firstValue.put(START_TIME, startValue);
		secondValue.put(END_TIME, endValue);
		underTest = new SeriesSegmentImpl(firstValue, secondValue, i);
	}
}
