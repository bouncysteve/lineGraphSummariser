package uk.co.lgs.domain.loader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.lgs.domain.loader.exception.LoaderException;

public class IscatterLoaderImpl implements Loader {

	private final Logger logger = LoggerFactory.getLogger(IscatterLoaderImpl.class);

	private static final String MISSING_FOLDER_MESSAGE_PREFIX = "Can't find a folder with the name ";

	private static final String MISSING_SCHEMA_MESSAGE = "Can't find a file called schema.csv at this location";

	private static final String MISSING_DATA_MESSAGE = "Can't find a file called data.csv at this location";

	private static final String BAD_OR_EMPTY_FILE_MESSAGE = "File exists but is empty or malformed";

	private static final String SCHEMA_FILENAME = "schema.csv";

	private static final String DATA_FILENAME = "data.csv";

	private static final String BAD_OR_EMPTY_HEADER_MESSAGE = "Header not found";

	private static final String BAD_OR_EMPTY_RECORDS_MESSAGE = "Records not found";

	File parentFolder;

	File schemaFile;

	File dataFile;

	List<CSVRecord> dataRecords, schemaRecords;

	private CSVRecord dataHeader, schemaHeader;

	public IscatterLoaderImpl(File parentFolder) throws LoaderException {
		super();
		if (!parentFolder.isDirectory()) {
			throw new LoaderException(MISSING_FOLDER_MESSAGE_PREFIX + parentFolder);
		}
		this.parentFolder = parentFolder;

		this.schemaFile = new File(parentFolder.getAbsolutePath() + "/" + SCHEMA_FILENAME);
		if (!schemaFile.isFile()) {
			throw new LoaderException(MISSING_SCHEMA_MESSAGE);
		}

		this.dataFile = new File(parentFolder.getAbsolutePath() + "/" + DATA_FILENAME);
		if (!dataFile.isFile()) {
			throw new LoaderException(MISSING_DATA_MESSAGE);
		}

		this.schemaRecords = new ArrayList<CSVRecord>();
		this.dataRecords = new ArrayList<CSVRecord>();

		parseFileIntoHeaderAndRecords(schemaFile, this.schemaHeader, this.schemaRecords);
		parseFileIntoHeaderAndRecords(dataFile, this.dataHeader, this.dataRecords);
	}

	
	//TODO: Instead of these 6 getters, construct a graph object and return that.
	/* (non-Javadoc)
	 * @see uk.co.lgs.domain.loader.Loader#getDataHeader()
	 */
	@Override
	public CSVRecord getDataHeader() {
		return this.dataHeader;
	}

	/* (non-Javadoc)
	 * @see uk.co.lgs.domain.loader.Loader#getDataRecordCount()
	 */
	@Override
	public int getDataRecordCount() {
		return this.dataRecords.size();
	}

	/* (non-Javadoc)
	 * @see uk.co.lgs.domain.loader.Loader#getData()
	 */
	@Override
	public List<CSVRecord> getData() {
		return this.dataRecords;
	}

	/* (non-Javadoc)
	 * @see uk.co.lgs.domain.loader.Loader#getSchemaHeader()
	 */
	@Override
	public CSVRecord getSchemaHeader() {
		return this.schemaHeader;
	}

	/* (non-Javadoc)
	 * @see uk.co.lgs.domain.loader.Loader#getSchemaRecordCount()
	 */
	@Override
	public int getSchemaRecordCount() {
		return this.schemaRecords.size();
	}

	/* (non-Javadoc)
	 * @see uk.co.lgs.domain.loader.Loader#getSchema()
	 */
	@Override
	public List<CSVRecord> getSchema() {
		return this.schemaRecords;
	}

	private void parseFileIntoHeaderAndRecords(File csvFile, CSVRecord headerRecord, List<CSVRecord> records)
			throws LoaderException {
		try {
			CSVParser parser = CSVParser.parse(csvFile, Charset.defaultCharset(), CSVFormat.DEFAULT);
			boolean foundHeader = false;
			boolean foundRecords = false;
			for (CSVRecord csvRecord : parser) {
				if (!foundHeader) {
					headerRecord = csvRecord;
					foundHeader = true;
				} else {
					foundRecords = true;
					if (logger.isDebugEnabled()) {
						logger.debug(csvRecord.toString());
					}
					records.add(csvRecord);
				}
			}
			if (!foundHeader) {
				throw new LoaderException(BAD_OR_EMPTY_HEADER_MESSAGE + ": " + csvFile.getName());
			}
			if (!foundRecords) {
				throw new LoaderException(BAD_OR_EMPTY_RECORDS_MESSAGE + ": " + csvFile.getName());
			}
		} catch (IOException ioe) {
			throw new LoaderException(BAD_OR_EMPTY_FILE_MESSAGE + ": " + csvFile.getName());
		}
	}
}
