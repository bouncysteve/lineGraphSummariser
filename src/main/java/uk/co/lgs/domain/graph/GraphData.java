package uk.co.lgs.domain.graph;

import java.util.List;

import uk.co.lgs.domain.graph.iscatter.schema.Schema;
import uk.co.lgs.domain.record.Record;

public interface GraphData {

    int getDataRecordCount();

    List<String> getHeader();

    List<Record> getRecords();

    Schema getSchema();

    int getSchemaAttributeCount();

    int getSeriesCount();

    String getTitle();
}