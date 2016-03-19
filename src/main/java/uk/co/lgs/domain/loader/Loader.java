package uk.co.lgs.domain.loader;

import java.util.List;

import org.apache.commons.csv.CSVRecord;

public interface Loader {

	//TODO: Instead of these 6 getters, construct a graph object and return that.
	CSVRecord getDataHeader();

	int getDataRecordCount();

	List<CSVRecord> getData();

	CSVRecord getSchemaHeader();

	int getSchemaRecordCount();

	List<CSVRecord> getSchema();

}