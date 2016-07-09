package uk.co.lgs.model.graph;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import uk.co.lgs.domain.exception.DomainException;
import uk.co.lgs.domain.graph.GraphData;
import uk.co.lgs.model.graph.service.GapService;
import uk.co.lgs.model.graph.service.SegmentationService;
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

    private static final String TITLE = "A Lovely Graph";

    private static final List<String> UNITS = Arrays.asList("", "$", "cm");

    private static final List<String> LABELS = Arrays.asList("time", "series1", "series2");

    @Mock
    private GraphData mockGraphData;

    @Mock
    private GraphSegment mockGraphSegment;

    @Mock
    private GapService gapService;

    @Mock
    private SegmentationService segmentationService;

    @InjectMocks
    private GraphModelImpl underTest;

    @Mock
    private GraphSegment mockAppendedGraphSegment;

    @Before
    public void beforeEachTest() throws SegmentCategoryNotFoundException {
        when(this.mockGraphSegment.getLength()).thenReturn(1);
        when(this.mockGraphData.getHeader()).thenReturn(LABELS);
        when(this.mockGraphData.getTitle()).thenReturn(TITLE);
        when(this.mockGraphData.getUnits()).thenReturn(UNITS);
    }

    @Test
    public void test() throws DomainException, SegmentCategoryNotFoundException {
        givenAGraphWithThisManySegments(2);
        whenTheGraphModelIsCreated();
        thenItWillContainThisManySegments(2);
        thenItWillHaveLength(2);
        thenItIsCollated(false);
        thenTheMetaDataIsAvailable();
    }

    private void givenAGraphWithThisManySegments(final int count) throws SegmentCategoryNotFoundException {
        final List<GraphSegment> graphSegments = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            graphSegments.add(this.mockGraphSegment);
        }
        when(this.segmentationService.segment(this.mockGraphData)).thenReturn(graphSegments);
        when(this.gapService.addGapInfo(graphSegments)).thenReturn(graphSegments);

    }

    @Test
    public void testAppend() throws DomainException, SegmentCategoryNotFoundException {
        givenAGraphWithThisManySegments(2);
        whenTheGraphModelIsCreated();
        whenASegmentIsAppendedWithLength(1);
        thenItWillContainThisManySegments(3);
        thenItWillHaveLength(3);
        thenItIsCollated(false);
        thenTheMetaDataIsAvailable();
    }

    @Test
    public void testAppendLongSegment() throws DomainException, SegmentCategoryNotFoundException {
        givenAGraphWithThisManySegments(2);
        whenTheGraphModelIsCreated();
        whenASegmentIsAppendedWithLength(2);
        thenItWillContainThisManySegments(3);
        thenItWillHaveLength(4);
        thenItIsCollated(false);
        thenTheMetaDataIsAvailable();
    }

    private void whenASegmentIsAppendedWithLength(final int length) {
        when(this.mockAppendedGraphSegment.getLength()).thenReturn(length);
        this.underTest.append(this.mockAppendedGraphSegment);
    }

    private void thenItWillHaveLength(final int i) {
        assertEquals(i, this.underTest.getLength());
    }

    private void whenTheGraphModelIsCreated() throws SegmentCategoryNotFoundException {
        this.underTest.setGraphData(this.mockGraphData);
    }

    private void thenItWillContainThisManySegments(final int i) {
        assertEquals(i, this.underTest.getSegmentCount());
    }

    private void thenItIsCollated(final boolean collated) {
        assertEquals(collated, this.underTest.isCollated());
    }

    private void thenTheMetaDataIsAvailable() {
        assertEquals(LABELS, this.underTest.getLabels());
        assertEquals(TITLE, this.underTest.getTitle());
        assertEquals(UNITS, this.underTest.getUnits());
    }

}
