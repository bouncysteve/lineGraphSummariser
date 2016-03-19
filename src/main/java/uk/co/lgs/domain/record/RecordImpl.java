package uk.co.lgs.domain.record;

import java.util.List;

import uk.co.lgs.domain.exception.DomainException;

/**
 * Domain class representing a data series.
 * 
 * @author bouncysteve
 *
 */
public class RecordImpl implements Record {
	
	private static String MISSING_VALUES_MESSAGE = "Record cannot be created without values";
	
	public RecordImpl(String label, List<Double> values) throws DomainException{
		this(values);
		this.label = label;
	}
	
    public RecordImpl(List<Double> values) throws DomainException {
        super();
        if (null == values || values.isEmpty()){
        	throw new DomainException(MISSING_VALUES_MESSAGE);
        }
        this.values = values;
    }

    String label;
    List<Double> values;

    /* (non-Javadoc)
	 * @see uk.co.lgs.domain.Record#getValues()
	 */
    @Override
	public List<Double> getValues() {
        return values;
    }
    
    /* (non-Javadoc)
	 * @see uk.co.lgs.domain.Record#getCount()
	 */
    @Override
	public int getCount() {
    	return values.size();
    }
    
    /* (non-Javadoc)
	 * @see uk.co.lgs.domain.Record#getLabel()
	 */
    @Override
	public String getLabel(){
    	return label;
    }
}
