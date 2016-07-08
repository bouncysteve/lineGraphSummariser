package uk.co.lgs.model.graph.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.co.lgs.model.segment.graph.GraphSegment;

public class GapServiceImplTest {

    private List<GraphSegment> graphSegmentsIn;

    private GapService gapService;

    @Before
    public void beforeEachTest() {
        this.gapService = new GapServiceImpl();
        this.graphSegmentsIn = new ArrayList<>();
    }

    @Test
    public void testMaximGapInFirstSegment() {
        givenASegmentWithEndValues(10, 20);
        givenASegmentWithEndValues(-5, -5);
        whenIcallAddGapInfo();
        thenTheSegmentsEndWithTheMaximumGap(Arrays.asList(true, false));
        thenTheSegmentsEndWithTheMinimumGap(Arrays.asList(false, false));
    }

    @Test
    public void testMaximGapMultipleTimes() {
        givenASegmentWithEndValues(30, 25);
        givenASegmentWithEndValues(-10, -50);
        givenASegmentWithEndValues(500, 500);
        givenASegmentWithEndValues(40, 0);
        whenIcallAddGapInfo();
        thenTheSegmentsEndWithTheMaximumGap(Arrays.asList(false, true, false, true));
        thenTheSegmentsEndWithTheMinimumGap(Arrays.asList(false, false, false, false));
    }

    @Test
    public void testMinimumGapInFirstSegment() {
        givenASegmentWithEndValues(5, 10);
        givenASegmentWithEndValues(-10, -20);
        givenASegmentWithEndValues(100, 20);
        whenIcallAddGapInfo();
        thenTheSegmentsEndWithTheMaximumGap(Arrays.asList(false, false, true));
        thenTheSegmentsEndWithTheMinimumGap(Arrays.asList(true, false, false));
    }

    @Test
    public void testMinimumGapMultipleTimes() {
        givenASegmentWithEndValues(-5, -10);
        givenASegmentWithEndValues(10, 20);
        givenASegmentWithEndValues(100, 20);
        givenASegmentWithEndValues(200, 205);
        whenIcallAddGapInfo();
        thenTheSegmentsEndWithTheMaximumGap(Arrays.asList(false, false, true, false));
        thenTheSegmentsEndWithTheMinimumGap(Arrays.asList(true, false, false, true));
    }

    @Test
    public void testConstantNonZeroGap() {
        givenASegmentWithEndValues(-50, -10);
        givenASegmentWithEndValues(20, 60);
        givenASegmentWithEndValues(30, 70);
        givenASegmentWithEndValues(40, 80);
        givenASegmentWithEndValues(50, 90);
        whenIcallAddGapInfo();
        thenTheSegmentsEndWithTheMaximumGap(Arrays.asList(true, true, true, true, true));
        thenTheSegmentsEndWithTheMinimumGap(Arrays.asList(true, true, true, true, true));
    }

    @Test
    public void testConstantZeroGap() {
        givenASegmentWithEndValues(-90, -90);
        givenASegmentWithEndValues(50, 50);
        givenASegmentWithEndValues(60, 60);
        givenASegmentWithEndValues(70, 70);
        givenASegmentWithEndValues(80, 80);
        givenASegmentWithEndValues(90, 90);
        whenIcallAddGapInfo();
        thenTheSegmentsEndWithTheMaximumGap(Arrays.asList(true, true, true, true, true, true));
        thenTheSegmentsEndWithTheMinimumGap(Arrays.asList(false, false, false, false, false, false));
    }

    private void thenTheSegmentsEndWithTheMinimumGap(final List<Boolean> segmentHasMinimum) {
        int index = 0;
        for (final GraphSegment graphSegment : this.graphSegmentsIn) {
            if (segmentHasMinimum.get(index++)) {
                verify(graphSegment).setGlobalMinimumGapAtSegmentEnd(true);
            }
        }

    }

    private void thenTheSegmentsEndWithTheMaximumGap(final List<Boolean> segmentHasMaximum) {
        int index = 0;
        for (final GraphSegment graphSegment : this.graphSegmentsIn) {
            if (segmentHasMaximum.get(index++)) {
                verify(graphSegment).setGlobalMaximumGapAtSegmentEnd(true);
            }
        }
    }

    private void givenASegmentWithEndValues(final int firstSeriesEndValue, final int secondSeriesEndValue) {
        final GraphSegment mockSegment = mock(GraphSegment.class);
        when(mockSegment.getGapBetweenSeriesEndValues())
                .thenReturn((double) Math.abs(firstSeriesEndValue - secondSeriesEndValue));
        this.graphSegmentsIn.add(mockSegment);
    }

    private void whenIcallAddGapInfo() {
        this.gapService.addGapInfo(this.graphSegmentsIn);
    }

}
