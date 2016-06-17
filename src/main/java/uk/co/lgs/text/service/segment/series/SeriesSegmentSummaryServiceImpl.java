package uk.co.lgs.text.service.segment.series;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.configuration2.Configuration;
import org.springframework.stereotype.Component;

import simplenlg.features.Feature;
import simplenlg.features.NumberAgreement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseElement;
import simplenlg.lexicon.Lexicon;
import uk.co.lgs.model.gradient.GradientType;
import uk.co.lgs.model.segment.series.SeriesSegment;
import uk.co.lgs.text.service.graph.PropertyNames;

/**
 * TODO: Incorporate the units into the values.
 * 
 * @author bouncysteve
 *
 */
@Component
public class SeriesSegmentSummaryServiceImpl implements SeriesSegmentSummaryService {

    private static final Lexicon lexicon = Lexicon.getDefaultLexicon();
    private static final NLGFactory NLG_FACTORY = new NLGFactory(lexicon);
    private static final DecimalFormat f = new DecimalFormat("0.##");
    /**
     * There is no easy way to tell if a label represents a plural term, so
     * maintaining a list here for now. This is needed for agreement between the
     * label and the rest of the phrase.
     */
    private static final List<String> COMMON_PLURAL_TERMS = Arrays.asList("sales");
    private static final List<String> PREFIX = Arrays.asList("£", "$");
    private static final List<String> FALL_SYNONYMS = Arrays.asList("fall", "decline", "drop", "shrink", "decrease",
            "lower", "reduce"); // ("weaken","diminish", "slide",)
    private static final List<String> RISE_SYNONYMS = Arrays.asList("rise", "increase", "grow", "improve", "progress",
            "extend", "build", "go up"); // ("ascend", "advance",)
    private static final List<String> CONSTANT_SYNONYMS = Arrays.asList("is constant", "remain", "continue", "freeze",
            "stop", "persist", "rest");// ("endure",
    private static final Random random = new Random();

    private boolean randomise = true;

    @Override
    public PhraseElement getSummary(SeriesSegment mainSeriesSegment, SeriesSegment minorSeriesSegment,
            Configuration config) {

        PhraseElement subject1 = pluralise(NLG_FACTORY.createNounPhrase(mainSeriesSegment.getLabel()));
        Object subject;
        if (null != config && config.getBoolean(PropertyNames.BOTH_SAME, false)) {
            subject = NLG_FACTORY.createCoordinatedPhrase(subject1,
                    pluralise(NLG_FACTORY.createNounPhrase(minorSeriesSegment.getLabel())));
        } else {
            subject = subject1;
        }
        PhraseElement gradient = NLG_FACTORY.createClause(subject,
                gradientTypeDescription(mainSeriesSegment.getGradientType()), null);
        PhraseElement behaviour;
        if (mainSeriesSegment.getGradientType().equals(GradientType.ZERO)) {
            behaviour = NLG_FACTORY.createPrepositionPhrase("at",
                    formatValueWithUnits(mainSeriesSegment.getStartValue(), mainSeriesSegment.getUnits()));
        } else {
            behaviour = NLG_FACTORY.createPrepositionPhrase("from",
                    formatValueWithUnits(mainSeriesSegment.getStartValue(), mainSeriesSegment.getUnits()));
            behaviour.addPostModifier(NLG_FACTORY.createPrepositionPhrase("to",
                    formatValueWithUnits(mainSeriesSegment.getEndValue(), mainSeriesSegment.getUnits())));
        }
        gradient.addPostModifier(behaviour);
        return gradient;
    }

    private String formatValueWithUnits(double value, String units) {
        String valueString = f.format(value);
        if (isPrefixable(units)) {
            valueString = units + valueString;
        } else {
            valueString = valueString + (needsLeadingSpace(units) ? " " : "") + units;
        }
        return valueString;
    }

    private boolean needsLeadingSpace(String units) {
        if ("%".equals(units)) {
            return false;
        }
        return true;
    }

    private boolean isPrefixable(String units) {
        for (String prefix : PREFIX) {
            if (prefix.equals(units)) {
                return true;
            }
        }
        return false;
    }

    private PhraseElement pluralise(PhraseElement subject) {
        for (String term : COMMON_PLURAL_TERMS) {
            if (subject.getHead().toString().toLowerCase().contains(term)) {
                subject.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
                break;
            }
        }
        return subject;
    }

    private PhraseElement gradientTypeDescription(GradientType gradientType) {
        String description;
        switch (gradientType) {
        case NEGATIVE:
            description = getSynonym(FALL_SYNONYMS);
            break;
        case POSITIVE:
            description = getSynonym(RISE_SYNONYMS);
            break;
        case ZERO:
        default:
            description = getSynonym(CONSTANT_SYNONYMS);
            break;
        }
        return NLG_FACTORY.createVerbPhrase(description);
    }

    private String getSynonym(List<String> synonyms) {
        int randomNumber = 0;
        if (this.randomise) {
            randomNumber = random.nextInt(synonyms.size() - 0);
        }
        return synonyms.get(randomNumber);
    }

    @Override
    public void setRandomise(boolean randomise) {
        this.randomise = randomise;
    }
}
