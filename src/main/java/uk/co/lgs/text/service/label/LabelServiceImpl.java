package uk.co.lgs.text.service.label;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import simplenlg.features.Feature;
import simplenlg.features.NumberAgreement;
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
     * label and the rest of the phrase.
     */
    private static final List<String> COMMON_PLURAL_TERMS = Arrays.asList("sales");

    /**
     * If either label is shorten than this many words then it doesn't make
     * sense to shorten them.
     */
    private static final int LIMIT = 6;

    private final NLGFactory nlgFactory = new NLGFactory(LEXICON);

    @Override
    public List<NPPhraseSpec> getLabelsForInitialUse(final GraphModel graphModel) {
        return getLabelsForInitialUse(graphModel.getGraphSegments().get(0));
    }

    @Override
    public List<NPPhraseSpec> getLabelsForInitialUse(final GraphSegment graphSegment) {
        final List<String> labels = Arrays.asList(null, graphSegment.getSeriesSegment(0).getLabel(),
                graphSegment.getSeriesSegment(1).getLabel());
        return getLabelsForInitialUse(labels);
    }

    @Override
    public List<NPPhraseSpec> getLabelsForInitialUse(final List<String> labels) {
        final List<String> labelsWithoutTimeSeries = labels.subList(1, labels.size());
        final List<String> shortLabels = shortenLabels(labelsWithoutTimeSeries);
        final List<String> newLabels = new ArrayList<>();
        for (int index = 0; index < labelsWithoutTimeSeries.size(); index++) {
            final String label = labelsWithoutTimeSeries.get(index);
            final String shortLabel = shortLabels.get(index);
            if (label.equals(shortLabel)) {
                newLabels.add(label);
            } else {
                newLabels.add(label + " (" + shortLabel + ")");
            }
        }

        final NPPhraseSpec firstSeriesLabel = pluralise(this.nlgFactory.createNounPhrase(newLabels.get(0)));
        final NPPhraseSpec secondSeriesLabel = pluralise(this.nlgFactory.createNounPhrase(newLabels.get(1)));
        return Arrays.asList(firstSeriesLabel, secondSeriesLabel);
    }

    @Override
    public List<NPPhraseSpec> getLabelsForCommonUse(final GraphSegment graphSegment) {
        final List<String> labels = Arrays.asList(graphSegment.getSeriesSegment(0).getLabel(),
                graphSegment.getSeriesSegment(1).getLabel());
        final List<String> shortLabels = shortenLabels(labels);
        final NPPhraseSpec firstSeriesShortLabel = pluralise(this.nlgFactory.createNounPhrase(shortLabels.get(0)));
        final NPPhraseSpec secondSeriesShortLabel = pluralise(this.nlgFactory.createNounPhrase(shortLabels.get(1)));
        return Arrays.asList(firstSeriesShortLabel, secondSeriesShortLabel);
    }

    /**
     * If labels are long, we can remove common elements to use only the unique
     * parts.
     *
     * @param firstSeriesLabel
     * @param secondSeriesLabel
     * @return
     */
    private List<String> shortenLabels(final List<String> seriesLabels) {
        final List<String> shortLabels = new ArrayList<>();
        final String firstSeriesLabel = seriesLabels.get(0);
        final String secondSeriesLabel = seriesLabels.get(1);
        final List<String> firstSeriesWords = Arrays.asList(firstSeriesLabel.split(" "));
        final List<String> secondSeriesWords = Arrays.asList(secondSeriesLabel.split(" "));
        if (firstSeriesWords.size() < LIMIT || secondSeriesWords.size() < LIMIT) {
            return seriesLabels;
        }

        StringBuilder builder = new StringBuilder();
        String commonStart = "";
        for (final char letter : firstSeriesLabel.toCharArray()) {
            builder.append(letter);
            if (secondSeriesLabel.startsWith(builder.toString())) {
                commonStart = builder.toString();
            } else {
                break;
            }
        }
        if (!commonStart.isEmpty()) {
            shortLabels.add(capitaliseFirstLetter(firstSeriesLabel.substring(commonStart.length())));
            shortLabels.add(capitaliseFirstLetter(secondSeriesLabel.substring(commonStart.length())));
        } else {

            final String reverseFirstSeriesLabel = new StringBuilder(firstSeriesLabel).reverse().toString();
            String commonEnd = "";
            builder = new StringBuilder();
            for (final char letter : reverseFirstSeriesLabel.toCharArray()) {
                builder.insert(0, letter);
                if (secondSeriesLabel.endsWith(builder.toString())) {
                    commonEnd = builder.toString();
                } else {
                    break;
                }
            }
            if (!commonEnd.isEmpty()) {
                shortLabels.add(capitaliseFirstLetter(
                        firstSeriesLabel.substring(0, firstSeriesLabel.length() - commonEnd.length())));
                shortLabels.add(capitaliseFirstLetter(
                        secondSeriesLabel.substring(0, secondSeriesLabel.length() - commonEnd.length())));
            } else {
                return seriesLabels;
            }
        }

        return shortLabels;
    }

    private NPPhraseSpec pluralise(final NPPhraseSpec subject) {
        for (final String term : COMMON_PLURAL_TERMS) {
            if (subject.getHead().toString().toLowerCase().contains(term)) {
                subject.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
                break;
            }
        }
        return subject;
    }

    private String capitaliseFirstLetter(final String input) {

        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    @Override
    public NPPhraseSpec getLabelForCommonUse(final GraphSegment graphSegment, final SeriesSegment seriesSegment) {
        final List<String> labels = Arrays.asList(graphSegment.getSeriesSegment(0).getLabel(),
                graphSegment.getSeriesSegment(1).getLabel());
        final List<String> shortLabels = shortenLabels(labels);
        final String shortLabel = shortLabels.get(graphSegment.indexOf(seriesSegment));
        return pluralise(this.nlgFactory.createNounPhrase(shortLabel));
    }

}