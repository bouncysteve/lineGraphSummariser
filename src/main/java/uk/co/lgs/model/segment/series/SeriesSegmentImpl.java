package uk.co.lgs.model.segment;

import java.util.Map;

import uk.co.lgs.model.GradientType;

public class SeriesSegmentImpl implements SeriesSegment {

	private Map<String, Double> startTimeAndValue;
	
	private Map<String, Double> endTimeAndValue;
	
	GradientType gradientType;
	
	/** We never need to know the exact gradient, so we assume that the interval between records is
	 * 1. If we have combined records (and we are taking an average gradient), then the segment length
	 * will be a multiple of 1.
	 */
	private int segmentLength;
	
	public SeriesSegmentImpl(Map<String, Double> startTimeAndValue, Map<String, Double> endTimeAndValue){
		this(startTimeAndValue, endTimeAndValue, 1);
	}
	
	public SeriesSegmentImpl(Map<String, Double> startTimeAndValue, Map<String, Double> endTimeAndValue, int segmentLength){
		this.startTimeAndValue = startTimeAndValue;
		this.endTimeAndValue = endTimeAndValue;
		this.segmentLength = segmentLength;
		this.gradientType = calculateGradient();
	}
	
	/* (non-Javadoc)
	 * @see uk.co.lgs.model.segment.SeriesSegment#getStartValue()
	 */
	@Override
	public double getStartValue(){
		return this.startTimeAndValue.values().iterator().next();
	}
	
	/* (non-Javadoc)
	 * @see uk.co.lgs.model.segment.SeriesSegment#getStartTime()
	 */
	@Override
	public String getStartTime(){
		return this.startTimeAndValue.keySet().iterator().next();
	}
	
	/* (non-Javadoc)
	 * @see uk.co.lgs.model.segment.SeriesSegment#getEndValue()
	 */
	@Override
	public double getEndValue(){
		return this.endTimeAndValue.values().iterator().next();
	}

	/* (non-Javadoc)
	 * @see uk.co.lgs.model.segment.SeriesSegment#getEndTime()
	 */
	@Override
	public String getEndTime(){
		return this.endTimeAndValue.keySet().iterator().next();
	}
	
	/* (non-Javadoc)
	 * @see uk.co.lgs.model.segment.SeriesSegment#getGradientType()
	 */
	@Override
	public GradientType getGradientType(){
		return this.gradientType;
	}
	
	/* (non-Javadoc)
	 * @see uk.co.lgs.model.segment.SeriesSegment#getSegmentLength()
	 */
	@Override
	public int getSegmentLength(){
		return this.segmentLength;
	}
	
	private GradientType calculateGradient() {
		double valueAtStart = startTimeAndValue.values().iterator().next();
		double valueAtEnd = endTimeAndValue.values().iterator().next();
		double numericGradient = (valueAtEnd - valueAtStart)/ segmentLength;
		GradientType gradientType;
		if (0 == numericGradient){
			gradientType = GradientType.ZERO;
		} else if (0 < numericGradient){
			gradientType = GradientType.POSITIVE;
		} else {
			gradientType = GradientType.NEGATIVE;
		}
		return gradientType;
	}
	
}
