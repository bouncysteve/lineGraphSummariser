package uk.co.lgs.model.segment.series;

import uk.co.lgs.model.gradient.GradientType;

public interface SeriesSegment {

    double getStartValue();

    String getStartTime();

    double getEndValue();

    String getEndTime();

    GradientType getGradientType();

    int getSegmentLength();

}