package uk.co.lgs.domain.graph;

import java.util.List;

import uk.co.lgs.domain.graph.iscatter.schema.Schema;
import uk.co.lgs.domain.record.Record;

/**
 * I am a domain class representing a multi-series line graph. A graph consists
 * of a title, and multiple records. From the records the series can be derived.
 * 
 * @author bouncysteve
 */
public interface GraphData {

    int getDataRecordCount();

    List<String> getHeader();

    List<Record> getRecords();

    Schema getSchema();

    int getSchemaAttributeCount();

    int getSeriesCount();

    String getTitle();

    List<String> getUnits();
}