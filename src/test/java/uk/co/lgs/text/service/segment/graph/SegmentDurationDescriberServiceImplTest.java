package uk.co.lgs.text.service.segment.graph;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
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
    private static final Realiser realiser = new Realiser(lexicon);

    private static final String START_TIME = "ages ago";
    private static final String END_TIME = "just now";

    private NLGFactory nlgFactory;

    private NPPhraseSpec startTimePhrase;

    private NPPhraseSpec endTimePhrase;

    @Mock
    GraphSegment graphSegment;

    private SegmentDurationDescriberServiceImpl underTest = new SegmentDurationDescriberServiceImpl();

    private SPhraseSpec summary;
    private String summaryText;

    @Before
    public void beforeEachTest() {
        this.nlgFactory = new NLGFactory(lexicon);
        this.startTimePhrase = this.nlgFactory.createNounPhrase(START_TIME);
        this.endTimePhrase = this.nlgFactory.createNounPhrase(END_TIME);
    }

    @Test
    public void testGenerateBetweenPhrase() {
        this.summary = this.underTest.generateBetweenPhrase(this.startTimePhrase, this.endTimePhrase);
        this.summaryText = realiser.realise(this.summary).toString();
        assertEquals("between " + START_TIME + " and " + END_TIME, this.summaryText);
    }

    @Test
    public void testGeneratePeriodPhrase() {
        this.summary = this.underTest.generatePeriodPhrase(this.startTimePhrase, this.endTimePhrase);
        this.summaryText = realiser.realise(this.summary).toString();
        assertEquals("during the period " + START_TIME + " to " + END_TIME, this.summaryText);
    }

    @Test
    public void testFromUntilPhrase() {
        this.summary = this.underTest.generateFromUntilPhrase(this.startTimePhrase, this.endTimePhrase);
        this.summaryText = realiser.realise(this.summary).toString();
        assertEquals("from " + START_TIME + " until " + END_TIME, this.summaryText);
    }

    @Test
    public void testUptoPhrase() {
        this.summary = this.underTest.generateUpToPhrase(this.endTimePhrase);
        this.summaryText = realiser.realise(this.summary).toString();
        assertEquals("up to " + END_TIME, this.summaryText);
    }

}
