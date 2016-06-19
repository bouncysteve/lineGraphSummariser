package uk.co.lgs.model.graph.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.mockito.Mock;

import uk.co.lgs.model.graph.GraphModel;
import uk.co.lgs.model.graph.collator.exception.CollatorException;
import uk.co.lgs.model.segment.exception.SegmentAppendException;
import uk.co.lgs.model.segment.exception.SegmentCategoryNotFoundException;
import uk.co.lgs.model.segment.graph.GraphSegment;
import uk.co.lgs.model.segment.graph.GraphSegmentImpl;
import uk.co.lgs.model.segment.graph.category.GapTrend;
import uk.co.lgs.model.segment.graph.category.GraphSegmentGradient;
import uk.co.lgs.model.segment.series.SeriesSegment;
import uk.co.lgs.test.AbstractTest;

public abstract class AbstractModelCollatorTest extends AbstractTest {

    static final String DUMMY_END_TIME = "end";
    static final String DUMMY_START_TIME = "start";
    static final String COLLATED_END_TIME = "endLater";

    @Mock
    GraphModel inputGraphModel;
    GraphModel outputGraphModel;
    @Mock
    SeriesSegment firstSeries;
    @Mock
    SeriesSegment secondSeries;

    protected ModelCollator underTest;

    public AbstractModelCollatorTest() {
        super();
    }

    protected void givenAModelContainingSegmentCategoriesAndGapTrends(List<GraphSegmentGradient> categories,
            List<GapTrend> gapTrendList) throws SegmentCategoryNotFoundException, SegmentAppendException {
        List<GraphSegment> segments = new ArrayList<GraphSegment>();
        int index = 0;
        GraphSegment collatedSegment = mock(GraphSegmentImpl.class);
        when(collatedSegment.getLength()).thenReturn(2);
        when(collatedSegment.getGraphSegmentGradientCategory()).thenReturn(categories.get(0));
        when(collatedSegment.getStartTime()).thenReturn(DUMMY_START_TIME);
        when(collatedSegment.getEndTime()).thenReturn(COLLATED_END_TIME);
        when(collatedSegment.getGraphSegmentTrend()).thenReturn(gapTrendList.get(0));
        for (GraphSegmentGradient category : categories) {
            GraphSegment segment = mock(GraphSegmentImpl.class);
            when(segment.getGraphSegmentGradientCategory()).thenReturn(category);
            when(segment.isIntersecting()).thenReturn(category.isIntersecting());
            when(segment.getLength()).thenReturn(1);
            when(segment.getEndTime()).thenReturn(DUMMY_END_TIME);
            when(segment.getSeriesSegment(0)).thenReturn(this.firstSeries);
            when(segment.getSeriesSegment(1)).thenReturn(this.secondSeries);
            when(segment.getStartTime()).thenReturn(DUMMY_START_TIME);

            when(segment.append(any(GraphSegment.class))).thenReturn(collatedSegment);
            if (null != gapTrendList) {
                when(segment.getGraphSegmentTrend()).thenReturn(gapTrendList.get(index++));
            }
            segments.add(segment);
        }

        when(this.inputGraphModel.getGraphSegments()).thenReturn(segments);
        when(this.inputGraphModel.getLength()).thenReturn(categories.size());
    }

    protected void whenTheModelIsCollated() throws SegmentCategoryNotFoundException, CollatorException {
        this.outputGraphModel = this.underTest.collate(this.inputGraphModel);
    }

    protected void thenTheModelHasSegments(List<GraphSegmentGradient> categoryList) {
        List<GraphSegment> segments = this.outputGraphModel.getGraphSegments();
        for (int i = 0; i < categoryList.size(); i++) {
            assertEquals(categoryList.get(i), segments.get(i).getGraphSegmentGradientCategory());
        }
    }

    protected void thenTheModelSaysItIsCollated(boolean expectedCollatedStatus) {
        assertEquals(expectedCollatedStatus, this.outputGraphModel.isCollated());
    }

    protected void thenTheModelHasLengths(List<Integer> lengthList) {
        List<GraphSegment> segments = this.outputGraphModel.getGraphSegments();
        for (int i = 0; i < lengthList.size(); i++) {
            assertEquals(lengthList.get(i), (Integer) segments.get(i).getLength());
        }

    }

    protected void thenTheModelHasGaps(List<GapTrend> gapTrends) {
        List<GraphSegment> segments = this.outputGraphModel.getGraphSegments();
        for (int i = 0; i < gapTrends.size(); i++) {
            assertEquals(gapTrends.get(i), segments.get(i).getGraphSegmentTrend());
        }
    }

}