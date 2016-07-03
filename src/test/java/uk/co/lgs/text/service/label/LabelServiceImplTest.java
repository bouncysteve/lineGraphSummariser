package uk.co.lgs.text.service.label;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.realiser.english.Realiser;
import uk.co.lgs.model.graph.GraphModel;
import uk.co.lgs.model.segment.graph.GraphSegment;
import uk.co.lgs.model.segment.series.SeriesSegment;
import uk.co.lgs.test.AbstractTest;

public class LabelServiceImplTest extends AbstractTest {
    private static final Lexicon LEXICON = Lexicon.getDefaultLexicon();
    private static final Realiser REALISER = new Realiser(LEXICON);

    private static final String CASH_SAVING_NET = "Cash saving ratio on a net interest basis";

    private static final String CASH_SAVING_GROSS = "Cash saving ratio on a gross interest basis";

    private static final String NET = "Net interest basis";

    private static final String GROSS = "Gross interest basis";
    private static final String SHORT_FIRST_LABEL = "Short label";
    private static final String SALES_OF_FISH = "Sales of fish and chips";
    private static final String PRICE_OF_PYJAMAS = "Price of pyjamas in the Isle of Man";
    private static final String LIVERPOOLS_TOTAL_NUMBER_OF_MAJOR_TROPHY_WINS = "Liverpool's total number of major trophy wins";
    private static final String MANCHESTER_UNITEDS_TOTAL_NUMBER_OF_MAJOR_TROPHY_WINS = "Manchester United's total number of major trophy wins";
    private static final String LIVERPOOL = "Liverpool";
    private static final String MANCHESTER_UNITED = "Manchester United";

    @Mock
    private GraphModel graphModel;

    @Mock
    private GraphSegment graphSegment;

    @Mock
    private SeriesSegment firstSeriesSegment;

    @Mock
    private SeriesSegment secondSeriesSegment;

    @InjectMocks
    private LabelServiceImpl underTest;

    private List<GraphSegment> segments;

    private List<NPPhraseSpec> labelNouns;

    private List<String> labelStrings;

    @Before
    public void beforeEachTest() {
        this.segments = Arrays.asList(this.graphSegment);
        when(this.graphModel.getGraphSegments()).thenReturn(this.segments);
        when(this.graphSegment.getSeriesSegment(0)).thenReturn(this.firstSeriesSegment);
        when(this.graphSegment.getSeriesSegment(1)).thenReturn(this.secondSeriesSegment);
    }

    @Test
    public void testRemoveCommonPrefix() {
        givenSeriesLabels(CASH_SAVING_NET, CASH_SAVING_GROSS);
        whenIGetLabelsForInitialUse();
        thenTheLabelsArePlural(false, false);
        thenTheLabelsAre(CASH_SAVING_NET + " (" + NET + ")", CASH_SAVING_GROSS + " (" + GROSS + ")");
        whenIGetLabelsForCommonUse();
        thenTheLabelsArePlural(false, false);
        thenTheLabelsAre(NET, GROSS);
    }

    @Test
    public void testRemoveCommonPostfix() {
        givenSeriesLabels(LIVERPOOLS_TOTAL_NUMBER_OF_MAJOR_TROPHY_WINS,
                MANCHESTER_UNITEDS_TOTAL_NUMBER_OF_MAJOR_TROPHY_WINS);
        whenIGetLabelsForInitialUse();
        thenTheLabelsArePlural(false, false);
        thenTheLabelsAre(LIVERPOOLS_TOTAL_NUMBER_OF_MAJOR_TROPHY_WINS + " (" + LIVERPOOL + ")",
                MANCHESTER_UNITEDS_TOTAL_NUMBER_OF_MAJOR_TROPHY_WINS + " (" + MANCHESTER_UNITED + ")");
        whenIGetLabelsForCommonUse();
        thenTheLabelsArePlural(false, false);
        thenTheLabelsAre(LIVERPOOL, MANCHESTER_UNITED);
    }

    @Test
    public void testNotReplacingShortLabel() {
        givenSeriesLabels(SHORT_FIRST_LABEL, CASH_SAVING_GROSS);
        whenIGetLabelsForInitialUse();
        thenTheLabelsArePlural(false, false);
        thenTheLabelsAre(SHORT_FIRST_LABEL, CASH_SAVING_GROSS);
        whenIGetLabelsForCommonUse();
        thenTheLabelsArePlural(false, false);
        thenTheLabelsAre(SHORT_FIRST_LABEL, CASH_SAVING_GROSS);
    }

    @Test
    public void testPluraliseSales() {
        givenSeriesLabels(SALES_OF_FISH, PRICE_OF_PYJAMAS);
        whenIGetLabelsForInitialUse();
        thenTheLabelsArePlural(true, false);
        thenTheLabelsAre(SALES_OF_FISH, PRICE_OF_PYJAMAS);
        whenIGetLabelsForCommonUse();
        thenTheLabelsArePlural(true, false);
        thenTheLabelsAre(SALES_OF_FISH, PRICE_OF_PYJAMAS);
    }

    private void thenTheLabelsArePlural(final boolean firstSeriesPlural, final boolean secondSeriesPlural) {
        assertEquals(firstSeriesPlural, this.labelNouns.get(0).isPlural());
        assertEquals(secondSeriesPlural, this.labelNouns.get(1).isPlural());
    }

    private void whenIGetLabelsForCommonUse() {
        this.labelNouns = this.underTest.getLabelsForCommonUse(this.graphSegment);
        this.labelStrings = realiseNouns(this.labelNouns);
    }

    private void thenTheLabelsAre(final String firstSeriesLabel, final String secondSeriesLabel) {
        assertEquals(firstSeriesLabel, this.labelStrings.get(0));
        assertEquals(secondSeriesLabel, this.labelStrings.get(1));
    }

    private List<String> realiseNouns(final List<NPPhraseSpec> nouns) {
        final List<String> realisedPhrases = new ArrayList<>();
        for (final NPPhraseSpec noun : nouns) {
            realisedPhrases.add(REALISER.realise(noun).toString());
        }
        return realisedPhrases;
    }

    private void whenIGetLabelsForInitialUse() {
        this.labelNouns = this.underTest.getLabelsForInitialUse(this.graphModel);
        this.labelStrings = realiseNouns(this.labelNouns);
    }

    private void givenSeriesLabels(final String firstSeriesLabel, final String secondSeriesLabel) {
        when(this.firstSeriesSegment.getLabel()).thenReturn(firstSeriesLabel);
        when(this.secondSeriesSegment.getLabel()).thenReturn(secondSeriesLabel);
    }

}
