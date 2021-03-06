package uk.co.lgs.model.segment.series;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import uk.co.lgs.model.gradient.GradientType;
import uk.co.lgs.model.point.Point;
import uk.co.lgs.model.point.PointImpl;
import uk.co.lgs.test.AbstractTest;

public class SeriesSegmentTest extends AbstractTest {

    private SeriesSegment underTest;

    private static final Double HIGH_VALUE = 23.45d;
    private static final Double LOW_VALUE = 12.34d;

    private static final String START_TIME = "2014";

    private static final String END_TIME = "2015";

    private static final String SERIES_LABEL = "Sales of mushrooms";

    private Point firstValue;
    private Point secondValue;

    @Before
    public void setup() {
        this.firstValue = new PointImpl(START_TIME, HIGH_VALUE);
        this.secondValue = new PointImpl(END_TIME, LOW_VALUE);
    }

    @Test
    public void testZeroGradient() {
        givenASegmentWithStartAndEndValues(LOW_VALUE, LOW_VALUE, SERIES_LABEL);
        thenTheGradientShouldEqualZero();
        thenTheSegmentLengthShouldBe(1);
        thenTheStartValueShouldEqual(LOW_VALUE);
        thenTheEndValueShouldEqual(LOW_VALUE);
        thenTheSeriesShouldBeCalled(SERIES_LABEL);
    }

    @Test
    public void testPositiveGradient() {
        givenASegmentWithStartAndEndValues(LOW_VALUE, HIGH_VALUE, SERIES_LABEL);
        thenTheGradientShouldBePositive();
        thenTheSegmentLengthShouldBe(1);
        thenTheStartValueShouldEqual(LOW_VALUE);
        thenTheEndValueShouldEqual(HIGH_VALUE);
        thenTheSeriesShouldBeCalled(SERIES_LABEL);
    }

    @Test
    public void testNegativeGradient() {
        givenASegmentWithStartAndEndValues(HIGH_VALUE, LOW_VALUE, SERIES_LABEL);
        thenTheGradientShouldBeNegative();
        thenTheSegmentLengthShouldBe(1);
        thenTheStartValueShouldEqual(HIGH_VALUE);
        thenTheEndValueShouldEqual(LOW_VALUE);
        thenTheSeriesShouldBeCalled(SERIES_LABEL);
    }

    @Test
    public void testGetSegmentLengthForCombinedSegments() {
        givenASegmentWithStartAndEndValuesAndLength(HIGH_VALUE, LOW_VALUE, SERIES_LABEL, 2);
        thenTheSegmentLengthShouldBe(2);
        thenTheStartValueShouldEqual(HIGH_VALUE);
        thenTheEndValueShouldEqual(LOW_VALUE);
        thenTheSeriesShouldBeCalled(SERIES_LABEL);
    }

    @Test
    public void testThatTheStartAndEndTimesAreReturned() {
        givenASegmentWithStartAndEndValues(HIGH_VALUE, LOW_VALUE, SERIES_LABEL);
        thenTheStartTimeShouldBe(START_TIME);
        thenTheEndTimeShouldBe(END_TIME);
        thenTheStartValueShouldEqual(HIGH_VALUE);
        thenTheEndValueShouldEqual(LOW_VALUE);
        thenTheSeriesShouldBeCalled(SERIES_LABEL);
    }

    private void thenTheSeriesShouldBeCalled(String seriesLabel) {
        assertEquals(seriesLabel, this.underTest.getLabel());

    }

    private void thenTheEndTimeShouldBe(String endTime) {
        assertEquals(endTime, this.underTest.getEndTime());
    }

    private void thenTheStartTimeShouldBe(String startTime) {
        assertEquals(startTime, this.underTest.getStartTime());
    }

    private void thenTheSegmentLengthShouldBe(int i) {
        assertEquals(i, this.underTest.getSegmentLength());
    }

    private void thenTheGradientShouldBePositive() {
        assertEquals(GradientType.POSITIVE, this.underTest.getGradientType());
    }

    private void thenTheGradientShouldBeNegative() {
        assertEquals(GradientType.NEGATIVE, this.underTest.getGradientType());
    }

    private void thenTheGradientShouldEqualZero() {
        assertEquals(GradientType.ZERO, this.underTest.getGradientType());
    }

    private void givenASegmentWithStartAndEndValues(double startValue, double endValue, String seriesLabel) {
        this.firstValue = new PointImpl(START_TIME, startValue);
        this.secondValue = new PointImpl(END_TIME, endValue);
        this.underTest = new SeriesSegmentImpl(this.firstValue, this.secondValue, seriesLabel, "", "");
    }

    private void givenASegmentWithStartAndEndValuesAndLength(double startValue, double endValue, String seriesLabel,
            int i) {
        this.firstValue = new PointImpl(START_TIME, startValue);
        this.secondValue = new PointImpl(END_TIME, endValue);
        this.underTest = new SeriesSegmentImpl(this.firstValue, this.secondValue, seriesLabel, "", "", i);
    }

    private void thenTheStartValueShouldEqual(double expectedValue) {
        assertEquals(expectedValue, this.underTest.getStartValue(), 0);
    }

    private void thenTheEndValueShouldEqual(double expectedValue) {
        assertEquals(expectedValue, this.underTest.getEndValue(), 0);
    }
}
