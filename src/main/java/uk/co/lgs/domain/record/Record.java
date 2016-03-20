package uk.co.lgs.domain.record;

import java.util.List;

public interface Record {

    List<Double> getValues();

    int getCount();

    String getLabel();

}