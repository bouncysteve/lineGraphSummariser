package uk.co.lgs.domain.loader;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.lgs.domain.graph.Graph;
import uk.co.lgs.domain.loader.exception.LoaderException;
import uk.co.lgs.domain.loader.iscatter.IscatterLoaderImpl;

@RunWith(MockitoJUnitRunner.class)
public class IscatterLoaderImplTest {

    private static final String BAD_OR_EMPTY_HEADER_MESSAGE = "File exists but is empty or malformed";

    private static final String MISSING_DATA_MESSAGE = "Can't find a file called data.csv at this location";

    private static final String MISSING_FOLDER_MESSAGE_PREFIX = "Can't find a folder with the name ";

    private static final String MISSING_SCHEMA_MESSAGE = "Can't find a file called schema.csv at this location";

    ClassLoader classLoader;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    private final Logger logger = LoggerFactory.getLogger(IscatterLoaderImplTest.class);

    private File parentDir;

    Loader underTest;

    @Test
    public void dataFileIsEmpty() throws LoaderException {
        givenILoadTheContentsOfDirectory("folderWithEmptyData");
        expectLoaderExceptionWithMessage(BAD_OR_EMPTY_HEADER_MESSAGE + ": " + "data.csv");
        whenICallIscatterLoader();
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
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("The file doesn't exist: " + dirName);
            }
        }
    }

    private void givenILoadTheContentsOfDirectoryDoesNotExist() {
        String dirName = "doesNotExist";
        this.parentDir = new File(dirName);
        this.expectedEx.expect(LoaderException.class);
        this.expectedEx.expectMessage(MISSING_FOLDER_MESSAGE_PREFIX + dirName);
    }

    @Test
    public void nullFolderPassed() throws LoaderException {
        expectNullPointerException();
        this.underTest = new IscatterLoaderImpl(null);
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
        givenILoadTheContentsOfDirectoryDoesNotExist();
        whenICallIscatterLoader();
    }

    @Test
    public void parentFolderExists() throws LoaderException {
        givenILoadTheContentsOfDirectory("simpleGraph");
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

    @Before
    public void setup() {
        this.classLoader = getClass().getClassLoader();
    }

    @Test
    public void sunnyDayScenario() throws LoaderException {
        givenILoadTheContentsOfDirectory("simpleGraph");
        whenICallIscatterLoader();
        Graph graph = this.underTest.getGraph();
        assertEquals(12, graph.getDataRecordCount());
        assertEquals(3, graph.getSchemaAttributeCount());
    }

    private void whenICallIscatterLoader() throws LoaderException {
        this.underTest = new IscatterLoaderImpl(this.parentDir);
    }
}
