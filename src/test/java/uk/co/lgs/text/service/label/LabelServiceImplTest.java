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
    private static final String MEN = "Men";
    private static final String WOMEN = "Women";
    private static final String MAN = "Man";
    private static final String WOMAN = "Woman";

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
    public void testUseDescriptionAndLabelsAppropriately() {
        givenSeriesDescriptions(CASH_SAVING_NET, CASH_SAVING_GROSS);
        givenSeriesLabels(NET, GROSS);
        whenIGetLabelsForInitialUse();
        thenTheLabelsArePlural(false, false);
        thenTheLabelsAre(CASH_SAVING_NET + " (" + NET + ")", CASH_SAVING_GROSS + " (" + GROSS + ")");
        whenIGetLabelsForCommonUse();
        thenTheLabelsArePlural(false, false);
        thenTheLabelsAre(NET, GROSS);
    }

    @Test
    public void testNotReplacingShortLabel() {
        givenSeriesDescriptions("", "");
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
        givenSeriesDescriptions("", "");
        givenSeriesLabels(SALES_OF_FISH, PRICE_OF_PYJAMAS);
        whenIGetLabelsForInitialUse();
        thenTheLabelsArePlural(true, false);
        thenTheLabelsAre(SALES_OF_FISH, PRICE_OF_PYJAMAS);
        whenIGetLabelsForCommonUse();
        thenTheLabelsArePlural(true, false);
        thenTheLabelsAre(SALES_OF_FISH, PRICE_OF_PYJAMAS);
    }

    @Test
    public void testPluraliseManAndWoman() {
        givenSeriesDescriptions("", "");
        givenSeriesLabels(MAN, WOMAN);
        whenIGetLabelsForInitialUse();
        thenTheLabelsArePlural(false, false);
        thenTheLabelsAre(MAN, WOMAN);
        whenIGetLabelsForCommonUse();
        thenTheLabelsArePlural(false, false);
        thenTheLabelsAre(MAN, WOMAN);
    }

    @Test
    public void testPluraliseMenAndWomen() {
        givenSeriesDescriptions("", "");
        givenSeriesLabels(MEN, WOMEN);
        whenIGetLabelsForInitialUse();
        thenTheLabelsArePlural(true, true);
        thenTheLabelsAre(MEN, WOMEN);
        whenIGetLabelsForCommonUse();
        thenTheLabelsArePlural(true, true);
        thenTheLabelsAre(MEN, WOMEN);
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

    private void givenSeriesDescriptions(final String firstSeriesDescription, final String secondSeriesDescription) {
        when(this.firstSeriesSegment.getDescription()).thenReturn(firstSeriesDescription);
        when(this.secondSeriesSegment.getDescription()).thenReturn(secondSeriesDescription);
    }

    private void givenSeriesLabels(final String firstSeriesLabel, final String secondSeriesLabel) {
        when(this.firstSeriesSegment.getLabel()).thenReturn(firstSeriesLabel);
        when(this.secondSeriesSegment.getLabel()).thenReturn(secondSeriesLabel);
    }

}
