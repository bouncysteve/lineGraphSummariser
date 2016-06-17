package uk.co.lgs.text.service.segment.series;

import simplenlg.framework.PhraseElement;
import uk.co.lgs.model.segment.series.SeriesSegment;

/**
 * I am responsible for generating a text summary of a single series over a
 * segment.
 * 
 * @author bouncysteve
 *
 */
public interface SeriesSegmentSummaryService {
    PhraseElement getSummary(SeriesSegment seriesSegment, SeriesSegment secondSeries,
            org.apache.commons.configuration2.Configuration config);

    /**
     * Set false for testing.
     * 
     * @param randomise
     *            - enables the random selection of synonyms.
     */
    void setRandomise(boolean b);
}
