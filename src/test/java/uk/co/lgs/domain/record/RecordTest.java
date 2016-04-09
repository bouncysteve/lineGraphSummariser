package uk.co.lgs.domain.record;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.co.lgs.domain.exception.DomainException;
import uk.co.lgs.test.AbstractTest;

public class RecordTest extends AbstractTest {

    private Record underTest;

    private static String MISSING_VALUES_MESSAGE = "Record cannot be created without values";

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testSunnyDay() throws DomainException {
        List<Double> values = new ArrayList<Double>();
        values.add(5.2);
        values.add(4.5);
        values.add(3.9);
        this.underTest = new RecordImpl(values);
        assertEquals(3, this.underTest.getCount());
        List<Double> returnedValues = this.underTest.getValues();
        assertEquals(values, returnedValues);
        assertNull(this.underTest.getPointInTime());
    }

    @Test
    public void testFailIfNullValueList() throws DomainException {
        this.expectedEx.expect(DomainException.class);
        this.expectedEx.expectMessage(MISSING_VALUES_MESSAGE);
        this.underTest = new RecordImpl(null);
    }

    @Test
    public void testFailIfNoValues() throws DomainException {
        this.expectedEx.expect(DomainException.class);
        this.expectedEx.expectMessage(MISSING_VALUES_MESSAGE);
        List<Double> values = new ArrayList<Double>();
        this.underTest = new RecordImpl(values);
    }

}
