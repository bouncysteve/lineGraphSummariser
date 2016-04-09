package uk.co.lgs.model.graph.service;

import java.util.ArrayList;
import java.util.List;

import uk.co.lgs.domain.graph.GraphData;
import uk.co.lgs.domain.record.Record;
import uk.co.lgs.model.point.PointImpl;
import uk.co.lgs.model.segment.exception.SegmentCategoryNotFoundException;
import uk.co.lgs.model.segment.graph.GraphSegment;
import uk.co.lgs.model.segment.graph.GraphSegmentImpl;
import uk.co.lgs.model.segment.series.SeriesSegment;
import uk.co.lgs.model.segment.series.SeriesSegmentImpl;

public class SegmentationServiceImpl implements SegmentationService {

    @Override
    public List<GraphSegment> segment(GraphData graphData) throws SegmentCategoryNotFoundException {
        List<Record> records = graphData.getRecords();
        List<GraphSegment> segments = new ArrayList<GraphSegment>();
        Record segmentStartRecord = null, segmentEndRecord;
        for (Record record : records) {
            if (null == segmentStartRecord) {
                segmentStartRecord = record;
            } else {
                segmentEndRecord = record;
                segments.add(convertRecordsToSegment(segmentStartRecord, segmentEndRecord));
                segmentStartRecord = record;
            }
        }
        return segments;
    }

    private GraphSegment convertRecordsToSegment(Record segmentStartRecord, Record segmentEndRecord)
            throws SegmentCategoryNotFoundException {
        String startTimePoint = segmentStartRecord.getPointInTime();
        Double firstSeriesStartValue = segmentStartRecord.getValues().get(0);
        Double firstSeriesEndValue = segmentEndRecord.getValues().get(0);

        Double secondSeriesStartValue = segmentStartRecord.getValues().get(1);
        Double secondSeriesEndValue = segmentEndRecord.getValues().get(1);

        String endTimePoint = segmentEndRecord.getPointInTime();

        SeriesSegment firstSeriesSegment = new SeriesSegmentImpl(new PointImpl(startTimePoint, firstSeriesStartValue),
                new PointImpl(endTimePoint, firstSeriesEndValue));
        SeriesSegment secondSeriesSegment = new SeriesSegmentImpl(new PointImpl(startTimePoint, secondSeriesStartValue),
                new PointImpl(endTimePoint, secondSeriesEndValue));

        return new GraphSegmentImpl(firstSeriesSegment, secondSeriesSegment);
    }

}
