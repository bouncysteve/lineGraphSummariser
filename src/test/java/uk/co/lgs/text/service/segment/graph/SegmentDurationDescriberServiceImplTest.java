package uk.co.lgs.text.service.segment.graph;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.mockito.Mock;

import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;
import uk.co.lgs.model.segment.graph.GraphSegment;
import uk.co.lgs.test.AbstractTest;

public class SegmentDurationDescriberServiceImplTest extends AbstractTest {

    private static final Lexicon lexicon = Lexicon.getDefaultLexicon();
    private static final NLGFactory NLG_FACTORY = new NLGFactory(lexicon);
    private static final Realiser realiser = new Realiser(lexicon);

    private static final String START_TIME = "ages ago";
    private static final NPPhraseSpec START_TIME_PHRASE = NLG_FACTORY.createNounPhrase(START_TIME);

    private static final String END_TIME = "just now";
    private static final NPPhraseSpec END_TIME_PHRASE = NLG_FACTORY.createNounPhrase(END_TIME);

    @Mock
    GraphSegment graphSegment;

    private SegmentDurationDescriberServiceImpl underTest = new SegmentDurationDescriberServiceImpl();

    private SPhraseSpec summary;
    private String summaryText;

    @Test
    public void testGenerateBetweenPhrase() {
        this.summary = this.underTest.generateBetweenPhrase(START_TIME_PHRASE, END_TIME_PHRASE);
        this.summaryText = realiser.realise(this.summary).toString();
        assertEquals("between " + START_TIME + " and " + END_TIME, this.summaryText);
    }

    @Test
    public void testGeneratePeriodPhrase() {
        this.summary = this.underTest.generatePeriodPhrase(START_TIME_PHRASE, END_TIME_PHRASE);
        this.summaryText = realiser.realise(this.summary).toString();
        assertEquals("during the period " + START_TIME + " to " + END_TIME, this.summaryText);
    }

    @Test
    public void testFromUntilPhrase() {
        this.summary = this.underTest.generateFromUntilPhrase(START_TIME_PHRASE, END_TIME_PHRASE);
        this.summaryText = realiser.realise(this.summary).toString();
        assertEquals("from " + START_TIME + " until " + END_TIME, this.summaryText);
    }

}
