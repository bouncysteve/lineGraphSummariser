package uk.co.lgs.model.graph.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import uk.co.lgs.domain.graph.GraphData;
import uk.co.lgs.domain.record.Record;
import uk.co.lgs.model.point.PointImpl;
import uk.co.lgs.model.segment.exception.SegmentCategoryNotFoundException;
import uk.co.lgs.model.segment.graph.GraphSegment;
import uk.co.lgs.model.segment.graph.GraphSegmentImpl;
import uk.co.lgs.model.segment.series.SeriesSegment;
import uk.co.lgs.model.segment.series.SeriesSegmentImpl;

/**
 * I am responsible for converting graph data into segments.
 *
 * @author bouncysteve
 *
 */
@Component
public class SegmentationServiceImpl implements SegmentationService {

    @Override
    public List<GraphSegment> segment(final GraphData graphData) throws SegmentCategoryNotFoundException {
        final List<Record> records = graphData.getRecords();
        final List<GraphSegment> segments = new ArrayList<>();
        final List<String> labels = graphData.getLabels();
        final List<String> descriptions = graphData.getDescriptions();
        final List<String> units = graphData.getUnits();
        Record segmentStartRecord = null;
        Record segmentEndRecord;
        for (final Record record : records) {
            if (null == segmentStartRecord) {
                segmentStartRecord = record;
            } else {
                segmentEndRecord = record;
                segments.add(
                        convertRecordsToSegment(segmentStartRecord, segmentEndRecord, labels, descriptions, units));
                segmentStartRecord = record;
            }
        }
        return segments;
    }

    private GraphSegment convertRecordsToSegment(final Record segmentStartRecord, final Record segmentEndRecord,
            final List<String> labels, final List<String> descriptions, final List<String> units)
                    throws SegmentCategoryNotFoundException {
        final String startTimePoint = segmentStartRecord.getPointInTime();
        final Double firstSeriesStartValue = segmentStartRecord.getValues().get(0);
        final Double firstSeriesEndValue = segmentEndRecord.getValues().get(0);

        final Double secondSeriesStartValue = segmentStartRecord.getValues().get(1);
        final Double secondSeriesEndValue = segmentEndRecord.getValues().get(1);

        final String endTimePoint = segmentEndRecord.getPointInTime();

        final SeriesSegment firstSeriesSegment = new SeriesSegmentImpl(
                new PointImpl(startTimePoint, firstSeriesStartValue), new PointImpl(endTimePoint, firstSeriesEndValue),
                labels.get(1), descriptions.get(1), units.get(1));
        final SeriesSegment secondSeriesSegment = new SeriesSegmentImpl(
                new PointImpl(startTimePoint, secondSeriesStartValue),
                new PointImpl(endTimePoint, secondSeriesEndValue), labels.get(2), descriptions.get(2), units.get(2));

        return new GraphSegmentImpl(firstSeriesSegment, secondSeriesSegment);
    }

}
