package uk.co.lgs.model.segment.graph;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.lgs.model.gradient.GradientType;
import uk.co.lgs.model.segment.exception.SegmentAppendException;
import uk.co.lgs.model.segment.exception.SegmentCategoryNotFoundException;
import uk.co.lgs.model.segment.graph.category.GapTrend;
import uk.co.lgs.model.segment.graph.category.GraphSegmentGradient;
import uk.co.lgs.model.segment.series.SeriesSegment;

/**
 * I represent a segment of the graph between two points on the x axis.
 *
 * @author bouncysteve
 *
 */
public class GraphSegmentImpl implements GraphSegment {

    private static final DecimalFormat DF = new DecimalFormat("#.00");

    private static final String APPEND_INVALID_GRADIENT_MESSAGE = "Cannot append segment, the gradient types do not match";

    private static final Logger LOG = LoggerFactory.getLogger(GraphSegmentImpl.class);

    private GraphSegmentGradient graphSegmentGradient;

    private final List<SeriesSegment> seriesSegments;

    private boolean intersecting;

    private double segmentDistanceToIntersection;

    private boolean parallel;

    private GapTrend gapTrend;

    private final SeriesSegment firstSeriesSegment;

    private final SeriesSegment secondSeriesSegment;

    private boolean endValuesAreGlobalMaximumGap;

    private boolean endValuesAreGlobalMinimumGap;

    /**
     * I create an object representing two series between two points in time.
     *
     * @param firstSeriesSegment
     * @param secondSeriesSegment
     * @throws SegmentCategoryNotFoundException
     */
    public GraphSegmentImpl(final SeriesSegment firstSeriesSegment, final SeriesSegment secondSeriesSegment)
            throws SegmentCategoryNotFoundException {
        this.firstSeriesSegment = firstSeriesSegment;
        this.secondSeriesSegment = secondSeriesSegment;
        this.seriesSegments = new ArrayList<>();
        this.seriesSegments.add(firstSeriesSegment);
        this.seriesSegments.add(secondSeriesSegment);
        determineIntersectionDetails();
        determineSegmentCategory();
        determineGraphSegmentGap();
    }

    @Override
    public boolean isIntersecting() {
        return this.intersecting;
    }

    @Override
    public boolean isParallel() {
        return this.parallel;
    }

    @Override
    public GraphSegmentGradient getGraphSegmentGradientCategory() {
        return this.graphSegmentGradient;
    }

    @Override
    public String getStartTime() {
        return this.seriesSegments.get(0).getStartTime();
    }

    @Override
    public String getEndTime() {
        return this.seriesSegments.get(0).getEndTime();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getStartTime()).append(" - ");
        sb.append(this.getEndTime()).append("\t");
        sb.append("(").append(this.getLength()).append(")\t");
        sb.append(this.getGraphSegmentGradientCategory()).append("\t");
        if (!this.getGraphSegmentGradientCategory().isIntersecting()) {
            sb.append("\t");
        }

        sb.append(this.getGapTrend() + "\t");

        String initialHigherSeries = "==";
        if (this.firstSeriesSegment.equals(this.getHigherSeriesAtStart())) {
            initialHigherSeries = "S1";
        } else if (this.secondSeriesSegment.equals(this.getHigherSeriesAtStart())) {
            initialHigherSeries = "S2";
        }
        sb.append(initialHigherSeries + "\t\t");

        String endHigherSeries = "==";
        if (this.firstSeriesSegment.equals(this.getHigherSeriesAtEnd())) {
            endHigherSeries = "S1";
        } else if (this.secondSeriesSegment.equals(this.getHigherSeriesAtEnd())) {
            endHigherSeries = "S2";
        }
        sb.append(endHigherSeries + "\t\t");

        sb.append("(" + DF.format(this.seriesSegments.get(0).getGradient()) + ", ");
        sb.append(DF.format(this.seriesSegments.get(1).getGradient()) + ")\t");

        if (this.isParallel()) {
            sb.append("Segments are parallel").append("\t");
        }
        return sb.toString();
    }

    /**
     * This will need more work to be able to tell if there are intersections
     * between n series (n>2)
     *
     */
    private void determineIntersectionDetails() {
        final SeriesSegment segment1 = this.seriesSegments.get(0);
        final SeriesSegment segment2 = this.seriesSegments.get(1);
        final double firstSeriesStartValue = segment1.getStartValue();
        final double firstSeriesEndValue = segment1.getEndValue();
        final double secondSeriesStartValue = segment2.getStartValue();
        final double secondSeriesEndValue = segment2.getEndValue();

        // equation of series: y = mx + c
        // y = (endValue - startValue)x + startValue
        // -y = (startValue - endValue)x - startValue
        // (startValue - endValue)x + y = startValue
        // http://commons.apache.org/proper/commons-math/userguide/linear.html
        // {startValue - endValue, 1} = {startValue}
        final RealMatrix coefficients = new Array2DRowRealMatrix(
                new double[][] { { firstSeriesStartValue - firstSeriesEndValue, 1 },
                        { secondSeriesStartValue - secondSeriesEndValue, 1 } },
                false);
        final DecompositionSolver solver = new LUDecomposition(coefficients).getSolver();
        final RealVector constants = new ArrayRealVector(new double[] { firstSeriesStartValue, secondSeriesStartValue },
                false);
        try {
            final RealVector solution = solver.solve(constants);
            this.segmentDistanceToIntersection = solution.getEntry(0);
            /*
             * There may be a solution to this simultaneous equation, but it
             * only makes sense if it is within this segment.
             */
            if (this.segmentDistanceToIntersection >= 0 && this.segmentDistanceToIntersection <= 1) {
                this.intersecting = null != Double.valueOf(solution.getEntry(1));
            }
        } catch (final SingularMatrixException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Lines are parallel, they either don't intersect or they are the identical", e);
            }
            this.parallel = true;
            if (firstSeriesStartValue == secondSeriesStartValue) {
                this.intersecting = true;
            }
        }
    }

    private void determineSegmentCategory() {
        final GradientType firstSeriesGradient = this.seriesSegments.get(0).getGradientType();
        final GradientType secondSeriesGradient = this.seriesSegments.get(1).getGradientType();
        for (final GraphSegmentGradient category : GraphSegmentGradient.values()) {
            if (category.getFirstSeriesGradient().equals(firstSeriesGradient)
                    && category.getSecondSeriesGradient().equals(secondSeriesGradient)
                    && category.isIntersecting() == this.intersecting) {
                this.graphSegmentGradient = category;
                break;
            }
        }
    }

    private void determineGraphSegmentGap() {
        final SeriesSegment segment1 = this.seriesSegments.get(0);
        final SeriesSegment segment2 = this.seriesSegments.get(1);
        final double firstSeriesStartValue = segment1.getStartValue();
        final double firstSeriesEndValue = segment1.getEndValue();
        final double secondSeriesStartValue = segment2.getStartValue();
        final double secondSeriesEndValue = segment2.getEndValue();

        final double differenceAtStart = firstSeriesStartValue - secondSeriesStartValue;
        final double differenceAtEnd = firstSeriesEndValue - secondSeriesEndValue;

        if (differenceAtStart > differenceAtEnd) {
            this.gapTrend = GapTrend.CONVERGING;
        } else if (differenceAtStart < differenceAtEnd) {
            this.gapTrend = GapTrend.DIVERGING;
        } else {
            this.gapTrend = GapTrend.PARALLEL;
        }
    }

    @Override
    public GraphSegment append(final GraphSegment newSegment) throws SegmentAppendException {
        // check that the type is the same, if not, throw an exception.
        // TODO: handle near same type (same but with/without intersection)
        for (final SeriesSegment seriesSegment : this.seriesSegments) {
            final SeriesSegment segmentToAppend = newSegment
                    .getSeriesSegment(this.seriesSegments.indexOf(seriesSegment));
            if (seriesSegment.getGradientType().equals(segmentToAppend.getGradientType())) {
                seriesSegment.append(segmentToAppend);
            } else {
                throw new SegmentAppendException(APPEND_INVALID_GRADIENT_MESSAGE);
            }
        }
        determineIntersectionDetails();
        determineSegmentCategory();
        return this;
    }

    @Override
    public int getLength() {
        return this.seriesSegments.get(0).getSegmentLength();
    }

    @Override
    public List<SeriesSegment> getSeriesSegments() {
        return this.seriesSegments;
    }

    @Override
    public SeriesSegment getSeriesSegment(final int index) {
        return this.seriesSegments.get(index);
    }

    @Override
    public GapTrend getGapTrend() {
        return this.gapTrend;
    }

    /**
     * Provides a header to explain the output of toString().
     *
     * @return
     */
    public static String getHeader() {
        final StringBuilder sb = new StringBuilder();
        sb.append("PERIOD\t\t").append("LENGTH\t").append("GRADIENT_TYPES\t").append("GAP\t").append("1st_HIGH\t")
                .append("2nd_HIGH\t").append("GRADIENTS\t").append("NOTES\t");
        return sb.toString();
    }

    @Override
    public SeriesSegment getHigherSeriesAtStart() {
        return getHigherSeriesFromValues(this.firstSeriesSegment.getStartValue(),
                this.secondSeriesSegment.getStartValue());
    }

    @Override
    public SeriesSegment getHigherSeriesAtEnd() {
        return getHigherSeriesFromValues(this.firstSeriesSegment.getEndValue(), this.secondSeriesSegment.getEndValue());
    }

    private SeriesSegment getHigherSeriesFromValues(final double firstSeriesValue, final double secondSeriesValue) {
        SeriesSegment higherSeriesSegment = null;
        if (firstSeriesValue > secondSeriesValue) {
            higherSeriesSegment = this.firstSeriesSegment;
        } else if (firstSeriesValue < secondSeriesValue) {
            higherSeriesSegment = this.secondSeriesSegment;
        }
        return higherSeriesSegment;
    }

    @Override
    public GradientType getFirstSeriesTrend() {
        return this.firstSeriesSegment.getGradientType();
    }

    @Override
    public GradientType getSecondSeriesTrend() {
        return this.secondSeriesSegment.getGradientType();
    }

    @Override
    public int indexOf(final SeriesSegment seriesSegment) {
        int index = -1;
        if (this.firstSeriesSegment.equals(seriesSegment)) {
            index = 0;
        } else if (this.secondSeriesSegment.equals(seriesSegment)) {
            index = 1;
        }
        return index;
    }

    @Override
    public boolean isGlobalMaximumGapAtSegmentEnd() {
        return this.endValuesAreGlobalMaximumGap;
    }

    @Override
    public void setGlobalMaximumGapAtSegmentEnd(final boolean b) {
        this.endValuesAreGlobalMaximumGap = b;

    }

    @Override
    public boolean isGlobalMinimumGapAtSegmentEnd() {
        return this.endValuesAreGlobalMinimumGap;
    }

    @Override
    public void setGlobalMinimumGapAtSegmentEnd(final boolean b) {
        this.endValuesAreGlobalMinimumGap = b;
    }

    @Override
    public double getGapBetweenSeriesEndValues() {
        return Math.abs(this.getSeriesSegment(0).getEndValue() - this.getSeriesSegment(1).getEndValue());
    }
}
