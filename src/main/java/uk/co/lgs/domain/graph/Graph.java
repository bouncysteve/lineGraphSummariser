package uk.co.lgs.domain.graph;

import java.util.Collection;

import uk.co.lgs.domain.record.RecordImpl;

public interface Graph {

	String getTitle();

	Collection<RecordImpl> getRecords();

}