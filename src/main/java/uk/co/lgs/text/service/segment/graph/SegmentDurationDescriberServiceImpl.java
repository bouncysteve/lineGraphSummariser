package uk.co.lgs.text.service.segment.graph;

import java.util.Random;

import org.springframework.stereotype.Component;

import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import uk.co.lgs.model.segment.graph.GraphSegment;

/**
 * @author bouncysteve
 *
 */
@Component
public class SegmentDurationDescriberServiceImpl implements SegmentDurationDescriberService {

    private static final Lexicon LEXICON = Lexicon.getDefaultLexicon();
    private static final NLGFactory NLG_FACTORY = new NLGFactory(LEXICON);

    private static final Random random = new Random();

    private boolean randomise = true;

    /**
     * The number of choices, i.e. one for each method.
     * 
     */
    private static final int VARIATIONS = 4;

    @Override
    public void setRandomise(boolean randomise) {
        this.randomise = randomise;
    }

    @Override
    public SPhraseSpec buildDurationDescription(GraphSegment graphSegment) {
        NPPhraseSpec startTime = NLG_FACTORY.createNounPhrase(graphSegment.getStartTime());
        NPPhraseSpec endTime = NLG_FACTORY.createNounPhrase(graphSegment.getEndTime());

        SPhraseSpec durationClause;

        int randomNumber = 0;
        if (this.randomise) {
            randomNumber = random.nextInt(VARIATIONS);
        }

        switch (randomNumber) {
        case 1:
            durationClause = generateBetweenPhrase(startTime, endTime);
            break;
        case 2:
            durationClause = generatePeriodPhrase(startTime, endTime);
            break;
        case 3:
            durationClause = generateFromUntilPhrase(startTime, endTime);
            break;
        default:
            durationClause = generateBetweenPhrase(startTime, endTime);
        }
        return durationClause;
    }

    protected SPhraseSpec generateBetweenPhrase(NPPhraseSpec startTime, NPPhraseSpec endTime) {
        PPPhraseSpec durationPreposition = NLG_FACTORY.createPrepositionPhrase();
        durationPreposition.addComplement(startTime);
        durationPreposition.setPreposition("between");
        durationPreposition.addComplement(endTime);
        SPhraseSpec durationClause = NLG_FACTORY.createClause();
        durationClause.setObject(durationPreposition);
        return durationClause;
    }

    protected SPhraseSpec generatePeriodPhrase(NPPhraseSpec startTime, NPPhraseSpec endTime) {
        SPhraseSpec durationClause = NLG_FACTORY.createClause();
        CoordinatedPhraseElement durationPreposition = NLG_FACTORY.createCoordinatedPhrase(startTime, endTime);
        durationPreposition.addPreModifier("during the period");
        durationPreposition.setConjunction("to");
        durationClause.setObject(durationPreposition);
        return durationClause;
    }

    protected SPhraseSpec generateFromUntilPhrase(NPPhraseSpec startTime, NPPhraseSpec endTime) {
        SPhraseSpec durationClause = NLG_FACTORY.createClause();
        CoordinatedPhraseElement durationPreposition = NLG_FACTORY.createCoordinatedPhrase(startTime, endTime);
        durationPreposition.addPreModifier("from");
        durationPreposition.setConjunction("until");
        durationClause.setObject(durationPreposition);
        return durationClause;
    }

    protected SPhraseSpec generateUpToPhrase(NPPhraseSpec endTimePhrase) {
        SPhraseSpec durationClause = NLG_FACTORY.createClause();
        NPPhraseSpec durationPreposition = NLG_FACTORY.createNounPhrase(endTimePhrase);
        durationPreposition.addPreModifier("up to");
        durationClause.setObject(durationPreposition);
        return durationClause;
    }
}
