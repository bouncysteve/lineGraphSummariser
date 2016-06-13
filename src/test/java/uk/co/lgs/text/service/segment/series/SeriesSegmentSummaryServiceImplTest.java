package uk.co.lgs.text.service.segment.series;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import simplenlg.framework.PhraseElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.realiser.english.Realiser;
import uk.co.lgs.model.point.PointImpl;
import uk.co.lgs.model.segment.series.SeriesSegment;
import uk.co.lgs.model.segment.series.SeriesSegmentImpl;

public class SeriesSegmentSummaryServiceImplTest {

    private static Lexicon lexicon = Lexicon.getDefaultLexicon();
    private static Realiser realiser = new Realiser(lexicon);

    private static final String PLURAL_SERIES_LABEL_UPPER = "Sales of soup";
    private static final String PLURAL_SERIES_LABEL_LOWER = "sales of soup";
    private static final String SINGULAR_SERIES_LABEL = "Population";
    private static final String START_TIME = "2012";
    private static final String END_TIME = "2013";
    private static double LOW_VALUE = 2.5;
    private static double HIGH_VALUE = 7.0;
    private static String HIGH_VALUE_FORMAT = "7";
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
    public void testRisingSeriesPluralLabelUpper() {
        givenASeriesSegment(LOW_VALUE, HIGH_VALUE, PLURAL_SERIES_LABEL_UPPER);
        whenTheSeriesSegmentIsSummarised();
        thenTheSummaryIs(PLURAL_SERIES_LABEL_UPPER + " rise from " + LOW_VALUE + " to " + HIGH_VALUE_FORMAT);
    }

    /**
     * Sales of soup fall from 7.0 to 2.5
     * 
     */
    @Test
    public void testFallingSeriesPluralLabelUpper() {
        givenASeriesSegment(HIGH_VALUE, LOW_VALUE, PLURAL_SERIES_LABEL_UPPER);
        whenTheSeriesSegmentIsSummarised();
        thenTheSummaryIs(PLURAL_SERIES_LABEL_UPPER + " fall from " + HIGH_VALUE_FORMAT + " to " + LOW_VALUE);
    }

    /**
     * Sales of soup are constant at 7.0
     * 
     */
    @Test
    public void testConstantSeriesPluralLabelUpper() {
        givenASeriesSegment(HIGH_VALUE, HIGH_VALUE, PLURAL_SERIES_LABEL_UPPER);
        whenTheSeriesSegmentIsSummarised();
        thenTheSummaryIs(PLURAL_SERIES_LABEL_UPPER + " are constant at " + HIGH_VALUE_FORMAT);
    }

    /**
     * sales of soup rise from 2.5 to 7.0
     * 
     */
    @Test
    public void testRisingSeriesPluralLabelLower() {
        givenASeriesSegment(LOW_VALUE, HIGH_VALUE, PLURAL_SERIES_LABEL_LOWER);
        whenTheSeriesSegmentIsSummarised();
        thenTheSummaryIs(PLURAL_SERIES_LABEL_LOWER + " rise from " + LOW_VALUE + " to " + HIGH_VALUE_FORMAT);
    }

    /**
     * sales of soup fall from 7.0 to 2.5
     * 
     */
    @Test
    public void testFallingSeriesPluralLabelLower() {
        givenASeriesSegment(HIGH_VALUE, LOW_VALUE, PLURAL_SERIES_LABEL_LOWER);
        whenTheSeriesSegmentIsSummarised();
        thenTheSummaryIs(PLURAL_SERIES_LABEL_LOWER + " fall from " + HIGH_VALUE_FORMAT + " to " + LOW_VALUE);
    }

    /**
     * sales of soup are constant at 7.0
     * 
     */
    @Test
    public void testConstantSeriesPluralLabelLower() {
        givenASeriesSegment(HIGH_VALUE, HIGH_VALUE, PLURAL_SERIES_LABEL_LOWER);
        whenTheSeriesSegmentIsSummarised();
        thenTheSummaryIs(PLURAL_SERIES_LABEL_LOWER + " are constant at " + HIGH_VALUE_FORMAT);
    }

    /**
     * Population rises from 2.5 to 7.0
     * 
     */
    @Test
    public void testRisingSeriesSinglularLabel() {
        givenASeriesSegment(LOW_VALUE, HIGH_VALUE, SINGULAR_SERIES_LABEL);
        whenTheSeriesSegmentIsSummarised();
        thenTheSummaryIs(SINGULAR_SERIES_LABEL + " rises from " + LOW_VALUE + " to " + HIGH_VALUE_FORMAT);
    }

    /**
     * Population falls from 7.0 to 2.5
     * 
     */
    @Test
    public void testFallingSeriesSinglularLabel() {
        givenASeriesSegment(HIGH_VALUE, LOW_VALUE, SINGULAR_SERIES_LABEL);
        whenTheSeriesSegmentIsSummarised();
        thenTheSummaryIs(SINGULAR_SERIES_LABEL + " falls from " + HIGH_VALUE_FORMAT + " to " + LOW_VALUE);
    }

    /**
     * Population is constant at 7.0
     * 
     */
    @Test
    public void testConstantSeriesSinglularLabel() {
        givenASeriesSegment(HIGH_VALUE, HIGH_VALUE, SINGULAR_SERIES_LABEL);
        whenTheSeriesSegmentIsSummarised();
        thenTheSummaryIs(SINGULAR_SERIES_LABEL + " is constant at " + HIGH_VALUE_FORMAT);
    }

    private void givenASeriesSegment(double startValue, double endValue, String seriesLabel) {
        this.seriesSegment = new SeriesSegmentImpl(new PointImpl(START_TIME, startValue),
                new PointImpl(END_TIME, endValue), seriesLabel, "");
    }

    private void whenTheSeriesSegmentIsSummarised() {
        this.summary = this.underTest.getSummary(this.seriesSegment, null, null);
        this.summaryText = realiser.realise(this.summary).toString();

    }

    private void thenTheSummaryIs(String string) {
        assertEquals(string, this.summaryText);
    }

}
