package uk.co.lgs.domain.loader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import uk.co.lgs.domain.loader.exception.LoaderException;

public class IscatterLoader {

	private final Logger logger = LoggerFactory.getLogger(IscatterLoader.class);
	
	private static final String MISSING_FOLDER_MESSAGE_PREFIX = "Can't find a folder with the name ";
	
	private static final String MISSING_SCHEMA_MESSAGE = "Can't find a file called schema.csv at this location";
	
	private static final String MISSING_DATA_MESSAGE = "Can't find a file called data.csv at this location";
	
	private static final String BAD_OR_EMPTY_SCHEMA_MESSAGE = "File schema.csv exists but is empty or malformed";

	private static final String SCHEMA_FILENAME = "schema.csv";
	
	private static final String DATA_FILENAME = "data.csv";
	
	File parentFolder;
	
	File schemaFile;
	
	File dataFile;

	public IscatterLoader(File parentFolder) throws LoaderException {
		super();
		if (!parentFolder.isDirectory()){
			throw new LoaderException(MISSING_FOLDER_MESSAGE_PREFIX + parentFolder); 
		}
		this.parentFolder = parentFolder;
		
		schemaFile = new File (parentFolder.getAbsolutePath() + "/" + SCHEMA_FILENAME);
		if (!schemaFile.isFile()){
			throw new LoaderException(MISSING_SCHEMA_MESSAGE);
		}
				
		dataFile = new File (parentFolder.getAbsolutePath() + "/" + DATA_FILENAME);
		if (!dataFile.isFile()){
			throw new LoaderException(MISSING_DATA_MESSAGE);
		}
		
		try {
			CSVParser parser = CSVParser.parse(schemaFile, Charset.defaultCharset(), CSVFormat.DEFAULT);
			boolean foundRecords = false;
			for (CSVRecord csvRecord: parser){
				foundRecords = true;
				if (logger.isDebugEnabled()){
					logger.debug(csvRecord.toString());
				}
			}
			if (!foundRecords){
				throw new LoaderException(BAD_OR_EMPTY_SCHEMA_MESSAGE);
			}
		} catch (IOException ioe){
			throw new LoaderException(BAD_OR_EMPTY_SCHEMA_MESSAGE);
		}

	}
	
	
}
