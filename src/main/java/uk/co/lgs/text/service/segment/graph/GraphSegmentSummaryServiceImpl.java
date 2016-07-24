package uk.co.lgs.text.service.segment.graph;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import simplenlg.features.Feature;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.DocumentElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.english.Realiser;
import uk.co.lgs.model.gradient.GradientType;
import uk.co.lgs.model.graph.GraphModel;
import uk.co.lgs.model.segment.graph.GraphSegment;
import uk.co.lgs.model.segment.graph.category.GapTrend;
import uk.co.lgs.model.segment.series.SeriesSegment;
import uk.co.lgs.text.service.label.LabelService;
import uk.co.lgs.text.service.synonym.Constants;
import uk.co.lgs.text.service.synonym.SynonymService;
import uk.co.lgs.text.service.value.ValueService;

/**
 * I am responsible for generating a summary of each individual graph segment.
 *
 * @author bouncysteve
 *
 */
@Component
public class GraphSegmentSummaryServiceImpl implements GraphSegmentSummaryService {
    private static final Logger LOG = LoggerFactory.getLogger(GraphSegmentSummaryServiceImpl.class);
    private static final Lexicon LEXICON = Lexicon.getDefaultLexicon();
    private static final Realiser REALISER = new Realiser(LEXICON);

    @Autowired
    private LabelService labelService;
    @Autowired
    private SynonymService synonymService;
    @Autowired
    private ValueService valueService;

    private final NLGFactory nlgFactory = new NLGFactory(LEXICON);

    @Override
    public List<DocumentElement> getSegmentSummaries(final GraphModel model) {
        final List<NPPhraseSpec> labels = this.labelService.getLabelsForCommonUse(model);
        final boolean intersectingGraph = model.isIntersecting();

        final List<DocumentElement> segmentSummaries = new ArrayList<>();
        boolean mentionedMaxGapYet = false;
        boolean mentionedMinGapYet = false;
        for (final GraphSegment graphSegment : model.getGraphSegments()) {
            segmentSummaries
                    .add(getSummary(graphSegment, intersectingGraph, mentionedMaxGapYet, mentionedMinGapYet, labels));
            if (!mentionedMaxGapYet && graphSegment.isGlobalMaximumGapAtSegmentEnd()) {
                mentionedMaxGapYet = true;
            }
            if (!mentionedMinGapYet && graphSegment.isGlobalMinimumGapAtSegmentEnd()) {
                mentionedMinGapYet = true;
            }
        }
        return segmentSummaries;
    }

    private DocumentElement getSummary(final GraphSegment graphSegment, final boolean intersectingGraph,
            final boolean mentionedMaxGapYet, final boolean mentionedMinGapYet, final List<NPPhraseSpec> labels) {
        LOG.info("******************************************");
        DocumentElement summary;
        CoordinatedPhraseElement endValuesPhrase = null;
        if (null == graphSegment.getHigherSeriesAtStart() && null != graphSegment.getHigherSeriesAtEnd()) {
            endValuesPhrase = describeSeriesWithDifferentEndValues(labels, graphSegment);
            LOG.info(REALISER.realiseSentence(endValuesPhrase));
            endValuesPhrase.addPreModifier(
                    this.nlgFactory.createPrepositionPhrase(this.synonymService.getSynonym(Constants.SO_THAT)));
            LOG.info(REALISER.realiseSentence(endValuesPhrase));
        }

        if (isSameTrends(graphSegment)) {
            summary = getSameTrendsSummary(graphSegment, intersectingGraph, mentionedMaxGapYet, mentionedMinGapYet,
                    labels);
        } else {
            summary = getOppositeTrendsSummary(graphSegment, intersectingGraph, mentionedMaxGapYet, mentionedMinGapYet,
                    labels);
        }

        if (null != endValuesPhrase) {
            summary.addComponent(endValuesPhrase);
        }
        return summary;
    }

    private DocumentElement getOppositeTrendsSummary(final GraphSegment graphSegment, final boolean intersectingGraph,
            final boolean mentionedMaxGapYet, final boolean mentionedMinGapYet, final List<NPPhraseSpec> labels) {

        DocumentElement oppositeTrendsSummary;
        if (graphSegment.isIntersecting()) {
            oppositeTrendsSummary = getOppositeTrendsIntersecting(graphSegment, labels);
        } else {
            oppositeTrendsSummary = getOppositeTrendsNonIntersecting(graphSegment, intersectingGraph,
                    mentionedMaxGapYet, mentionedMinGapYet, labels);
        }
        LOG.info(REALISER.realiseSentence(oppositeTrendsSummary));
        return oppositeTrendsSummary;
    }

    private DocumentElement getOppositeTrendsIntersecting(final GraphSegment graphSegment,
            final List<NPPhraseSpec> labels) {
        PPPhraseSpec preposition = null;
        CoordinatedPhraseElement endValuesPhrase = null;
        CoordinatedPhraseElement trendsPhrase;
        if (0 == graphSegment.getGapBetweenSeriesEndValues()) {
            preposition = getEndTime(this.synonymService.getSynonym(Constants.BY), graphSegment);
            final SeriesSegment firstSeriesSegment = graphSegment.getSeriesSegment(0);
            final String conjunction = "to " + this.valueService.formatValueWithUnits(firstSeriesSegment.getEndValue(),
                    firstSeriesSegment.getUnits()) + " and ";
            trendsPhrase = getOppositeTrends(graphSegment, labels, preposition, conjunction);
            trendsPhrase.addPostModifier(this.synonymService.getSynonym(Constants.TO_THE_SAME_VALUE));
        } else {
            trendsPhrase = getOppositeTrends(graphSegment, labels, preposition, null);

            trendsPhrase.addPostModifier(getAndTheyCross());
            preposition = this.nlgFactory.createPrepositionPhrase(this.synonymService.getSynonym(Constants.NEXT));
            endValuesPhrase = describeSeriesWithDifferentEndValues(labels, graphSegment);
            endValuesPhrase
                    .addPreModifier(getEndTime(this.synonymService.getSynonym(Constants.BY), graphSegment));
        }

        final CoordinatedPhraseElement parentPhrase = this.nlgFactory.createCoordinatedPhrase();
        parentPhrase.addCoordinate(trendsPhrase);
        if (null != endValuesPhrase) {
            parentPhrase.setConjunction(this.synonymService.getSynonym(Constants.SO_THAT));
            parentPhrase.addCoordinate(endValuesPhrase);
            parentPhrase.addPreModifier(preposition);
        }

        return this.nlgFactory.createSentence(parentPhrase);
    }

    private SPhraseSpec getAndTheyCross() {
        final NPPhraseSpec series = this.nlgFactory.createNounPhrase();
        series.setPlural(true);
        series.setDeterminer(this.synonymService.getSynonym(Constants.THEY));
        final SPhraseSpec crossPhrase = this.nlgFactory.createClause();
        crossPhrase.setSubject(series);
        crossPhrase.setVerb(Constants.CROSS);
        return crossPhrase;
    }

    private DocumentElement getSameTrendsIntersecting(final GraphSegment graphSegment,
            final List<NPPhraseSpec> labels) {
        final CoordinatedPhraseElement parentPhrase = this.nlgFactory.createCoordinatedPhrase();
        CoordinatedPhraseElement endValuesPhrase = null;
        SPhraseSpec trendsPhrase;
        CoordinatedPhraseElement steepnessPhrase = null;
        if (0 == graphSegment.getGapBetweenSeriesEndValues()) {
            trendsPhrase = getSameTrends(graphSegment,
                    getEndTime(this.synonymService.getSynonym(Constants.BY), graphSegment));
            final SeriesSegment firstSeriesSegment = graphSegment.getSeriesSegment(0);
            final String complement = "to " + this.valueService.formatValueWithUnits(firstSeriesSegment.getEndValue(),
                    firstSeriesSegment.getUnits());
            trendsPhrase.addComplement(complement);
        } else {
            trendsPhrase = getSameTrends(graphSegment, null);

            steepnessPhrase = getSteepness(graphSegment, labels);
            LOG.info(REALISER.realiseSentence(steepnessPhrase));
            endValuesPhrase = describeSeriesWithDifferentEndValues(labels, graphSegment);
            LOG.info(REALISER.realiseSentence(endValuesPhrase));
            endValuesPhrase
                    .addPreModifier(getEndTime(this.synonymService.getSynonym(Constants.BY), graphSegment));
            LOG.info(REALISER.realiseSentence(endValuesPhrase));
        }
        parentPhrase.addCoordinate(trendsPhrase);
        parentPhrase.addCoordinate(steepnessPhrase);
        parentPhrase.addCoordinate(endValuesPhrase);
        LOG.info(REALISER.realiseSentence(steepnessPhrase));
        return this.nlgFactory.createSentence(parentPhrase);
    }

    private DocumentElement getOppositeTrendsNonIntersecting(final GraphSegment graphSegment,
            final boolean intersectingGraph, final boolean mentionedMaxGapYet, final boolean mentionedMinGapYet,
            final List<NPPhraseSpec> labels) {

        final CoordinatedPhraseElement childPhrase = getOppositeTrends(graphSegment, labels,
                getEndTime(this.synonymService.getSynonym(Constants.UNTIL), graphSegment), null);
        final CoordinatedPhraseElement parentPhrase = this.nlgFactory.createCoordinatedPhrase();
        parentPhrase.addCoordinate(childPhrase);
        parentPhrase.setConjunction(this.synonymService.getSynonym(Constants.SO));
        parentPhrase
                .addCoordinate(getGap(graphSegment, intersectingGraph, mentionedMaxGapYet, mentionedMinGapYet));
        return this.nlgFactory.createSentence(parentPhrase);
    }

    private DocumentElement getSameTrendsNonIntersecting(final GraphSegment graphSegment,
            final boolean intersectingGraph, final boolean mentionedMaxGapYet, final boolean mentionedMinGapYet,
            final List<NPPhraseSpec> labels) {
        final CoordinatedPhraseElement parentPhrase = this.nlgFactory.createCoordinatedPhrase();
        parentPhrase.addCoordinate(getSameTrends(graphSegment,
                getEndTime(this.synonymService.getSynonym(Constants.UNTIL), graphSegment)));

        final SPhraseSpec gapPhrase = getGap(graphSegment, intersectingGraph, mentionedMaxGapYet,
                mentionedMinGapYet);
        parentPhrase.addCoordinate(getSteepness(graphSegment, labels));
        gapPhrase.addFrontModifier(Constants.SO);
        parentPhrase.addCoordinate(gapPhrase);
        LOG.info(REALISER.realiseSentence(parentPhrase));
        return this.nlgFactory.createSentence(parentPhrase);
    }

    /** Because <series> [increases/decreases] more steeply (they cross) */
    private CoordinatedPhraseElement getSteepness(final GraphSegment graphSegment,
            final List<NPPhraseSpec> labels) {
        final CoordinatedPhraseElement parentPhrase = this.nlgFactory.createCoordinatedPhrase();
        final SPhraseSpec steepnessPreposition = this.nlgFactory.createClause();
        final SPhraseSpec crossPhrase = this.nlgFactory.createClause();
        final Integer indexOfSteeperSeries = getIndexOfSteeperSeries(graphSegment);
        if (null != indexOfSteeperSeries) {
            final NPPhraseSpec subject = labels.get(indexOfSteeperSeries);

            steepnessPreposition.setSubject(subject);
            final VPPhraseSpec verb = this.nlgFactory
                    .createVerbPhrase(getTrendString(graphSegment.getSeriesSegment(indexOfSteeperSeries)));
            verb.addPostModifier(this.synonymService.getSynonym(Constants.MORE_STEEPLY));
            steepnessPreposition.setVerb(verb);
            parentPhrase.addCoordinate(steepnessPreposition);
            if (graphSegment.isIntersecting()) {
                final NPPhraseSpec series = this.nlgFactory.createNounPhrase();
                series.setPlural(true);
                series.setDeterminer(this.synonymService.getSynonym(Constants.THEY));
                crossPhrase.setSubject(series);
                crossPhrase.setVerb(Constants.CROSS);
                LOG.info(REALISER.realiseSentence(crossPhrase));
                parentPhrase.addCoordinate(crossPhrase);
                parentPhrase.setConjunction(this.synonymService.getSynonym(Constants.SO));
            }
        }
        LOG.info(REALISER.realiseSentence(parentPhrase));
        return parentPhrase;
    }

    private Integer getIndexOfSteeperSeries(final GraphSegment graphSegment) {
        final double firstSeriesSteepness = Math.abs(graphSegment.getSeriesSegment(0).getGradient());
        final double secondSeriesSteepness = Math.abs(graphSegment.getSeriesSegment(1).getGradient());
        Integer index = null;
        if (firstSeriesSteepness > secondSeriesSteepness) {
            index = 0;
        } else if (firstSeriesSteepness < secondSeriesSteepness) {
            index = 1;
        }
        return index;
    }

    private CoordinatedPhraseElement getOppositeTrends(final GraphSegment graphSegment,
            final List<NPPhraseSpec> labels, final PPPhraseSpec endTimePhrase, final String conjunction) {
        String localConjunction = conjunction;
        final CoordinatedPhraseElement trendsPhrase = this.nlgFactory.createCoordinatedPhrase();
        SeriesSegment higherSeriesAtStart = graphSegment.getHigherSeriesAtStart();
        if (null == higherSeriesAtStart) {
            higherSeriesAtStart = graphSegment.getSeriesSegment(0);
        }
        final int higherSeriesIndex = graphSegment.indexOf(higherSeriesAtStart);
        final SPhraseSpec trendPhrase = getTrend(higherSeriesAtStart, labels.get(higherSeriesIndex));
        trendPhrase.addFrontModifier(endTimePhrase);
        trendsPhrase.addCoordinate(trendPhrase);
        if (null == localConjunction) {
            localConjunction = this.synonymService.getSynonym(Constants.BUT);
        }
        trendsPhrase.setConjunction(localConjunction);
        trendsPhrase.addCoordinate(getTrend(graphSegment.getSeriesSegment(1 - higherSeriesIndex),
                labels.get(1 - higherSeriesIndex)));
        // This in combination with REALISER.setCommaSepCuephrase(true) above,
        // should add a comma here, but doesn't seem to.
        trendsPhrase.setFeature(Feature.CUE_PHRASE, true);
        return trendsPhrase;
    }

    private SPhraseSpec getSameTrends(final GraphSegment graphSegment, final PPPhraseSpec endTimePhrase) {
        final NPPhraseSpec noun = this.nlgFactory.createNounPhrase();
        noun.setPlural(true);
        noun.setDeterminer(this.synonymService.getSynonym(Constants.BOTH));
        final SeriesSegment firstSeriesSegment = graphSegment.getSeriesSegment(0);
        final VPPhraseSpec verb = this.nlgFactory.createVerbPhrase(getTrendString(firstSeriesSegment));
        final SPhraseSpec trendPhrase = this.nlgFactory.createClause();
        if (graphSegment.getSeriesSegment(0).getGradient() == graphSegment.getSeriesSegment(1).getGradient()) {
            verb.addPostModifier(this.synonymService.getSynonym(Constants.AT_THE_SAME_RATE));
        }
        trendPhrase.setSubject(noun);
        trendPhrase.setVerb(verb);
        PPPhraseSpec leadingEndTimePhrase = endTimePhrase;
        if (null == leadingEndTimePhrase) {
            leadingEndTimePhrase = this.nlgFactory
                    .createPrepositionPhrase(this.synonymService.getSynonym(Constants.NEXT));
        }
        trendPhrase.addFrontModifier(leadingEndTimePhrase);
        return trendPhrase;
    }

    // the gap between them increases/decreases (to VALUE), (its minimum value)
    private SPhraseSpec getGap(final GraphSegment graphSegment, final boolean intersectingGraph,
            final boolean mentionedMaxGapYet, final boolean mentionedMinGapYet) {
        final SPhraseSpec gapPhrase = this.nlgFactory.createClause();
        String verbString = null;
        String preposition = Constants.TO;
        switch (graphSegment.getGapTrend()) {
        case CONVERGING:
            verbString = Constants.DECREASE;
            break;
        case DIVERGING:
            verbString = Constants.INCREASE;
            break;
        case PARALLEL:
            verbString = Constants.REMAIN;
            preposition = null;
            break;
        default:
            break;
        }

        gapPhrase.setSubject(this.synonymService.getSynonym(Constants.THE_GAP_BETWEEN_THEM));
        final VPPhraseSpec verb = this.nlgFactory.createVerbPhrase(this.synonymService.getSynonym(verbString));
        gapPhrase.setVerb(verb);

        if (graphSegment.isGlobalMaximumGapAtSegmentEnd() || graphSegment.isGlobalMinimumGapAtSegmentEnd()
                || GapTrend.PARALLEL.equals(graphSegment.getGapTrend())) {
            gapPhrase.addComplement(getGapValue(graphSegment, preposition));
        }

        if (graphSegment.isGlobalMaximumGapAtSegmentEnd()) {
            gapPhrase.addPostModifier(getMaximumValue(mentionedMaxGapYet));
        } else if (graphSegment.isGlobalMinimumGapAtSegmentEnd()) {
            gapPhrase.addPostModifier(getMinimumValue(mentionedMinGapYet, intersectingGraph));
        }
        return gapPhrase;
    }

    private PPPhraseSpec getGapValue(final GraphSegment graphSegment, final String preposition) {
        return this.nlgFactory.createPrepositionPhrase(this.synonymService.getSynonym(preposition),
                this.valueService.formatValueWithUnits(graphSegment.getGapBetweenSeriesEndValues(),
                        graphSegment.getSeriesSegment(0).getUnits()));
    }

    private String getMaximumValue(final boolean mentionedMaxGapYet) {
        return mentionedMaxGapYet ? "" : "its maximum value";
    }

    private String getMinimumValue(final boolean mentionedMinGapYet, final boolean intersectingGraph) {
        String minPhrase = "its minimum value";

        if (mentionedMinGapYet || intersectingGraph) {
            minPhrase = "";
        }
        return minPhrase;
    }

    /** For the trend of an individual series. */
    private String getTrendString(final SeriesSegment seriesSegment) {
        String verbString = null;
        switch (seriesSegment.getGradientType()) {
        case NEGATIVE:
            verbString = Constants.DECREASE;
            break;
        case POSITIVE:
            verbString = Constants.INCREASE;
            break;
        case ZERO:
            verbString = Constants.REMAIN;
            break;
        default:
            break;
        }
        return this.synonymService.getSynonym(verbString);
    }

    /** For the trend of an individual series. */
    private SPhraseSpec getTrend(final SeriesSegment seriesSegment, final NPPhraseSpec label) {
        return this.nlgFactory.createClause(label, getTrendString(seriesSegment));
    }

    private DocumentElement getSameTrendsSummary(final GraphSegment graphSegment, final boolean intersectingGraph,
            final boolean mentionedMaxGapYet, final boolean mentionedMinGapYet, final List<NPPhraseSpec> labels) {

        DocumentElement sameTrendsPhrase;
        if (graphSegment.isIntersecting()) {
            sameTrendsPhrase = getSameTrendsIntersecting(graphSegment, labels);
        } else {
            sameTrendsPhrase = getSameTrendsNonIntersecting(graphSegment, intersectingGraph, mentionedMaxGapYet,
                    mentionedMinGapYet, labels);
        }
        LOG.info(REALISER.realiseSentence(sameTrendsPhrase));
        return sameTrendsPhrase;
    }

    private boolean isSameTrends(final GraphSegment graphSegment) {
        final GradientType firstGradient = graphSegment.getFirstSeriesTrend();
        final GradientType secondGradient = graphSegment.getSecondSeriesTrend();
        return null != firstGradient && null != secondGradient && firstGradient.equals(secondGradient);
    }

    private final PPPhraseSpec getEndTime(final String preposition, final GraphSegment graphSegment) {
        return this.nlgFactory.createPrepositionPhrase(this.synonymService.getSynonym(preposition),
                graphSegment.getEndTime());
    }

    private CoordinatedPhraseElement describeSeriesWithDifferentEndValues(final List<NPPhraseSpec> labels,
            final GraphSegment segment) {
        final SeriesSegment higherSeries = segment.getHigherSeriesAtEnd();
        final NPPhraseSpec higherSeriesNoun = labels.get(segment.indexOf(higherSeries));
        final VPPhraseSpec higherVerb = this.nlgFactory
                .createVerbPhrase(this.synonymService.getSynonym(Constants.BE_HIGHER));
        final NPPhraseSpec higherSeriesValue = this.nlgFactory.createNounPhrase(
                this.valueService.formatValueWithUnits(higherSeries.getEndValue(), higherSeries.getUnits()));
        higherSeriesValue.addPreModifier(this.synonymService.getSynonym(Constants.WITH));
        final SPhraseSpec higherSeriesPhrase = this.nlgFactory.createClause(higherSeriesNoun, higherVerb,
                higherSeriesValue);

        final SeriesSegment lowerSeries = segment.getSeriesSegment(1 - segment.indexOf(higherSeries));
        final NPPhraseSpec lowerSeriesNoun = labels.get(segment.indexOf(lowerSeries));
        final VPPhraseSpec lowerVerb = this.nlgFactory.createVerbPhrase(this.synonymService.getSynonym(Constants.HAS));
        final NPPhraseSpec lowerSeriesValue = this.nlgFactory.createNounPhrase(
                this.valueService.formatValueWithUnits(lowerSeries.getEndValue(), lowerSeries.getUnits()));
        final SPhraseSpec lowerSeriesPhrase = this.nlgFactory.createClause(lowerSeriesNoun, lowerVerb,
                lowerSeriesValue);

        final CoordinatedPhraseElement differentValuesPhrase = this.nlgFactory.createCoordinatedPhrase();
        differentValuesPhrase.addCoordinate(higherSeriesPhrase);
        LOG.info(REALISER.realiseSentence(differentValuesPhrase));
        differentValuesPhrase.addCoordinate(lowerSeriesPhrase);
        LOG.info(REALISER.realiseSentence(differentValuesPhrase));
        differentValuesPhrase.setConjunction(this.synonymService.getSynonym(Constants.WHILE));
        LOG.info(REALISER.realiseSentence(differentValuesPhrase));
        return differentValuesPhrase;
    }

}
