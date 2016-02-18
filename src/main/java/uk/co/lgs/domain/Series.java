package uk.co.lgs.domain;

import java.util.List;

import uk.co.lgs.domain.exception.DomainException;

/**
 * Domain class representing a data series.
 * 
 * @author bouncysteve
 *
 */
public class Series {
	
	private static String MISSING_VALUES_MESSAGE = "Series cannot be created without values";
	
	public Series(String label, List<Double> values) throws DomainException{
		this(values);
		this.label = label;
	}
	
    public Series(List<Double> values) throws DomainException {
        super();
        if (null == values || values.isEmpty()){
        	throw new DomainException(MISSING_VALUES_MESSAGE);
        }
        this.values = values;
    }

    String label;
    List<Double> values;

    public List<Double> getValues() {
        return values;
    }
    
    public int getCount() {
    	return values.size();
    }
    
    public String getLabel(){
    	return label;
    }
}
