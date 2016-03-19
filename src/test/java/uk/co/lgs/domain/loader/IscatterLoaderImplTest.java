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

import uk.co.lgs.domain.loader.exception.LoaderException;

@RunWith(MockitoJUnitRunner.class)
public class IscatterLoaderTest {
	
	private static final String MISSING_FOLDER_MESSAGE_PREFIX = "Can't find a folder with the name ";

	private static final String MISSING_SCHEMA_MESSAGE = "Can't find a file called schema.csv at this location";
	
	private static final String MISSING_DATA_MESSAGE = "Can't find a file called data.csv at this location";

	private static final String BAD_OR_EMPTY_SCHEMA_MESSAGE = "File schema.csv exists but is empty or malformed";

	private static final String BAD_OR_EMPTY_DATA_MESSAGE = "File data.csv exists but is empty or malformed";

	private final Logger logger = LoggerFactory.getLogger(IscatterLoaderTest.class);
	
	IscatterLoader underTest;
	
	ClassLoader classLoader;
	
	private File parentDir;
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Before
	public void setup() {
		classLoader = getClass().getClassLoader();
	}

	@Test
	public void sunnyDayScenario() throws LoaderException{
		givenILoadTheContentsOfDirectory("simpleGraph");
		whenICallIscatterLoader();
		assertEquals(12, underTest.getRecordCount());
	}
	 
	@Test
	public void nullFolderPassed() throws LoaderException{
		expectExceptionWithType(NullPointerException.class);
		underTest = new IscatterLoader(null);
	}
	
	/** To avoid a NPE, this test constructs a file from a filename and passes it to the class under test.
	 * All other tests will use the class loader.
	 * @throws LoaderException
	 */
	@Test
	public void parentFolderDoesNotExist() throws LoaderException{
		expectExceptionWithTypeAndMessage(LoaderException.class, MISSING_FOLDER_MESSAGE_PREFIX + "doesNotExist");
	    givenILoadTheContentsOfDirectoryDoesNotExist();
	    whenICallIscatterLoader();
	}

	@Test
	public void parentFolderIsAFile() throws LoaderException{
		givenILoadTheContentsOfDirectory("simpleGraph/schema.csv");
		expectExceptionWithTypeAndMessage(LoaderException.class, MISSING_FOLDER_MESSAGE_PREFIX);
	    whenICallIscatterLoader();
	}

	@Test
	public void parentFolderExists() throws LoaderException {
		givenILoadTheContentsOfDirectory("simpleGraph");
		whenICallIscatterLoader();
	}
	
	@Test
	public void parentFolderDoesNotContainSchemaCsvFile() throws LoaderException {
		givenILoadTheContentsOfDirectory("folderWithNoSchema");
		expectExceptionWithTypeAndMessage(LoaderException.class, MISSING_SCHEMA_MESSAGE);
	    whenICallIscatterLoader();
	}
	
	@Test
	public void parentFolderDoesNotContainDataCsvFile() throws LoaderException {
		givenILoadTheContentsOfDirectory("folderWithNoData");
		expectExceptionWithTypeAndMessage(LoaderException.class,MISSING_DATA_MESSAGE);
	    whenICallIscatterLoader();
	}
	
	@Test
	public void schemaFileIsEmpty() throws LoaderException {
		givenILoadTheContentsOfDirectory("folderWithEmptyFiles");
		expectExceptionWithTypeAndMessage(LoaderException.class, BAD_OR_EMPTY_SCHEMA_MESSAGE);
	    whenICallIscatterLoader();
	}
	
	@Test
	public void dataFileIsEmpty() throws LoaderException {
		givenILoadTheContentsOfDirectory("folderWithEmptyData");
		expectExceptionWithTypeAndMessage(LoaderException.class, BAD_OR_EMPTY_DATA_MESSAGE);
	    whenICallIscatterLoader();
	}
	
	private void givenILoadTheContentsOfDirectory(String dirName){
		try{
			parentDir = new File(classLoader.getResource(dirName).getFile());
		} catch (NullPointerException e){
			if (logger.isDebugEnabled()){
				logger.debug("The file doesn't exist: " + dirName);
			}
		}
	}
	

	private void givenILoadTheContentsOfDirectoryDoesNotExist() {
		String dirName = "doesNotExist";
		parentDir = new File(dirName);
		expectedEx.expect(LoaderException.class);
	    expectedEx.expectMessage(MISSING_FOLDER_MESSAGE_PREFIX + dirName);		
	}
	
	private void expectExceptionWithTypeAndMessage(Class clazz, String message) {
		expectedEx.expect(clazz);
	    expectedEx.expectMessage(message);
	}
	
	private void expectExceptionWithType(Class clazz) {
		expectedEx.expect(clazz);
	}
	
	private void whenICallIscatterLoader() throws LoaderException{
		underTest = new IscatterLoader(parentDir);
	}
}
