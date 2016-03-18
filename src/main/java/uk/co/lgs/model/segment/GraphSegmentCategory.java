package uk.co.lgs.model.segment;

import uk.co.lgs.model.Gradient;

public enum GraphSegmentCategory {
	ZERO_ZERO (Gradient.ZERO, Gradient.ZERO, false),
	NEGATIVE_ZERO(Gradient.NEGATIVE, Gradient.ZERO, false),
	POSITIVE_ZERO(Gradient.POSITIVE, Gradient.ZERO, false),
	ZERO_POSITIVE(Gradient.ZERO, Gradient.POSITIVE, false),
	NEGATIVE_POSITIVE(Gradient.NEGATIVE, Gradient.POSITIVE, false),
	POSITIVE_POSITIVE(Gradient.POSITIVE, Gradient.POSITIVE, false),
	ZERO_NEGATIVE(Gradient.ZERO, Gradient.NEGATIVE, false),
	NEGATIVE_NEGATIVE(Gradient.NEGATIVE, Gradient.NEGATIVE, false),
	POSITIVE_NEGATIVE(Gradient.POSITIVE, Gradient.NEGATIVE, false),
	ZERO_ZERO_INTERSECTING (Gradient.ZERO, Gradient.ZERO, true),
	NEGATIVE_ZERO_INTERSECTING(Gradient.NEGATIVE, Gradient.ZERO, true),
	POSITIVE_ZERO_INTERSECTING(Gradient.POSITIVE, Gradient.ZERO, true),
	ZERO_POSITIVE_INTERSECTING(Gradient.ZERO, Gradient.POSITIVE, true),
	NEGATIVE_POSITIVE_INTERSECTING(Gradient.NEGATIVE, Gradient.POSITIVE, true),
	POSITIVE_POSITIVE_INTERSECTING(Gradient.POSITIVE, Gradient.POSITIVE, true),
	ZERO_NEGATIVE_INTERSECTING(Gradient.ZERO, Gradient.NEGATIVE, true),
	NEGATIVE_NEGATIVE_INTERSECTING(Gradient.NEGATIVE, Gradient.NEGATIVE, true),
	POSITIVE_NEGATIVE_INTERSECTING(Gradient.POSITIVE, Gradient.NEGATIVE, true);
	
	private boolean intersecting;
	Gradient  firstSeriesGradient;
	Gradient secondSeriesGradient;
	
	private GraphSegmentCategory (Gradient firstSeriesGradient, Gradient secondSeriesGradient, boolean intersecting){
		this.intersecting = intersecting;
		this.firstSeriesGradient = firstSeriesGradient;
		this.secondSeriesGradient = secondSeriesGradient;
	}
	
	public boolean isIntersecting(){
		return this.intersecting;
	}
	
	public Gradient getFirstSeriesGradient(){
		return this.firstSeriesGradient;
	}
	
	public Gradient getSecondSeriesGradient(){
		return this.secondSeriesGradient;
	}
	
}
