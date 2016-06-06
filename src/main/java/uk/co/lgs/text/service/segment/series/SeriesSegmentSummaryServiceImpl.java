package uk.co.lgs.text.service.segment.series;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import simplenlg.features.Feature;
import simplenlg.features.NumberAgreement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseElement;
import simplenlg.lexicon.Lexicon;
import uk.co.lgs.model.gradient.GradientType;
import uk.co.lgs.model.segment.series.SeriesSegment;

@Component
public class SeriesSegmentSummaryServiceImpl implements SeriesSegmentSummaryService {

    private static final Lexicon lexicon = Lexicon.getDefaultLexicon();
    private static final NLGFactory NLG_FACTORY = new NLGFactory(lexicon);

    /**
     * There is no easy way to tell if a label represents a plural term, so
     * maintaining a list here for now. This is needed for agreement between the
     * label and the rest of the phrase.
     */
    private static final List<String> COMMON_PLURAL_TERMS = Arrays.asList("Sales of");

    @Override
    public PhraseElement getSummary(SeriesSegment seriesSegment) {
        PhraseElement subject = pluralise(NLG_FACTORY.createNounPhrase(seriesSegment.getLabel()));

        PhraseElement gradient = NLG_FACTORY.createClause(subject,
                gradientTypeDescription(seriesSegment.getGradientType()), null);
        PhraseElement behaviour = null;
        if (seriesSegment.getGradientType().equals(GradientType.ZERO)) {
            behaviour = NLG_FACTORY.createPrepositionPhrase("at", seriesSegment.getStartValue() + "");
        } else {
            behaviour = NLG_FACTORY.createPrepositionPhrase("from", seriesSegment.getStartValue() + "");
            behaviour.addPostModifier(NLG_FACTORY.createPrepositionPhrase("to", seriesSegment.getEndValue() + ""));

        }
        gradient.addPostModifier(behaviour);
        return gradient;
    }

    private PhraseElement pluralise(PhraseElement subject) {
        for (String term : COMMON_PLURAL_TERMS) {
            if (subject.toString().contains(term)) {
                subject.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
                break;
            }
        }
        return subject;
    }

    private PhraseElement gradientTypeDescription(GradientType gradientType) {
        String description = "";
        switch (gradientType) {
        case NEGATIVE:
            description = "fall";
            break;
        case POSITIVE:
            description = "rise";
            break;
        case ZERO:
        default:
            description = "is constant";
            break;
        }
        return NLG_FACTORY.createVerbPhrase(description);
    }

}
