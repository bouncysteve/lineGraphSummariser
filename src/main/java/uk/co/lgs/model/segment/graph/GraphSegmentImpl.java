package uk.co.lgs.model.segment.graph;

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
import uk.co.lgs.model.segment.exception.SegmentCategoryNotFoundException;
import uk.co.lgs.model.segment.graph.category.GraphSegmentCategory;
import uk.co.lgs.model.segment.series.SeriesSegment;

public class GraphSegmentImpl implements GraphSegment {

    private final Logger logger = LoggerFactory.getLogger(GraphSegmentImpl.class);

    private GraphSegmentCategory segmentCategory;

    private Double valueAtIntersection = null;

    private SeriesSegment firstSeriesSegment;

    private SeriesSegment secondSeriesSegment;

    private boolean intersecting;

    private double segmentDistanceToIntersection;

    private boolean parallel;

    public GraphSegmentImpl(SeriesSegment firstSeriesSegment, SeriesSegment secondSeriesSegment)
            throws SegmentCategoryNotFoundException {
        this.firstSeriesSegment = firstSeriesSegment;
        this.secondSeriesSegment = secondSeriesSegment;
        determineIntersectionDetails();
        determineSegmentCategory();
    }

    @Override
    public boolean isIntersecting() {
        return this.intersecting;
    }

    @Override
    public Object getPointOfIntersection() {
        return valueAtIntersection;
    }

    @Override
    public GraphSegmentCategory getRecordCategory() {
        return segmentCategory;
    }

    @Override
    public boolean isParallel() {
        return parallel;
    }

    @Override
    public GraphSegmentCategory getSegmentCategory() {
        return this.segmentCategory;
    }

    private void determineIntersectionDetails() {
        double firstSeriesStartValue = firstSeriesSegment.getStartValue();
        double firstSeriesEndValue = firstSeriesSegment.getEndValue();
        double secondSeriesStartValue = secondSeriesSegment.getStartValue();
        double secondSeriesEndValue = secondSeriesSegment.getEndValue();

        /*
         * equation of series: y = mx + c y = (endValue - startValue)x +
         * startValue -y = (startValue - endValue)x - startValue (startValue -
         * endValue)x + y = startValue {startValue - endValue, 1} = {startValue}
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
                this.intersecting = (null != valueAtIntersection);
            }
        } catch (SingularMatrixException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Lines are parallel, they either don't intersect or they are the identical");
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
        GradientType firstSeriesGradient = firstSeriesSegment.getGradientType();
        GradientType secondSeriesGradient = secondSeriesSegment.getGradientType();
        for (GraphSegmentCategory category : GraphSegmentCategory.values()) {
            if (category.getFirstSeriesGradient().equals(firstSeriesGradient)
                    && category.getSecondSeriesGradient().equals(secondSeriesGradient)
                    && category.isIntersecting() == intersecting) {
                this.segmentCategory = category;
                break;
            }
        }
        if (null == this.segmentCategory) {
            throw new SegmentCategoryNotFoundException();
        }
    }

    /*
     * public Object getDependentValue(){ //TODO: do something with time series
     * value from domainRecord
     * 
     * }
     */

}
