package uk.co.lgs.text.service.label;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import uk.co.lgs.model.graph.GraphModel;
import uk.co.lgs.model.segment.graph.GraphSegment;
import uk.co.lgs.model.segment.series.SeriesSegment;

/**
 * I am responsible for converting the series labels as received from the
 * original data into a readable form.
 *
 * @author bouncysteve
 *
 */
@Component
public class LabelServiceImpl implements LabelService {
    private static final Lexicon LEXICON = Lexicon.getDefaultLexicon();
    /**
     * There is no easy way to tell if a label represents a plural term, so
     * maintaining a list here for now. This is needed for agreement between the
     * label and the rest of the phrase. TODO: this needs revisiting as it does
     * not handle "men", etc.
     */
    private static final List<String> COMMON_PLURAL_TERMS = Arrays.asList("sales", "men", "women");

    private final NLGFactory nlgFactory = new NLGFactory(LEXICON);

    @Override
    public List<NPPhraseSpec> getLabelsForInitialUse(final GraphModel graphModel) {
        return getLabelsForInitialUse(graphModel.getGraphSegments().get(0));
    }

    @Override
    public List<NPPhraseSpec> getLabelsForInitialUse(final GraphSegment graphSegment) {
        final List<String> labels = Arrays.asList(null, graphSegment.getSeriesSegment(0).getLabel(),
                graphSegment.getSeriesSegment(1).getLabel());
        final List<String> descriptions = Arrays.asList(null, graphSegment.getSeriesSegment(0).getDescription(),
                graphSegment.getSeriesSegment(1).getDescription());
        return getLabelsForInitialUse(descriptions, labels);
    }

    @Override
    public List<NPPhraseSpec> getLabelsForInitialUse(final List<String> descriptions, final List<String> labels) {
        final List<String> descriptionsWithoutTimeSeries = descriptions.subList(1, descriptions.size());
        final List<String> shortLabels = labels.subList(1, labels.size());
        final List<String> newLabels = new ArrayList<>();
        for (int index = 0; index < descriptionsWithoutTimeSeries.size(); index++) {
            String description = descriptionsWithoutTimeSeries.get(index);
            final String shortLabel = shortLabels.get(index);
            if (StringUtils.isEmpty(description)) {
                description = shortLabel;
            }
            if (description.equals(shortLabel)) {
                newLabels.add(description);
            } else {
                newLabels.add(description + " (" + shortLabel + ")");
            }
        }

        final NPPhraseSpec firstSeriesLabel = pluralise(newLabels.get(0));
        final NPPhraseSpec secondSeriesLabel = pluralise(newLabels.get(1));
        return Arrays.asList(firstSeriesLabel, secondSeriesLabel);
    }

    @Override
    public List<NPPhraseSpec> getLabelsForCommonUse(final GraphSegment graphSegment) {
        final List<String> shortLabels = Arrays.asList(graphSegment.getSeriesSegment(0).getLabel(),
                graphSegment.getSeriesSegment(1).getLabel());
        final NPPhraseSpec firstSeriesShortLabel = pluralise(shortLabels.get(0));
        final NPPhraseSpec secondSeriesShortLabel = pluralise(shortLabels.get(1));
        return Arrays.asList(firstSeriesShortLabel, secondSeriesShortLabel);
    }

    @Override
    public List<NPPhraseSpec> getLabelsForCommonUse(final GraphModel graphModel) {
        // First label is the time series
        final List<String> shortLabels = Arrays.asList(graphModel.getLabels().get(1), graphModel.getLabels().get(2));
        final NPPhraseSpec firstSeriesShortLabel = pluralise(shortLabels.get(0));
        final NPPhraseSpec secondSeriesShortLabel = pluralise(shortLabels.get(1));
        return Arrays.asList(firstSeriesShortLabel, secondSeriesShortLabel);
    }

    /**
     * Simple NLG doesn't seem to cope with being given nouns already in the
     * plural form, so we replace them with the singlular version, and then
     * pluralise them.
     *
     * @param subject
     * @return
     */
    private NPPhraseSpec pluralise(final String nounString) {
        boolean initiallyPlural = false;
        for (final String term : COMMON_PLURAL_TERMS) {
            if (nounString.toLowerCase().contains(term)) {
                initiallyPlural = true;
                break;
            }
        }
        final NPPhraseSpec noun = this.nlgFactory.createNounPhrase(nounString);
        if (initiallyPlural) {
            noun.setPlural(true);
            noun.setHead(nounString);
            noun.setPlural(true);
        }
        return noun;
    }

    @Override
    public NPPhraseSpec getLabelForCommonUse(final GraphSegment graphSegment, final SeriesSegment seriesSegment) {
        final List<String> shortLabels = Arrays.asList(graphSegment.getSeriesSegment(0).getLabel(),
                graphSegment.getSeriesSegment(1).getLabel());
        final String shortLabel = shortLabels.get(graphSegment.indexOf(seriesSegment));
        return pluralise(shortLabel);
    }

}