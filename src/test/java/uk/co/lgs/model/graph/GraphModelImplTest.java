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
    // FIXME: this is not a unit test as real gapService and segmentationService
    // are used!!!!!!
    @Mock
    private GraphData mockGraphData;

    @Mock
    private GraphSegment mockGraphSegment;

    private List<Record> records;

    private GraphModel underTest;

    @Before
    public void setup() {
        this.records = new ArrayList<Record>();
        when(this.mockGraphData.getLabels()).thenReturn(Arrays.asList(new String[] { "time", "series1", "series2" }));
        when(this.mockGraphData.getDescriptions())
                .thenReturn(Arrays.asList(new String[] { "timeDesc", "series1Desc", "series2Desc" }));
    }

    @Test
    public void test() throws DomainException, SegmentCategoryNotFoundException {
        givenAGraphWithTwoSeriesAndRecords(3);
        whenTheGraphModelIsCreated();
        thenItWillContainThisManySegments(2);
        thenItWillHaveLength(2);
        thenItIsCollated(false);
    }

    @Test
    public void testAppend() throws DomainException, SegmentCategoryNotFoundException {
        givenAGraphWithTwoSeriesAndRecords(3);
        whenTheGraphModelIsCreated();
        whenASegmentIsAppendedWithLength(1);
        thenItWillContainThisManySegments(3);
        thenItWillHaveLength(3);
        thenItIsCollated(false);
    }

    @Test
    public void testAppendLongSegment() throws DomainException, SegmentCategoryNotFoundException {
        givenAGraphWithTwoSeriesAndRecords(3);
        whenTheGraphModelIsCreated();
        whenASegmentIsAppendedWithLength(2);
        thenItWillContainThisManySegments(3);
        thenItWillHaveLength(4);
        thenItIsCollated(false);
    }

    private void whenASegmentIsAppendedWithLength(final int length) {
        when(this.mockGraphSegment.getLength()).thenReturn(length);
        this.underTest.append(this.mockGraphSegment);
    }

    private void thenItWillHaveLength(final int i) {
        assertEquals(i, this.underTest.getLength());
    }

    private void givenAGraphWithTwoSeriesAndRecords(final int recordCount) throws DomainException {
        for (int i = 0; i < recordCount; i++) {
            this.records.add(new RecordImpl("Label" + i, Arrays.asList(1d * i, 2d * i)));
        }
    }

    private void whenTheGraphModelIsCreated() throws SegmentCategoryNotFoundException {
        when(this.mockGraphData.getRecords()).thenReturn(this.records);
        when(this.mockGraphData.getUnits()).thenReturn(Arrays.asList("", "", ""));
        this.underTest = new GraphModelImpl(this.mockGraphData);
    }

    private void thenItWillContainThisManySegments(final int i) {
        assertEquals(i, this.underTest.getSegmentCount());
    }

    private void thenItIsCollated(final boolean collated) {
        assertEquals(collated, this.underTest.isCollated());
    }

}
