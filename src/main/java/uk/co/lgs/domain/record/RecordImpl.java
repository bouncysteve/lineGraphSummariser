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
    private String pointInTime;
    private List<Double> values;

    public RecordImpl(String pointInTime, List<Double> values) throws DomainException {
        this(values);
        this.pointInTime = pointInTime;
    }

    public RecordImpl(List<Double> values) throws DomainException {
        super();
        if (null == values || values.isEmpty()) {
            throw new DomainException(MISSING_VALUES_MESSAGE);
        }
        this.values = values;
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.co.lgs.domain.Record#getValues()
     */
    @Override
    public List<Double> getValues() {
        return this.values;
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.co.lgs.domain.Record#getCount()
     */
    @Override
    public int getCount() {
        return this.values.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.co.lgs.domain.Record#getLabel()
     */
    @Override
    public String getPointInTime() {
        return this.pointInTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.pointInTime).append("\t");
        for (Double value : this.values) {
            sb.append(value).append("\t");
        }
        return sb.toString();
    }
}
