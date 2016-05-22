package uk.co.lgs.text.service.segment.series;

import simplenlg.framework.DocumentElement;
import uk.co.lgs.model.segment.series.SeriesSegment;

public interface SeriesSegmentSummaryService {
    DocumentElement getSummary(SeriesSegment seriesSegment);
}
