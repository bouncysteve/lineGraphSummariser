package uk.co.lgs.text.service.segment.series;

import org.springframework.stereotype.Component;

import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.framework.DocumentElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.english.Realiser;
import uk.co.lgs.model.gradient.GradientType;
import uk.co.lgs.model.segment.series.SeriesSegment;

@Component
public class SeriesSegmentSummaryServiceImpl implements SeriesSegmentSummaryService {

    private static Lexicon lexicon = Lexicon.getDefaultLexicon();
    private static NLGFactory nlgFactory = new NLGFactory(lexicon);
    private static Realiser realiser = new Realiser(lexicon);

    @Override
    public DocumentElement getSummary(SeriesSegment seriesSegment) {

        DocumentElement valueChange = nlgFactory.createSentence();
        NPPhraseSpec series = nlgFactory.createNounPhrase(seriesSegment.getLabel());
        VPPhraseSpec verb = nlgFactory.createVerbPhrase(gradientTypeDescription(seriesSegment.getGradientType()));
        verb.setFeature(Feature.FORM, Form.NORMAL);
        verb.setPlural(true);
        PPPhraseSpec prep_1 = nlgFactory.createPrepositionPhrase();
        NPPhraseSpec startValue = nlgFactory.createNounPhrase();
        startValue.setNoun(seriesSegment.getStartValue() + "");
        prep_1.addComplement(startValue);
        if (seriesSegment.getGradientType().equals(GradientType.ZERO)) {
            prep_1.setPreposition("at");
        } else {
            prep_1.setPreposition("from");
        }
        SPhraseSpec clause_1 = nlgFactory.createClause();
        clause_1.setSubject(series);
        clause_1.setVerbPhrase(verb);
        clause_1.setObject(prep_1);
        valueChange.addComponent(clause_1);
        if (!seriesSegment.getGradientType().equals(GradientType.ZERO)) {
            PPPhraseSpec prep_2 = nlgFactory.createPrepositionPhrase();
            NPPhraseSpec object_2 = nlgFactory.createNounPhrase();
            object_2.setNoun(seriesSegment.getEndValue() + "");
            prep_2.addComplement(object_2);
            prep_2.setPreposition("to");
            SPhraseSpec clause_2 = nlgFactory.createClause();
            clause_2.setObject(prep_2);
            valueChange.addComponent(clause_2);
        }

        return nlgFactory.createSentence(valueChange);
    }

    private String gradientTypeDescription(GradientType gradientType) {
        String description = "";
        switch (gradientType) {
        case NEGATIVE:
            description = "fall";
            break;
        case POSITIVE:
            description = "rise";
            break;
        default:
            description = "are constant";
            break;
        }
        return description;
    }

}
