package uk.co.lgs.model.segment.graph.category;

import uk.co.lgs.model.gradient.GradientType;

/**
 * I encapsulate the gradient types of the two series within a segment, and
 * whether the series intersect at some point in the segment.
 * 
 * @author bouncysteve
 *
 */
public enum GraphSegmentGradient {
    ZERO_ZERO(GradientType.ZERO, GradientType.ZERO, false), NEGATIVE_ZERO(GradientType.NEGATIVE, GradientType.ZERO,
            false), POSITIVE_ZERO(GradientType.POSITIVE, GradientType.ZERO, false), ZERO_POSITIVE(GradientType.ZERO,
                    GradientType.POSITIVE,
                    false), NEGATIVE_POSITIVE(GradientType.NEGATIVE, GradientType.POSITIVE, false), POSITIVE_POSITIVE(
                            GradientType.POSITIVE, GradientType.POSITIVE,
                            false), ZERO_NEGATIVE(GradientType.ZERO, GradientType.NEGATIVE, false), NEGATIVE_NEGATIVE(
                                    GradientType.NEGATIVE, GradientType.NEGATIVE,
                                    false), POSITIVE_NEGATIVE(GradientType.POSITIVE, GradientType.NEGATIVE,
                                            false), ZERO_ZERO_INTERSECTING(GradientType.ZERO, GradientType.ZERO,
                                                    true), NEGATIVE_ZERO_INTERSECTING(GradientType.NEGATIVE,
                                                            GradientType.ZERO, true), POSITIVE_ZERO_INTERSECTING(
                                                                    GradientType.POSITIVE, GradientType.ZERO,
                                                                    true), ZERO_POSITIVE_INTERSECTING(GradientType.ZERO,
                                                                            GradientType.POSITIVE,
                                                                            true), NEGATIVE_POSITIVE_INTERSECTING(
                                                                                    GradientType.NEGATIVE,
                                                                                    GradientType.POSITIVE,
                                                                                    true), POSITIVE_POSITIVE_INTERSECTING(
                                                                                            GradientType.POSITIVE,
                                                                                            GradientType.POSITIVE,
                                                                                            true), ZERO_NEGATIVE_INTERSECTING(
                                                                                                    GradientType.ZERO,
                                                                                                    GradientType.NEGATIVE,
                                                                                                    true), NEGATIVE_NEGATIVE_INTERSECTING(
                                                                                                            GradientType.NEGATIVE,
                                                                                                            GradientType.NEGATIVE,
                                                                                                            true), POSITIVE_NEGATIVE_INTERSECTING(
                                                                                                                    GradientType.POSITIVE,
                                                                                                                    GradientType.NEGATIVE,
                                                                                                                    true);

    private boolean intersecting;
    GradientType firstSeriesGradient;
    GradientType secondSeriesGradient;

    private GraphSegmentGradient(GradientType firstSeriesGradient, GradientType secondSeriesGradient,
            boolean intersecting) {
        this.intersecting = intersecting;
        this.firstSeriesGradient = firstSeriesGradient;
        this.secondSeriesGradient = secondSeriesGradient;
    }

    public boolean isIntersecting() {
        return this.intersecting;
    }

    public GradientType getFirstSeriesGradient() {
        return this.firstSeriesGradient;
    }

    public GradientType getSecondSeriesGradient() {
        return this.secondSeriesGradient;
    }

}
