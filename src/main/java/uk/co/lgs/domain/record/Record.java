package uk.co.lgs.domain.record;

import java.util.List;

/**
 * I am a vertical slice of a graph, with one value for each series and one for
 * the x-axis label.
 * 
 * @author bouncysteve
 *
 */
public interface Record {

    List<Double> getValues();

    int getCount();

    String getPointInTime();

}