package uk.co.lgs.model.graph;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import uk.co.lgs.domain.exception.DomainException;
import uk.co.lgs.domain.graph.GraphData;
import uk.co.lgs.domain.record.Record;
import uk.co.lgs.domain.record.RecordImpl;
import uk.co.lgs.model.segment.exception.SegmentCategoryNotFoundException;
import uk.co.lgs.model.segment.graph.GraphSegment;
import uk.co.lgs.test.AbstractTest;

/**
 * The test assumes that all graphs depict two series.
 * 
 * @author bouncysteve
 *
 */
public class GraphModelImplTest extends AbstractTest {

    @Mock
    private GraphData mockGraphData;

    @Mock
    private GraphSegment mockGraphSegment;

    private List<Record> records;

    private GraphModel underTest;

    @Before
    public void setup() {
        this.records = new ArrayList<Record>();
    }

    @Test
    public void test() throws DomainException, SegmentCategoryNotFoundException {
        givenAGraphWithTwoSeriesAndRecords(3);
        whenTheGraphModelIsCreated();
        thenItWillContainThisManySegments(2);
        thenItWillHaveLength(2);
    }

    @Test
    public void testAppend() throws DomainException, SegmentCategoryNotFoundException {
        givenAGraphWithTwoSeriesAndRecords(3);
        whenTheGraphModelIsCreated();
        whenASegmentIsAppendedWithLength(1);
        thenItWillContainThisManySegments(3);
        thenItWillHaveLength(3);
    }

    private void whenASegmentIsAppendedWithLength(int length) {
        when(this.mockGraphSegment.getLength()).thenReturn(length);
        this.underTest.append(this.mockGraphSegment);
    }

    @Test
    public void testAppendLongSegment() throws DomainException, SegmentCategoryNotFoundException {
        givenAGraphWithTwoSeriesAndRecords(3);
        whenTheGraphModelIsCreated();
        whenASegmentIsAppendedWithLength(2);
        thenItWillContainThisManySegments(3);
        thenItWillHaveLength(4);
    }

    private void thenItWillHaveLength(int i) {
        assertEquals(i, this.underTest.getLength());
    }

    private void givenAGraphWithTwoSeriesAndRecords(int recordCount) throws DomainException {
        for (int i = 0; i < recordCount; i++) {
            this.records.add(new RecordImpl("Label" + i, Arrays.asList(1d * i, 2d * i)));
        }
    }

    private void whenTheGraphModelIsCreated() throws SegmentCategoryNotFoundException {
        when(this.mockGraphData.getRecords()).thenReturn(this.records);
        this.underTest = new GraphModelImpl(this.mockGraphData);
    }

    private void thenItWillContainThisManySegments(int i) {
        assertEquals(i, this.underTest.getSegmentCount());
    }
}
