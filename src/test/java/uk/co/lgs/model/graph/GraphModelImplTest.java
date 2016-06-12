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
import uk.co.lgs.domain.graph.iscatter.schema.Schema;
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

    @Mock
    private Schema mockSchema;

    private List<Record> records;

    private GraphModel underTest;

    private static final String SERIES_ONE_SCHEMA_DESCRIPTION = "series1Label";
    private static final String SERIES_TWO_SCHEMA_DESCRIPTION = "series2Label";

    private static final String SERIES_ONE_LABEL = "series1";
    private static final String SERIES_TWO_LABEL = "series2";
    private static final String TIME_SERIES_LABEL = "time";

    @Before
    public void setup() {
        this.records = new ArrayList<Record>();
        when(this.mockGraphData.getHeader())
                .thenReturn(Arrays.asList(new String[] { TIME_SERIES_LABEL, SERIES_ONE_LABEL, SERIES_TWO_LABEL }));
    }

    @Test
    public void test() throws DomainException, SegmentCategoryNotFoundException {
        givenAGraphWithTwoSeriesAndRecords(3);
        whenTheGraphModelIsCreated();
        thenItWillContainThisManySegments(2);
        thenItWillHaveLength(2);
        thenTheseLabelsAreUsed(TIME_SERIES_LABEL, SERIES_ONE_LABEL, SERIES_TWO_LABEL);
        thenItIsCollated(false);
    }

    @Test
    public void testWithDescriptionsInSchema() throws DomainException, SegmentCategoryNotFoundException {
        givenAGraphWithTwoSeriesAndRecords(3);
        givenTheSchemaHasDescriptions(SERIES_ONE_SCHEMA_DESCRIPTION, SERIES_TWO_SCHEMA_DESCRIPTION);
        whenTheGraphModelIsCreated();
        thenItWillContainThisManySegments(2);
        thenItWillHaveLength(2);
        thenTheseLabelsAreUsed(TIME_SERIES_LABEL, SERIES_ONE_SCHEMA_DESCRIPTION, SERIES_TWO_SCHEMA_DESCRIPTION);
        thenItIsCollated(false);
    }

    @Test
    public void testAppend() throws DomainException, SegmentCategoryNotFoundException {
        givenAGraphWithTwoSeriesAndRecords(3);
        whenTheGraphModelIsCreated();
        whenASegmentIsAppendedWithLength(1);
        thenItWillContainThisManySegments(3);
        thenItWillHaveLength(3);
        thenTheseLabelsAreUsed(TIME_SERIES_LABEL, SERIES_ONE_LABEL, SERIES_TWO_LABEL);
        thenItIsCollated(false);
    }

    @Test
    public void testAppendLongSegment() throws DomainException, SegmentCategoryNotFoundException {
        givenAGraphWithTwoSeriesAndRecords(3);
        whenTheGraphModelIsCreated();
        whenASegmentIsAppendedWithLength(2);
        thenItWillContainThisManySegments(3);
        thenItWillHaveLength(4);
        thenTheseLabelsAreUsed(TIME_SERIES_LABEL, SERIES_ONE_LABEL, SERIES_TWO_LABEL);
        thenItIsCollated(false);
    }

    private void givenTheSchemaHasDescriptions(String seriesOneSchemaDescription, String seriesTwoSchemaDescription) {
        when(this.mockGraphData.getSchema()).thenReturn(this.mockSchema);
        when(this.mockSchema.getDescription("series1")).thenReturn(seriesOneSchemaDescription);
        when(this.mockSchema.getDescription("series2")).thenReturn(seriesTwoSchemaDescription);
    }

    private void whenASegmentIsAppendedWithLength(int length) {
        when(this.mockGraphSegment.getLength()).thenReturn(length);
        this.underTest.append(this.mockGraphSegment);
    }

    private void thenItWillHaveLength(int i) {
        assertEquals(i, this.underTest.getLength());
    }

    private void thenTheseLabelsAreUsed(String timeSeriesLabel, String seriesOneLabel, String seriesTwoLabel) {
        assertEquals(timeSeriesLabel, this.underTest.getLabels().get(0));
        assertEquals(seriesOneLabel, this.underTest.getLabels().get(1));
        assertEquals(seriesTwoLabel, this.underTest.getLabels().get(2));
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

    private void thenItIsCollated(boolean collated) {
        assertEquals(collated, this.underTest.isCollated());
    }
}
