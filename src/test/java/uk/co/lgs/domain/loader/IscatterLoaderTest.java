package uk.co.lgs.domain.loader;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import uk.co.lgs.domain.loader.exception.LoaderException;

@RunWith(MockitoJUnitRunner.class)
public class IscatterLoaderTest {

	private static final String MISSING_FOLDER_MESSAGE_PREFIX = "Can't find a folder with the name ";

	private static final String MISSING_SCHEMA_MESSAGE = "Can't find a file called schema.csv at this location";
	
	private static final String MISSING_DATA_MESSAGE = "Can't find a file called data.csv at this location";

	private static final String BAD_OR_EMPTY_SCHEMA_MESSAGE = "File schema.csv exists but is empty or malformed";

	private static final String BAD_OR_EMPTY_DATA_MESSAGE = "File data.csv exists but is empty or malformed";

	IscatterLoader underTest;
	
	ClassLoader classLoader;
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Before
	public void setup() {
		classLoader = getClass().getClassLoader();
	}
	
	@Test
	public void nullFolderPassed() throws LoaderException{
		expectedEx.expect(NullPointerException.class);
	    underTest = new IscatterLoader(null);
	}
	
	/** To avoid a NPE, this test constructs a file from a filename and passes it to the class under test.
	 * All other tests will use the class loader.
	 * @throws LoaderException
	 */
	@Test
	public void parentFolderDoesNotExist() throws LoaderException{
		String fileName = "doesNotExist";
		File file = new File(fileName);
		expectedEx.expect(LoaderException.class);
	    expectedEx.expectMessage(MISSING_FOLDER_MESSAGE_PREFIX + fileName);
	    underTest = new IscatterLoader(file);
	}
	
	@Test
	public void parentFolderIsAFile() throws LoaderException{
		String fileName = "simpleGraph/schema.csv";
		File file = new File(classLoader.getResource(fileName).getFile());
		expectedEx.expect(LoaderException.class);
	    expectedEx.expectMessage(MISSING_FOLDER_MESSAGE_PREFIX);
	    underTest = new IscatterLoader(file);
	}
	
	@Test
	public void parentFolderExists() throws LoaderException {
		String fileName = "simpleGraph";
		File file = new File(classLoader.getResource(fileName).getFile());
		underTest = new IscatterLoader(file);
	}
	
	@Test
	public void parentFolderDoesNotContainSchemaCsvFile() throws LoaderException {
		String fileName = "folderWithNoSchema";
		expectedEx.expect(LoaderException.class);
	    expectedEx.expectMessage(MISSING_SCHEMA_MESSAGE);
	    File file = new File(classLoader.getResource(fileName).getFile());
		underTest = new IscatterLoader(file);
	}
	
	@Test
	public void parentFolderDoesNotContainDataCsvFile() throws LoaderException {
		String fileName = "folderWithNoData";
		expectedEx.expect(LoaderException.class);
	    expectedEx.expectMessage(MISSING_DATA_MESSAGE);
	    File file = new File(classLoader.getResource(fileName).getFile());
		underTest = new IscatterLoader(file);
	}
	
	@Test
	public void schemaFileIsEmpty() throws LoaderException {
		String fileName = "folderWithEmptyFiles";
		expectedEx.expect(LoaderException.class);
	    expectedEx.expectMessage(BAD_OR_EMPTY_SCHEMA_MESSAGE);
	    File file = new File(classLoader.getResource(fileName).getFile());
		underTest = new IscatterLoader(file);
	}
	
	@Test
	public void dataFileIsEmpty() throws LoaderException {
		String fileName = "folderWithEmptyData";
		expectedEx.expect(LoaderException.class);
	    expectedEx.expectMessage(BAD_OR_EMPTY_DATA_MESSAGE);
	    File file = new File(classLoader.getResource(fileName).getFile());
		underTest = new IscatterLoader(file);
	}
	
	@Test
	public void sunnyDayScenario() throws LoaderException{
		String fileName = "simpleGraph";
		File file = new File(classLoader.getResource(fileName).getFile());
		underTest = new IscatterLoader(file);
	}
}
