package uk.co.lgs.text.service.segment.series;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import simplenlg.framework.DocumentElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.realiser.english.Realiser;
import uk.co.lgs.model.point.PointImpl;
import uk.co.lgs.model.segment.series.SeriesSegment;
import uk.co.lgs.model.segment.series.SeriesSegmentImpl;

public class SeriesSegmentSummaryServiceImplTest {

    private static Lexicon lexicon = Lexicon.getDefaultLexicon();
    private static NLGFactory nlgFactory = new NLGFactory(lexicon);
    private static Realiser realiser = new Realiser(lexicon);

    private static final String SERIES_LABEL = "Sales of soap";
    private static final String START_TIME = "2012";
    private static final String END_TIME = "2013";
    private static double LOW_VALUE = 2.5;
    private static double HIGH_VALUE = 7.0;
    private DocumentElement summary;
    private SeriesSegmentSummaryService underTest;
    private SeriesSegment seriesSegment;

    @Before
    public void setup() {
        this.underTest = new SeriesSegmentSummaryServiceImpl();
    }

    @Test
    public void testRisingSeries() {
        givenASeriesSegment(LOW_VALUE, HIGH_VALUE, SERIES_LABEL);
        whenTheSeriesSegmentIsSummarised();
        thenTheSummaryIs(SERIES_LABEL + " rise from " + LOW_VALUE + " to " + HIGH_VALUE + ".");
    }

    @Test
    public void testFallingSeries() {
        givenASeriesSegment(HIGH_VALUE, LOW_VALUE, SERIES_LABEL);
        whenTheSeriesSegmentIsSummarised();
        thenTheSummaryIs(SERIES_LABEL + " fall from " + HIGH_VALUE + " to " + LOW_VALUE + ".");
    }

    @Test
    public void testConstantSeries() {
        givenASeriesSegment(HIGH_VALUE, HIGH_VALUE, SERIES_LABEL);
        whenTheSeriesSegmentIsSummarised();
        thenTheSummaryIs(SERIES_LABEL + " are constant at " + HIGH_VALUE + ".");
    }

    private void givenASeriesSegment(double startValue, double endValue, String seriesLabel) {
        this.seriesSegment = new SeriesSegmentImpl(new PointImpl(START_TIME, startValue),
                new PointImpl(END_TIME, endValue), seriesLabel);
    }

    private void whenTheSeriesSegmentIsSummarised() {
        this.summary = this.underTest.getSummary(this.seriesSegment);

    }

    private void thenTheSummaryIs(String string) {
        assertEquals(string, realiser.realise(this.summary).toString());
    }

}
