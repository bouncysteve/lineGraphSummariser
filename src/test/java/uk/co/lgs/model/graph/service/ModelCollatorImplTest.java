package uk.co.lgs.model.graph.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.mockito.Mock;

import uk.co.lgs.model.graph.GraphModel;
import uk.co.lgs.model.graph.collator.exception.CollatorException;
import uk.co.lgs.model.segment.exception.SegmentAppendException;
import uk.co.lgs.model.segment.exception.SegmentCategoryNotFoundException;
import uk.co.lgs.model.segment.graph.GraphSegment;
import uk.co.lgs.model.segment.graph.GraphSegmentImpl;
import uk.co.lgs.model.segment.graph.category.GraphSegmentCategory;
import uk.co.lgs.model.segment.series.SeriesSegment;
import uk.co.lgs.test.AbstractTest;

public class ModelCollatorImplTest extends AbstractTest {

    private static final String DUMMY_END_TIME = "end";

    private static final String DUMMY_START_TIME = "start";

    @Mock
    private GraphModel inputGraphModel;

    private ModelCollator underTest = new ModelCollatorImpl();

    private GraphModel outputGraphModel;

    @Mock
    private SeriesSegment firstSeries;

    @Mock
    private SeriesSegment secondSeries;

    @Test
    public void testCollate() throws SegmentCategoryNotFoundException, CollatorException, SegmentAppendException {
        givenAModelContainingSegmentCategories(
                Arrays.asList(GraphSegmentCategory.NEGATIVE_NEGATIVE, GraphSegmentCategory.NEGATIVE_NEGATIVE));
        whenTheModelIsCollated();
        thenTheModelHasSegments(Arrays.asList(GraphSegmentCategory.NEGATIVE_NEGATIVE));
        andTheLengthsOfTheSegmentsAre(Arrays.asList(2));
        andTheModelSaysItIsCollated(true);
        // TODO: handle intersections
    }

    private void givenAModelContainingSegmentCategories(List<GraphSegmentCategory> categories)
            throws SegmentCategoryNotFoundException, SegmentAppendException {
        List<GraphSegment> segments = new ArrayList<GraphSegment>();

        // TODO: this is a bit hacky and only caters for collating two 1-long
        // segments
        for (GraphSegmentCategory category : categories) {
            GraphSegment segment = mock(GraphSegmentImpl.class);
            when(segment.getSegmentCategory()).thenReturn(category);
            // hack!
            when(segment.getLength()).thenReturn(2);
            when(segment.getEndTime()).thenReturn(DUMMY_END_TIME);
            when(segment.getSeriesSegment(0)).thenReturn(this.firstSeries);
            when(segment.getSeriesSegment(1)).thenReturn(this.secondSeries);
            when(segment.getStartTime()).thenReturn(DUMMY_START_TIME);
            when(segment.append(any(GraphSegment.class))).thenReturn(segment);
            segments.add(segment);
        }
        when(this.inputGraphModel.getGraphSegments()).thenReturn(segments);
        when(this.inputGraphModel.getLength()).thenReturn(categories.size());
    }

    private void whenTheModelIsCollated() throws SegmentCategoryNotFoundException, CollatorException {
        this.outputGraphModel = this.underTest.collate(this.inputGraphModel);
    }

    private void thenTheModelHasSegments(List<GraphSegmentCategory> categoryList) {
        for (GraphSegment segment : this.outputGraphModel.getGraphSegments()) {
            assertEquals(categoryList.iterator().next(), segment.getSegmentCategory());
        }
    }

    private void andTheLengthsOfTheSegmentsAre(List<Integer> expectedSegmentLengths) {
        for (GraphSegment segment : this.outputGraphModel.getGraphSegments()) {
            assertEquals(expectedSegmentLengths.iterator().next().intValue(), segment.getLength());
        }
    }

    private void andTheModelSaysItIsCollated(boolean expectedCollatedStatus) {
        assertEquals(expectedCollatedStatus, this.outputGraphModel.isCollated());
    }
}
