package uk.co.lgs.domain.record;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import uk.co.lgs.domain.exception.DomainException;
import uk.co.lgs.domain.record.Record;
import uk.co.lgs.domain.record.RecordImpl;

@RunWith(MockitoJUnitRunner.class)
public class RecordTest {

	Record underTest;
	
	private static String MISSING_VALUES_MESSAGE = "Record cannot be created without values";
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Test
	public void testSunnyDay() throws DomainException {
		List<Double> values = new ArrayList<Double>();
		values.add(5.2);
		values.add(4.5);
		values.add(3.9);
		underTest = new RecordImpl(values);
		assertEquals(3, underTest.getCount());
		List<Double> returnedValues = underTest.getValues();
		assertEquals(values, returnedValues);
		assertNull(underTest.getLabel());
	}
	
	@Test
	public void testFailIfNullValueList() throws DomainException{
		expectedEx.expect(DomainException.class);
	    expectedEx.expectMessage(MISSING_VALUES_MESSAGE);
	    underTest = new RecordImpl(null);
	}
	
	@Test
	public void testFailIfNoValues() throws DomainException{
		expectedEx.expect(DomainException.class);
	    expectedEx.expectMessage(MISSING_VALUES_MESSAGE);
	    List<Double> values = new ArrayList<Double>();
		underTest = new RecordImpl(values);
	}

}
