package uk.co.lgs.model.segment.series;

import uk.co.lgs.model.gradient.GradientType;

public interface SeriesSegment {

    double getStartValue();

    String getStartTime();

    double getEndValue();

    String getEndTime();

    GradientType getGradientType();

    double getGradient();

    int getSegmentLength();

    SeriesSegment append(SeriesSegment segmentToAppend);

    String getLabel();

    String getUnits();

}