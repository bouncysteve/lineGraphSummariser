package uk.co.lgs.domain.graph;

import java.util.List;

import uk.co.lgs.domain.graph.iscatter.schema.Schema;

public interface GraphData {

    int getDataRecordCount();

    List<String> getHeader();

    List<List<String>> getRecords();

    Schema getSchema();

    int getSchemaAttributeCount();

    int getSeriesCount();

    String getTitle();
}