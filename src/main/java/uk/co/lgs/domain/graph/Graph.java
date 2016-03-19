package uk.co.lgs.domain.graph;

import java.util.List;

import org.apache.commons.csv.CSVRecord;

import uk.co.lgs.domain.graph.iscatter.schema.Schema;

public interface Graph {

	CSVRecord getHeader();

	Schema getSchema();

	int getSeriesCount();
	
	int getRecordCount();

	List<CSVRecord> getRecords();
}