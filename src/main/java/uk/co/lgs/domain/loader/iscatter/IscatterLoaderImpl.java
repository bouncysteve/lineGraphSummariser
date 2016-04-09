package uk.co.lgs.domain.loader.iscatter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import uk.co.lgs.domain.exception.DomainException;
import uk.co.lgs.domain.graph.GraphData;
import uk.co.lgs.domain.graph.GraphDataImpl;
import uk.co.lgs.domain.graph.iscatter.schema.Schema;
import uk.co.lgs.domain.graph.iscatter.schema.SchemaImpl;
import uk.co.lgs.domain.graph.iscatter.schema.exception.SchemaException;
import uk.co.lgs.domain.loader.Loader;
import uk.co.lgs.domain.loader.exception.LoaderException;
import uk.co.lgs.domain.record.Record;
import uk.co.lgs.domain.record.RecordImpl;

@Component
public class IscatterLoaderImpl implements Loader {

    private static final String BAD_OR_EMPTY_FILE_MESSAGE = "File exists but is empty or malformed";

    private static final String DATA_FILENAME = "data.csv";

    private static final String MISSING_DATA_MESSAGE = "Can't find a file called data.csv at this location";

    private static final String MISSING_FOLDER_MESSAGE_PREFIX = "Can't find a folder with the name ";

    private static final String MISSING_SCHEMA_MESSAGE = "Can't find a file called schema.csv at this location";

    private static final String SCHEMA_FILENAME = "schema.csv";

    private List<Record> dataRecords;

    private GraphData graphData;

    private Schema schema;

    private List<String> header;

    private File schemaFile, dataFile;

    public IscatterLoaderImpl() {
        super();
    }

    private void initialise(File parentFolder) throws LoaderException {
        if (!parentFolder.isDirectory()) {
            throw new LoaderException(MISSING_FOLDER_MESSAGE_PREFIX + parentFolder);
        }
        this.schemaFile = new File(parentFolder.getAbsolutePath() + "/" + SCHEMA_FILENAME);
        if (!this.schemaFile.isFile()) {
            throw new LoaderException(MISSING_SCHEMA_MESSAGE);
        }

        this.dataFile = new File(parentFolder.getAbsolutePath() + "/" + DATA_FILENAME);
        if (!this.dataFile.isFile()) {
            throw new LoaderException(MISSING_DATA_MESSAGE);
        }

        try {
            createSchema();
        } catch (SchemaException e) {
            throw new LoaderException("problem creating schema: ", e);
        }
        createRecords();
        try {
            createGraph();
        } catch (DomainException e) {
            throw new LoaderException("Problem creating graph", e);
        }
    }

    private void createGraph() throws DomainException {
        this.graphData = new GraphDataImpl(this.schema, this.header, this.dataRecords);
    }

    private void createRecords() throws LoaderException {
        this.dataRecords = parseFileIntoRecordCollection(this.dataFile);
    }

    private void createSchema() throws LoaderException, SchemaException {
        this.schema = new SchemaImpl(parseFileIntoSchema(this.schemaFile));
    }

    @Override
    public GraphData getGraph(File parentFolder) throws LoaderException {
        initialise(parentFolder);
        return this.graphData;
    }

    private List<List<String>> parseFileIntoSchema(File csvFile) throws LoaderException {
        List<List<String>> rows = new ArrayList<List<String>>();
        try {
            CSVParser parser = CSVParser.parse(csvFile, Charset.defaultCharset(), CSVFormat.DEFAULT);
            List<String> row;
            for (CSVRecord csvRecord : parser) {
                row = new ArrayList<String>();
                java.util.Iterator<String> it = csvRecord.iterator();
                while (it.hasNext()) {
                    row.add(it.next());
                }
                rows.add(row);
            }
        } catch (IOException ioe) {
            throw new LoaderException(BAD_OR_EMPTY_FILE_MESSAGE + ": " + csvFile.getName());
        }
        if (rows.isEmpty()) {
            throw new LoaderException(BAD_OR_EMPTY_FILE_MESSAGE + ": " + csvFile.getName());
        }
        return rows;
    }

    private List<Record> parseFileIntoRecordCollection(File csvFile) throws LoaderException {
        List<Record> records = new ArrayList<Record>();
        try {
            CSVParser parser = CSVParser.parse(csvFile, Charset.defaultCharset(), CSVFormat.DEFAULT);
            String timePoint;
            boolean gotHeader = false;
            for (CSVRecord csvRecord : parser) {
                List<Double> values = new ArrayList<Double>();
                java.util.Iterator<String> it = csvRecord.iterator();
                if (!gotHeader) {
                    List<String> header = new ArrayList<String>();
                    while (it.hasNext()) {
                        header.add(it.next());
                    }
                    this.header = header;
                    gotHeader = true;
                } else {
                    timePoint = it.next();
                    while (it.hasNext()) {
                        values.add(Double.parseDouble(it.next()));
                    }
                    records.add(new RecordImpl(timePoint, values));
                }
            }
        } catch (IOException ioe) {
            throw new LoaderException(BAD_OR_EMPTY_FILE_MESSAGE + ": " + csvFile.getName());
        } catch (DomainException de) {
            throw new LoaderException("Unable to create record");
        }
        if (records.isEmpty()) {
            throw new LoaderException(BAD_OR_EMPTY_FILE_MESSAGE + ": " + csvFile.getName());
        }
        return records;
    }
}
