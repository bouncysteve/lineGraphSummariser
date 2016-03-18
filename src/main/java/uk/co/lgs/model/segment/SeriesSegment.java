package uk.co.lgs.model.segment;

import uk.co.lgs.model.Gradient;

public interface SeriesSegment {

	double getStartValue();

	String getStartTime();

	double getEndValue();

	String getEndTime();

	Gradient getGradientType();

	int getSegmentLength();

}