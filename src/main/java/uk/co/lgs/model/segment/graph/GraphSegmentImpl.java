package uk.co.lgs.model.segment.graph;

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
import uk.co.lgs.model.segment.graph.category.GraphSegmentCategory;
import uk.co.lgs.model.segment.series.SeriesSegment;

public class GraphSegmentImpl implements GraphSegment {

    private static final String APPEND_INVALID_GRADIENT_MESSAGE = "Cannot append segment, the gradient types do not match";

    private static final Logger LOG = LoggerFactory.getLogger(GraphSegmentImpl.class);

    private GraphSegmentCategory segmentCategory;

    private Double valueAtIntersection = null;

    private List<SeriesSegment> seriesSegments;

    private boolean intersecting;

    private double segmentDistanceToIntersection;

    private boolean parallel;

    public GraphSegmentImpl(SeriesSegment firstSeriesSegment, SeriesSegment secondSeriesSegment)
            throws SegmentCategoryNotFoundException {
        this.seriesSegments = new ArrayList<SeriesSegment>();
        this.seriesSegments.add(firstSeriesSegment);
        this.seriesSegments.add(secondSeriesSegment);
        determineIntersectionDetails();
        determineSegmentCategory();
    }

    @Override
    public boolean isIntersecting() {
        return this.intersecting;
    }

    @Override
    public Double getValueAtIntersection() {
        return this.valueAtIntersection;
    }

    @Override
    public boolean isParallel() {
        return this.parallel;
    }

    @Override
    public GraphSegmentCategory getSegmentCategory() {
        return this.segmentCategory;
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
        StringBuilder sb = new StringBuilder();
        sb.append("Start: ").append(this.getStartTime()).append("\t");
        sb.append("End: ").append(this.getEndTime()).append("\t");
        sb.append("Length: ").append(this.getLength()).append("\t");
        sb.append("Cat: ").append(this.getSegmentCategory()).append("\t");
        if (!this.getSegmentCategory().isIntersecting()) {
            sb.append("\t").append("\t");
        }
        if (GraphSegmentCategory.ZERO_ZERO_INTERSECTING.equals(this.getSegmentCategory())) {
            sb.append("\t");
        }
        for (SeriesSegment seriesSegment : this.seriesSegments) {
            sb.append("S" + this.seriesSegments.indexOf(seriesSegment) + " gradient: ")
                    .append(df.format(seriesSegment.getGradient())).append("\t");
        }
        if (this.isIntersecting()) {
            sb.append("Intersection: ").append(this.getValueAtIntersection()).append("\t");
        }
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
        SeriesSegment segment1 = this.seriesSegments.get(0);
        SeriesSegment segment2 = this.seriesSegments.get(1);
        double firstSeriesStartValue = segment1.getStartValue();
        double firstSeriesEndValue = segment1.getEndValue();
        double secondSeriesStartValue = segment2.getStartValue();
        double secondSeriesEndValue = segment2.getEndValue();

        /*
         * equation of series: y = mx + c; y = (endValue - startValue)x +
         * startValue; -y = (startValue - endValue)x - startValue; (startValue -
         * endValue)x + y = startValue; {startValue - endValue, 1} =
         * {startValue}
         * http://commons.apache.org/proper/commons-math/userguide/linear.html
         */
        RealMatrix coefficients = new Array2DRowRealMatrix(
                new double[][] { { firstSeriesStartValue - firstSeriesEndValue, 1 },
                        { secondSeriesStartValue - secondSeriesEndValue, 1 } },
                false);
        DecompositionSolver solver = new LUDecomposition(coefficients).getSolver();
        RealVector constants = new ArrayRealVector(new double[] { firstSeriesStartValue, secondSeriesStartValue },
                false);
        try {
            RealVector solution = solver.solve(constants);
            this.segmentDistanceToIntersection = solution.getEntry(0);
            /*
             * There may be a solution to this simultaneous equation, but it
             * only makes sense if it is within this segment.
             */
            if (this.segmentDistanceToIntersection >= 0 && this.segmentDistanceToIntersection <= 1) {
                this.valueAtIntersection = solution.getEntry(1);
                this.intersecting = (null != this.valueAtIntersection);
            }
        } catch (SingularMatrixException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Lines are parallel, they either don't intersect or they are the identical");
            }
            this.parallel = true;
            if (firstSeriesStartValue == secondSeriesStartValue) {
                this.intersecting = true;
                if (firstSeriesStartValue == firstSeriesEndValue) {
                    // series are constant, so we can give an intersection value
                    this.valueAtIntersection = firstSeriesStartValue;
                }
            }
        }
    }

    private void determineSegmentCategory() throws SegmentCategoryNotFoundException {
        GradientType firstSeriesGradient = this.seriesSegments.get(0).getGradientType();
        GradientType secondSeriesGradient = this.seriesSegments.get(1).getGradientType();
        for (GraphSegmentCategory category : GraphSegmentCategory.values()) {
            if (category.getFirstSeriesGradient().equals(firstSeriesGradient)
                    && category.getSecondSeriesGradient().equals(secondSeriesGradient)
                    && category.isIntersecting() == this.intersecting) {
                this.segmentCategory = category;
                break;
            }
        }
        if (null == this.segmentCategory) {
            throw new SegmentCategoryNotFoundException();
        }
    }

    @Override
    public GraphSegment append(GraphSegment newSegment)
            throws SegmentCategoryNotFoundException, SegmentAppendException {
        // check that the type is the same, if not, throw an exception.
        // TODO: handle near same type (same but with/without intersection)
        for (SeriesSegment seriesSegment : this.seriesSegments) {
            SeriesSegment segmentToAppend = newSegment.getSeriesSegment(this.seriesSegments.indexOf(seriesSegment));
            if (seriesSegment.getGradientType().equals(segmentToAppend.getGradientType())) {
                seriesSegment = seriesSegment.append(segmentToAppend);
            } else
                throw new SegmentAppendException(APPEND_INVALID_GRADIENT_MESSAGE);
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
    public SeriesSegment getSeriesSegment(int index) {
        return this.seriesSegments.get(index);
    }
}
