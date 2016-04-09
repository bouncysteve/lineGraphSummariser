package uk.co.lgs.domain.loader;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.lgs.domain.graph.GraphData;
import uk.co.lgs.domain.loader.exception.LoaderException;
import uk.co.lgs.domain.loader.iscatter.IscatterLoaderImpl;
import uk.co.lgs.domain.record.Record;
import uk.co.lgs.test.AbstractTest;

public class IscatterLoaderImplTest extends AbstractTest {

    private static final String BAD_OR_EMPTY_HEADER_MESSAGE = "File exists but is empty or malformed";

    private static final String MISSING_DATA_MESSAGE = "Can't find a file called data.csv at this location";

    private static final String MISSING_FOLDER_MESSAGE_PREFIX = "Can't find a folder with the name ";

    private static final String MISSING_SCHEMA_MESSAGE = "Can't find a file called schema.csv at this location";

    ClassLoader classLoader;

    private static final Logger LOG = LoggerFactory.getLogger(IscatterLoaderImplTest.class);

    private File parentDir;

    Loader underTest;

    private GraphData graphData;

    @Before
    public void setup() {
        this.classLoader = getClass().getClassLoader();
    }

    @Test
    public void dataFileIsEmpty() throws LoaderException {
        givenILoadTheContentsOfDirectory("folderWithEmptyData");
        expectLoaderExceptionWithMessage(BAD_OR_EMPTY_HEADER_MESSAGE + ": " + "data.csv");
        whenICallIscatterLoader();
    }

    @Test
    public void nullFolderPassed() throws LoaderException {
        givenILoadTheContentsOfDirectory(null);
        expectNullPointerException();
        whenICallIscatterLoader();
    }

    @Test
    public void parentFolderDoesNotContainDataCsvFile() throws LoaderException {
        givenILoadTheContentsOfDirectory("folderWithNoData");
        expectLoaderExceptionWithMessage(MISSING_DATA_MESSAGE);
        whenICallIscatterLoader();
    }

    @Test
    public void parentFolderDoesNotContainSchemaCsvFile() throws LoaderException {
        givenILoadTheContentsOfDirectory("folderWithNoSchema");
        expectLoaderExceptionWithMessage(MISSING_SCHEMA_MESSAGE);
        whenICallIscatterLoader();
    }

    /**
     * To avoid a NPE, this test constructs a file from a filename and passes it
     * to the class under test. All other tests will use the class loader.
     * 
     * @throws LoaderException
     */
    @Test
    public void parentFolderDoesNotExist() throws LoaderException {
        expectLoaderExceptionWithMessage(MISSING_FOLDER_MESSAGE_PREFIX + "doesNotExist");
        givenILoadTheContentsOfADirectoryWhichDoesNotExist();
        whenICallIscatterLoader();
    }

    @Test
    public void parentFolderIsAFile() throws LoaderException {
        givenILoadTheContentsOfDirectory("simpleGraph/schema.csv");
        expectLoaderExceptionWithMessage(MISSING_FOLDER_MESSAGE_PREFIX);
        whenICallIscatterLoader();
    }

    @Test
    public void schemaFileIsEmpty() throws LoaderException {
        givenILoadTheContentsOfDirectory("folderWithEmptyFiles");
        expectLoaderExceptionWithMessage(BAD_OR_EMPTY_HEADER_MESSAGE + ": " + "schema.csv");
        whenICallIscatterLoader();
    }

    @Test
    public void sunnyDayScenario() throws LoaderException {
        givenILoadTheContentsOfDirectory("simpleGraph");
        whenICallIscatterLoader();
        assertEquals(12, this.graphData.getDataRecordCount());
        assertEquals(3, this.graphData.getSchemaAttributeCount());
        for (Record record : this.graphData.getRecords()) {
            assertEquals(2, record.getValues().size());
        }
    }

    private void whenICallIscatterLoader() throws LoaderException {
        this.underTest = new IscatterLoaderImpl();
        this.graphData = this.underTest.getGraph(this.parentDir);
    }

    private void expectLoaderExceptionWithMessage(String message) {
        this.expectedEx.expect(LoaderException.class);
        this.expectedEx.expectMessage(message);
    }

    private void expectNullPointerException() {
        this.expectedEx.expect(NullPointerException.class);
    }

    private void givenILoadTheContentsOfDirectory(String dirName) {
        try {
            this.parentDir = new File(this.classLoader.getResource(dirName).getFile());
        } catch (NullPointerException e) {
            if (this.LOG.isDebugEnabled()) {
                this.LOG.debug("The file doesn't exist: " + dirName);
            }
        }
    }

    private void givenILoadTheContentsOfADirectoryWhichDoesNotExist() {
        String dirName = "doesNotExist";
        this.parentDir = new File(dirName);
        this.expectedEx.expect(LoaderException.class);
        this.expectedEx.expectMessage(MISSING_FOLDER_MESSAGE_PREFIX + dirName);
    }

}
