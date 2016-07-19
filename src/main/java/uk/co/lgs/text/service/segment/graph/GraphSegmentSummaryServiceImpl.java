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
        // REALISER.setCommaSepCuephrase(true);
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
        DocumentElement summary;

        if (null == graphSegment.getHigherSeriesAtStart()) {
            summary = getSameValueAtStartSentence(graphSegment, labels);
        }

        if (isSameTrends(graphSegment)) {
            summary = getSameTrendsSummary(graphSegment, intersectingGraph, mentionedMaxGapYet, mentionedMinGapYet,
                    labels);
        } else {
            summary = getOppositeTrendsSummary(graphSegment, intersectingGraph, mentionedMaxGapYet, mentionedMinGapYet,
                    labels);
        }

        // TODO: if both end on same value then add extra sentence
        return summary;
    }

    private DocumentElement getSameValueAtStartSentence(final GraphSegment graphSegment,
            final List<NPPhraseSpec> labels) {
        SPhraseSpec sameStartValuePhrase = null;
        if (graphSegment.getSeriesSegment(0).getStartValue() == graphSegment.getSeriesSegment(1).getStartValue()) {
            sameStartValuePhrase = this.nlgFactory.createClause();
            final CoordinatedPhraseElement subject = this.nlgFactory.createCoordinatedPhrase(labels.get(0),
                    labels.get(1));
            // FIXME: use determiner to set subject to "both"
            final VPPhraseSpec verb = this.nlgFactory.createVerbPhrase("both have value");
            final SeriesSegment firstSeriesSegment = graphSegment.getSeriesSegment(0);

            final NPPhraseSpec object = this.nlgFactory.createNounPhrase(this.valueService
                    .formatValueWithUnits(firstSeriesSegment.getStartValue(), firstSeriesSegment.getUnits()));
            sameStartValuePhrase.setSubject(subject);
            final PPPhraseSpec preposition = getStartTimePhrase(this.synonymService.getSynonym(Constants.AT),
                    graphSegment);
            sameStartValuePhrase.setVerb(verb);
            if (null != object) {
                sameStartValuePhrase.setObject(object);
            }
            sameStartValuePhrase.addComplement(preposition);

            LOG.info(REALISER.realiseSentence(sameStartValuePhrase));
        }
        return this.nlgFactory.createSentence(sameStartValuePhrase);
    }

    private DocumentElement getOppositeTrendsSummary(final GraphSegment graphSegment, final boolean intersectingGraph,
            final boolean mentionedMaxGapYet, final boolean mentionedMinGapYet, final List<NPPhraseSpec> labels) {

        DocumentElement oppositeTrendsSummary;
        if (graphSegment.isIntersecting()) {
            oppositeTrendsSummary = getOppositeTrendsIntersectingPhrase(graphSegment, labels);
        } else {
            oppositeTrendsSummary = getOppositeTrendsNonIntersectingPhrase(graphSegment, intersectingGraph,
                    mentionedMaxGapYet, mentionedMinGapYet, labels);
        }
        LOG.info(REALISER.realiseSentence(oppositeTrendsSummary));
        return oppositeTrendsSummary;
    }

    private DocumentElement getOppositeTrendsIntersectingPhrase(final GraphSegment graphSegment,
            final List<NPPhraseSpec> labels) {
        PPPhraseSpec preposition = null;
        CoordinatedPhraseElement endValuesPhrase = null;
        CoordinatedPhraseElement trendsPhrase;
        if (0 == graphSegment.getGapBetweenSeriesEndValues()) {
            preposition = getEndTimePhrase(this.synonymService.getSynonym(Constants.BY), graphSegment);
            final SeriesSegment firstSeriesSegment = graphSegment.getSeriesSegment(0);
            final String conjunction = "to " + this.valueService.formatValueWithUnits(firstSeriesSegment.getEndValue(),
                    firstSeriesSegment.getUnits()) + " and ";
            trendsPhrase = getOppositeTrendsPhrase(graphSegment, labels, preposition, conjunction);
            trendsPhrase.addPostModifier("to the same value");
        } else {
            trendsPhrase = getOppositeTrendsPhrase(graphSegment, labels, preposition, null);
            trendsPhrase.addPostModifier("and they cross");
            preposition = this.nlgFactory.createPrepositionPhrase(this.synonymService.getSynonym(Constants.NEXT));
            endValuesPhrase = describeSeriesWithDifferentEndValues(labels, graphSegment);

            endValuesPhrase
                    .addPreModifier(getEndTimePhrase(this.synonymService.getSynonym(Constants.BY), graphSegment));
        }

        final CoordinatedPhraseElement parentPhrase = this.nlgFactory.createCoordinatedPhrase();
        parentPhrase.addCoordinate(trendsPhrase);
        if (null != endValuesPhrase) {
            parentPhrase.setConjunction("so that");
            parentPhrase.addCoordinate(endValuesPhrase);
            parentPhrase.addPreModifier(preposition);
        }

        return this.nlgFactory.createSentence(parentPhrase);
    }

    private DocumentElement getSameTrendsIntersectingPhrase(final GraphSegment graphSegment,
            final List<NPPhraseSpec> labels) {
        final CoordinatedPhraseElement parentPhrase = this.nlgFactory.createCoordinatedPhrase();
        CoordinatedPhraseElement endValuesPhrase = null;
        SPhraseSpec trendsPhrase;
        CoordinatedPhraseElement steepnessPhrase = null;
        if (0 == graphSegment.getGapBetweenSeriesEndValues()) {
            trendsPhrase = getSameTrendsPhrase(graphSegment,
                    getEndTimePhrase(this.synonymService.getSynonym(Constants.BY), graphSegment));
            final SeriesSegment firstSeriesSegment = graphSegment.getSeriesSegment(0);
            final String complement = "to " + this.valueService.formatValueWithUnits(firstSeriesSegment.getEndValue(),
                    firstSeriesSegment.getUnits());
            trendsPhrase.addComplement(complement);
        } else {
            trendsPhrase = getSameTrendsPhrase(graphSegment, null);

            steepnessPhrase = getSteepnessPhrase(graphSegment, labels);
            LOG.info(REALISER.realiseSentence(steepnessPhrase));
            endValuesPhrase = describeSeriesWithDifferentEndValues(labels, graphSegment);
            LOG.info(REALISER.realiseSentence(endValuesPhrase));
            endValuesPhrase
                    .addPreModifier(getEndTimePhrase(this.synonymService.getSynonym(Constants.BY), graphSegment));
            LOG.info(REALISER.realiseSentence(endValuesPhrase));
        }
        parentPhrase.addCoordinate(trendsPhrase);
        parentPhrase.addCoordinate(steepnessPhrase);
        parentPhrase.addCoordinate(endValuesPhrase);
        LOG.info(REALISER.realiseSentence(steepnessPhrase));
        return this.nlgFactory.createSentence(parentPhrase);
    }

    private DocumentElement getOppositeTrendsNonIntersectingPhrase(final GraphSegment graphSegment,
            final boolean intersectingGraph, final boolean mentionedMaxGapYet, final boolean mentionedMinGapYet,
            final List<NPPhraseSpec> labels) {

        final CoordinatedPhraseElement childPhrase = getOppositeTrendsPhrase(graphSegment, labels,
                getEndTimePhrase(this.synonymService.getSynonym(Constants.UNTIL), graphSegment), null);
        final CoordinatedPhraseElement parentPhrase = this.nlgFactory.createCoordinatedPhrase();
        parentPhrase.addCoordinate(childPhrase);
        parentPhrase.setConjunction(this.synonymService.getSynonym(Constants.SO));
        parentPhrase
                .addCoordinate(getGapPhrase(graphSegment, intersectingGraph, mentionedMaxGapYet, mentionedMinGapYet));
        return this.nlgFactory.createSentence(parentPhrase);
    }

    private DocumentElement getSameTrendsNonIntersectingPhrase(final GraphSegment graphSegment,
            final boolean intersectingGraph, final boolean mentionedMaxGapYet, final boolean mentionedMinGapYet,
            final List<NPPhraseSpec> labels) {
        final CoordinatedPhraseElement parentPhrase = this.nlgFactory.createCoordinatedPhrase();
        parentPhrase.addCoordinate(getSameTrendsPhrase(graphSegment,
                getEndTimePhrase(this.synonymService.getSynonym(Constants.UNTIL), graphSegment)));

        final SPhraseSpec gapPhrase = getGapPhrase(graphSegment, intersectingGraph, mentionedMaxGapYet,
                mentionedMinGapYet);
        parentPhrase.addCoordinate(getSteepnessPhrase(graphSegment, labels));
        gapPhrase.addFrontModifier(Constants.SO);
        parentPhrase.addCoordinate(gapPhrase);
        LOG.info(REALISER.realiseSentence(parentPhrase));
        return this.nlgFactory.createSentence(parentPhrase);
    }

    /** Because <series> [increases/decreases] more steeply (they cross) */
    private CoordinatedPhraseElement getSteepnessPhrase(final GraphSegment graphSegment,
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
            verb.addPostModifier(this.synonymService.getSynonym("more steeply"));
            steepnessPreposition.setVerb(verb);
            parentPhrase.addCoordinate(steepnessPreposition);
            if (graphSegment.isIntersecting()) {
                final NPPhraseSpec series = this.nlgFactory.createNounPhrase();
                series.setPlural(true);
                series.setDeterminer("they");
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

    private CoordinatedPhraseElement getOppositeTrendsPhrase(final GraphSegment graphSegment,
            final List<NPPhraseSpec> labels, final PPPhraseSpec endTimePhrase, final String conjunction) {
        String localConjunction = conjunction;
        final CoordinatedPhraseElement trendsPhrase = this.nlgFactory.createCoordinatedPhrase();
        SeriesSegment higherSeriesAtStart = graphSegment.getHigherSeriesAtStart();
        if (null == higherSeriesAtStart) {
            higherSeriesAtStart = graphSegment.getSeriesSegment(0);
        }
        final int higherSeriesIndex = graphSegment.indexOf(higherSeriesAtStart);
        final SPhraseSpec trendPhrase = getTrendPhrase(higherSeriesAtStart, labels.get(higherSeriesIndex));
        trendPhrase.addFrontModifier(endTimePhrase);
        trendsPhrase.addCoordinate(trendPhrase);
        if (null == localConjunction) {
            localConjunction = this.synonymService.getSynonym(Constants.BUT);
        }
        trendsPhrase.setConjunction(localConjunction);
        trendsPhrase.addCoordinate(getTrendPhrase(graphSegment.getSeriesSegment(1 - higherSeriesIndex),
                labels.get(1 - higherSeriesIndex)));
        // This in combination with REALISER.setCommaSepCuephrase(true) above,
        // should add a comma here, but doesn't seem to.
        trendsPhrase.setFeature(Feature.CUE_PHRASE, true);
        return trendsPhrase;
    }

    private SPhraseSpec getSameTrendsPhrase(final GraphSegment graphSegment, final PPPhraseSpec endTimePhrase) {
        final NPPhraseSpec noun = this.nlgFactory.createNounPhrase();
        noun.setPlural(true);
        noun.setDeterminer("both");
        final SeriesSegment firstSeriesSegment = graphSegment.getSeriesSegment(0);
        final VPPhraseSpec verb = this.nlgFactory.createVerbPhrase(getTrendString(firstSeriesSegment));
        final SPhraseSpec trendPhrase = this.nlgFactory.createClause();
        if (graphSegment.getSeriesSegment(0).getGradient() == graphSegment.getSeriesSegment(1).getGradient()) {
            verb.addPostModifier("at the same rate");
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
    private SPhraseSpec getGapPhrase(final GraphSegment graphSegment, final boolean intersectingGraph,
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

        gapPhrase.setSubject("the gap between them");
        final VPPhraseSpec verb = this.nlgFactory.createVerbPhrase(this.synonymService.getSynonym(verbString));
        gapPhrase.setVerb(verb);

        if (graphSegment.isGlobalMaximumGapAtSegmentEnd() || graphSegment.isGlobalMinimumGapAtSegmentEnd()
                || GapTrend.PARALLEL.equals(graphSegment.getGapTrend())) {
            gapPhrase.addComplement(getGapValuePhrase(graphSegment, preposition));
        }

        if (graphSegment.isGlobalMaximumGapAtSegmentEnd()) {
            gapPhrase.addPostModifier(getMaximumValuePhrase(mentionedMaxGapYet));
        } else if (graphSegment.isGlobalMinimumGapAtSegmentEnd()) {
            gapPhrase.addPostModifier(getMinimumValuePhrase(mentionedMinGapYet, intersectingGraph, graphSegment));
        }
        return gapPhrase;
    }

    private PPPhraseSpec getGapValuePhrase(final GraphSegment graphSegment, final String preposition) {
        return this.nlgFactory.createPrepositionPhrase(this.synonymService.getSynonym(preposition),
                this.valueService.formatValueWithUnits(graphSegment.getGapBetweenSeriesEndValues(),
                        graphSegment.getSeriesSegment(0).getUnits()));
    }

    private String getMaximumValuePhrase(final boolean mentionedMaxGapYet) {
        return mentionedMaxGapYet ? "" : "its maximum value";
    }

    private String getMinimumValuePhrase(final boolean mentionedMinGapYet, final boolean intersectingGraph,
            final GraphSegment graphSegment) {
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
    private SPhraseSpec getTrendPhrase(final SeriesSegment seriesSegment, final NPPhraseSpec label) {
        return this.nlgFactory.createClause(label, getTrendString(seriesSegment));
    }

    private DocumentElement getSameTrendsSummary(final GraphSegment graphSegment, final boolean intersectingGraph,
            final boolean mentionedMaxGapYet, final boolean mentionedMinGapYet, final List<NPPhraseSpec> labels) {

        DocumentElement sameTrendsPhrase;
        if (graphSegment.isIntersecting()) {
            sameTrendsPhrase = getSameTrendsIntersectingPhrase(graphSegment, labels);
        } else {
            sameTrendsPhrase = getSameTrendsNonIntersectingPhrase(graphSegment, intersectingGraph, mentionedMaxGapYet,
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

    private final PPPhraseSpec getEndTimePhrase(final String preposition, final GraphSegment graphSegment) {
        return this.nlgFactory.createPrepositionPhrase(this.synonymService.getSynonym(preposition),
                graphSegment.getEndTime());
    }

    private PPPhraseSpec getStartTimePhrase(final String preposition, final GraphSegment graphSegment) {
        return this.nlgFactory.createPrepositionPhrase(this.synonymService.getSynonym(preposition),
                graphSegment.getStartTime());
    }

    private CoordinatedPhraseElement describeSeriesWithDifferentEndValues(final List<NPPhraseSpec> labels,
            final GraphSegment segment) {
        final SeriesSegment higherSeries = segment.getHigherSeriesAtEnd();
        final NPPhraseSpec higherSeriesNoun = labels.get(segment.indexOf(higherSeries));
        final VPPhraseSpec higherVerb = this.nlgFactory.createVerbPhrase("is higher");
        final NPPhraseSpec higherSeriesValue = this.nlgFactory.createNounPhrase(
                this.valueService.formatValueWithUnits(higherSeries.getEndValue(), higherSeries.getUnits()));
        higherSeriesValue.addPreModifier("with");
        final SPhraseSpec higherSeriesPhrase = this.nlgFactory.createClause(higherSeriesNoun, higherVerb,
                higherSeriesValue);

        final SeriesSegment lowerSeries = segment.getSeriesSegment(1 - segment.indexOf(higherSeries));
        final NPPhraseSpec lowerSeriesNoun = labels.get(segment.indexOf(lowerSeries));
        final VPPhraseSpec lowerVerb = this.nlgFactory.createVerbPhrase("have");
        final NPPhraseSpec lowerSeriesValue = this.nlgFactory.createNounPhrase(
                this.valueService.formatValueWithUnits(lowerSeries.getEndValue(), lowerSeries.getUnits()));
        final SPhraseSpec lowerSeriesPhrase = this.nlgFactory.createClause(lowerSeriesNoun, lowerVerb,
                lowerSeriesValue);

        final CoordinatedPhraseElement differentValuesPhrase = this.nlgFactory.createCoordinatedPhrase();
        differentValuesPhrase.addCoordinate(higherSeriesPhrase);
        LOG.info(REALISER.realiseSentence(differentValuesPhrase));
        differentValuesPhrase.addCoordinate(lowerSeriesPhrase);
        LOG.info(REALISER.realiseSentence(differentValuesPhrase));
        differentValuesPhrase.setConjunction("while");
        LOG.info(REALISER.realiseSentence(differentValuesPhrase));
        return differentValuesPhrase;
    }

}
