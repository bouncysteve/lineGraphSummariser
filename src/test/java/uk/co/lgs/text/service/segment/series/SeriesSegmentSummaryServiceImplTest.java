package uk.co.lgs.text.service.segment.series;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.realiser.english.Realiser;
import uk.co.lgs.model.point.PointImpl;
import uk.co.lgs.model.segment.series.SeriesSegment;
import uk.co.lgs.model.segment.series.SeriesSegmentImpl;

public class SeriesSegmentSummaryServiceImplTest {

    private static Lexicon lexicon = Lexicon.getDefaultLexicon();
    private static NLGFactory nlgFactory = new NLGFactory(lexicon);
    private static Realiser realiser = new Realiser(lexicon);

    private static final String PLURAL_SERIES_LABEL = "Sales of soup";
    private static final String SINGULAR_SERIES_LABEL = "Population";
    private static final String START_TIME = "2012";
    private static final String END_TIME = "2013";
    private static double LOW_VALUE = 2.5;
    private static double HIGH_VALUE = 7.0;
    private PhraseElement summary;
    private String summaryText;
    private SeriesSegmentSummaryService underTest;
    private SeriesSegment seriesSegment;

    @Before
    public void setup() {
        this.underTest = new SeriesSegmentSummaryServiceImpl();
    }

    /**
     * Sales of soup rise from 2.5 to 7.0
     * 
     */
    @Test
    public void testRisingSeriesPluralLabel() {
        givenASeriesSegment(LOW_VALUE, HIGH_VALUE, PLURAL_SERIES_LABEL);
        whenTheSeriesSegmentIsSummarised();
        thenTheSummaryIs(PLURAL_SERIES_LABEL + " rise from " + LOW_VALUE + " to " + HIGH_VALUE);
    }

    /**
     * Sales of soup fall from 7.0 to 2.5
     * 
     */
    @Test
    public void testFallingSeriesPluralLabel() {
        givenASeriesSegment(HIGH_VALUE, LOW_VALUE, PLURAL_SERIES_LABEL);
        whenTheSeriesSegmentIsSummarised();
        thenTheSummaryIs(PLURAL_SERIES_LABEL + " fall from " + HIGH_VALUE + " to " + LOW_VALUE);
    }

    /**
     * Sales of soup are constant at 7.0
     * 
     */
    @Test
    public void testConstantSeriesPluralLabel() {
        givenASeriesSegment(HIGH_VALUE, HIGH_VALUE, PLURAL_SERIES_LABEL);
        whenTheSeriesSegmentIsSummarised();
        thenTheSummaryIs(PLURAL_SERIES_LABEL + " are constant at " + HIGH_VALUE);
    }

    /**
     * Population rises from 2.5 to 7.0
     * 
     */
    @Test
    public void testRisingSeriesSinglularLabel() {
        givenASeriesSegment(LOW_VALUE, HIGH_VALUE, SINGULAR_SERIES_LABEL);
        whenTheSeriesSegmentIsSummarised();
        thenTheSummaryIs(SINGULAR_SERIES_LABEL + " rises from " + LOW_VALUE + " to " + HIGH_VALUE);
    }

    /**
     * Population falls from 7.0 to 2.5
     * 
     */
    @Test
    public void testFallingSeriesSinglularLabel() {
        givenASeriesSegment(HIGH_VALUE, LOW_VALUE, SINGULAR_SERIES_LABEL);
        whenTheSeriesSegmentIsSummarised();
        thenTheSummaryIs(SINGULAR_SERIES_LABEL + " falls from " + HIGH_VALUE + " to " + LOW_VALUE);
    }

    /**
     * Population is constant at 7.0
     * 
     */
    @Test
    public void testConstantSeriesSinglularLabel() {
        givenASeriesSegment(HIGH_VALUE, HIGH_VALUE, SINGULAR_SERIES_LABEL);
        whenTheSeriesSegmentIsSummarised();
        thenTheSummaryIs(SINGULAR_SERIES_LABEL + " is constant at " + HIGH_VALUE);
    }

    private void givenASeriesSegment(double startValue, double endValue, String seriesLabel) {
        this.seriesSegment = new SeriesSegmentImpl(new PointImpl(START_TIME, startValue),
                new PointImpl(END_TIME, endValue), seriesLabel);
    }

    private void whenTheSeriesSegmentIsSummarised() {
        this.summary = this.underTest.getSummary(this.seriesSegment);
        this.summaryText = realiser.realise(this.summary).toString();

    }

    private void thenTheSummaryIs(String string) {
        assertEquals(string, this.summaryText);
    }

}
