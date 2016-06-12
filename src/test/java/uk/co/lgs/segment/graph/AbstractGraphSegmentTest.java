package uk.co.lgs.segment.graph;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.mockito.Mock;

import uk.co.lgs.model.gradient.GradientType;
import uk.co.lgs.model.segment.graph.GraphSegment;
import uk.co.lgs.model.segment.graph.Intersection;
import uk.co.lgs.model.segment.graph.category.GraphSegmentCategory;
import uk.co.lgs.model.segment.series.SeriesSegment;
import uk.co.lgs.test.AbstractTest;

public abstract class AbstractGraphSegmentTest extends AbstractTest {

    /**
     * The first series has an initial value of 5, and a gradient of either -1,
     * 0 or 1. The second series has a gradient of either -2, 0 or 2, and a
     * start value is calculated for it to intersect with the first series at
     * the required position.
     */
    protected static final int FIRST_SERIES_GRADIENT_FACTOR = 1;
    protected static final int SECOND_SERIES_GRADIENT_FACTOR = 2;
    protected static final Double FIRST_SERIES_START = 5d;
    protected static final Double DUMMY_END_VALUE = 12d;

    protected double firstSeriesEndValue;

    @Mock
    protected SeriesSegment firstSeriesSegment;

    @Mock
    protected SeriesSegment secondSeriesSegment;

    @Mock
    protected SeriesSegment appendFirstSeriesSegment;
    @Mock
    protected SeriesSegment appendSecondSeriesSegment;

    @Mock
    protected SeriesSegment collatedFirstSeriesSegment;

    @Mock
    protected SeriesSegment collatedSecondSeriesSegment;

    @Mock
    protected GraphSegment graphSegment;

    protected double secondSeriesStartValue;
    protected double secondSeriesEndValue;

    @Before
    public void setup() {
        when(this.firstSeriesSegment.getStartValue()).thenReturn(FIRST_SERIES_START);
    }

    protected void givenFirstSeriesWithGradient(SeriesSegment firstSeries, GradientType gradientType) {
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

    protected void givenSecondSeriesWithGradientThatIntersectsAt(SeriesSegment firstSeries, SeriesSegment secondSeries,
            GradientType gradientType, Intersection intersection) {
        GraphSegmentCategory graphSegmentCategory = null;
        when(secondSeries.getGradientType()).thenReturn(gradientType);
        this.secondSeriesStartValue = calculateStartOfSecondSeriesGivenIntersect(firstSeries, gradientType,
                intersection);
        when(secondSeries.getStartValue()).thenReturn(this.secondSeriesStartValue);
        this.secondSeriesEndValue = 0;
        switch (gradientType) {
        case NEGATIVE:
            this.secondSeriesEndValue = this.secondSeriesStartValue - SECOND_SERIES_GRADIENT_FACTOR;
            switch (firstSeries.getGradientType()) {
            case NEGATIVE:
                graphSegmentCategory = GraphSegmentCategory.NEGATIVE_NEGATIVE_INTERSECTING;
                break;
            case ZERO:
                graphSegmentCategory = GraphSegmentCategory.NEGATIVE_ZERO_INTERSECTING;
                break;
            case POSITIVE:
                graphSegmentCategory = GraphSegmentCategory.NEGATIVE_POSITIVE_INTERSECTING;
                break;
            }
            break;
        case ZERO:
            this.secondSeriesEndValue = this.secondSeriesStartValue;
            switch (firstSeries.getGradientType()) {
            case NEGATIVE:
                graphSegmentCategory = GraphSegmentCategory.ZERO_NEGATIVE_INTERSECTING;
                break;
            case ZERO:
                graphSegmentCategory = GraphSegmentCategory.ZERO_ZERO_INTERSECTING;
                break;
            case POSITIVE:
                graphSegmentCategory = GraphSegmentCategory.ZERO_POSITIVE_INTERSECTING;
                break;
            }
            break;
        case POSITIVE:
            this.secondSeriesEndValue = this.secondSeriesStartValue + SECOND_SERIES_GRADIENT_FACTOR;
            switch (firstSeries.getGradientType()) {
            case NEGATIVE:
                graphSegmentCategory = GraphSegmentCategory.POSITIVE_NEGATIVE_INTERSECTING;
                break;
            case ZERO:
                graphSegmentCategory = GraphSegmentCategory.POSITIVE_ZERO_INTERSECTING;
                break;
            case POSITIVE:
                graphSegmentCategory = GraphSegmentCategory.POSITIVE_POSITIVE_INTERSECTING;
                break;
            }
            break;
        default:
            fail("gradient type not specified");
        }
        when(secondSeries.getEndValue()).thenReturn(this.secondSeriesEndValue);
        when(this.graphSegment.getSegmentCategory()).thenReturn(graphSegmentCategory);
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
}
