package uk.co.lgs.model.graph.service;

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

public class SegmentationServiceImplTest extends AbstractTest {

    @Mock
    private GraphData mockGraphData;

    private SegmentationServiceImpl underTest;

    private List<GraphSegment> returnedSegments;

    private List<Record> records;

    @Before
    public void setup() {
        this.underTest = new SegmentationServiceImpl();
        this.records = new ArrayList<Record>();
        when(this.mockGraphData.getHeader()).thenReturn(Arrays.asList(new String[] { "time", "series1", "series2" }));
    }

    @Test
    public void test() throws SegmentCategoryNotFoundException, DomainException {
        givenARecordWithTimeAndValues("time1", 1d, 1d);
        givenARecordWithTimeAndValues("time2", 2d, 0d);
        whenTheRecordsAreConvertedToSegments();
        ThenTheNumberOfSegmentsIs(1);
    }

    private void ThenTheNumberOfSegmentsIs(int i) {
        assertEquals(i, this.returnedSegments.size());
    }

    private void whenTheRecordsAreConvertedToSegments() throws SegmentCategoryNotFoundException {
        when(this.mockGraphData.getRecords()).thenReturn(this.records);
        when(this.mockGraphData.getUnits()).thenReturn(Arrays.asList("", "", ""));
        this.returnedSegments = this.underTest.segment(this.mockGraphData);

    }

    private void givenARecordWithTimeAndValues(String string, double d, double e) throws DomainException {
        this.records.add(new RecordImpl(string, Arrays.asList(d, e)));

    }

}
