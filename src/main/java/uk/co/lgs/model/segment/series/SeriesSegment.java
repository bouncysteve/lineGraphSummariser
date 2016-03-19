package uk.co.lgs.model.segment;

import uk.co.lgs.model.GradientType;

public interface SeriesSegment {

	double getStartValue();

	String getStartTime();

	double getEndValue();

	String getEndTime();

	GradientType getGradientType();

	int getSegmentLength();

}