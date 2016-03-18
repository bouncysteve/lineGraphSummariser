package uk.co.lgs.domain;

import java.util.List;

public interface Record {

	List<Double> getValues();

	int getCount();

	String getLabel();

}