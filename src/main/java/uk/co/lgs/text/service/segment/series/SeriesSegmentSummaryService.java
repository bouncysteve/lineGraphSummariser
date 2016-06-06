package uk.co.lgs.text.service.segment.series;

import simplenlg.framework.PhraseElement;
import uk.co.lgs.model.segment.series.SeriesSegment;

public interface SeriesSegmentSummaryService {
    PhraseElement getSummary(SeriesSegment seriesSegment);
}
