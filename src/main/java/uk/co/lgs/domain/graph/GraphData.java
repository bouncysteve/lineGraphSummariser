package uk.co.lgs.domain.graph;

import java.util.List;

import uk.co.lgs.domain.graph.iscatter.schema.IScatterSchema;
import uk.co.lgs.domain.record.Record;

/**
 * I am a domain class representing a multi-series line graph. A graph consists
 * of a title, and multiple records. From the records the series can be derived.
 *
 * @author bouncysteve
 */
public interface GraphData {

    /**
     * @return the number of records(rows) in the data file
     */
    int getDataRecordCount();

    /**
     * @return the series labels
     */
    List<String> getLabels();

    /**
     * @return the series descriptions
     */
    List<String> getDescriptions();

    /**
     * @return a list of records (rows), each represents a vertical slice of the
     *         graph.
     */
    List<Record> getRecords();

    /**
     * @return the object representing the iScatter schema.csv
     */
    IScatterSchema getSchema();

    /**
     * @return the number of attributes in the iScatter schema.csv (this should
     *         match the number of series in the graph, including time)
     */
    int getSchemaAttributeCount();

    /**
     * @return the number of series in the graph.
     */
    int getSeriesCount();

    /**
     * @return the graph's title
     */
    String getTitle();

    /**
     * @return the units of each series, respectively
     */
    List<String> getUnits();
}