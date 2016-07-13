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
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.english.Realiser;
import uk.co.lgs.model.gradient.GradientType;
import uk.co.lgs.model.graph.GraphModel;
import uk.co.lgs.model.segment.graph.GraphSegment;
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
        REALISER.setCommaSepCuephrase(true);
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
        final DocumentElement summary = this.nlgFactory.createSentence();

        if (null == graphSegment.getHigherSeriesAtStart()) {
            summary.addComponent(getSameValueAtStartSentence(graphSegment, labels));
        }

        if (isSameTrends(graphSegment)) {
            summary.addComponent(getSameTrendsSummary(graphSegment, intersectingGraph, mentionedMaxGapYet,
                    mentionedMinGapYet, labels));
        } else {
            summary.addComponent(getOppositeTrendsSummary(graphSegment, intersectingGraph, mentionedMaxGapYet,
                    mentionedMinGapYet, labels));
        }

        // TODO: if both end on same value then add extra sentence
        return summary;
    }

    private NLGElement getSameValueAtStartSentence(final GraphSegment graphSegment, final List<NPPhraseSpec> labels) {
        SPhraseSpec sameStartValuePhrase = null;
        if (graphSegment.getSeriesSegment(0).getStartValue() == graphSegment.getSeriesSegment(1).getStartValue()) {
            sameStartValuePhrase = this.nlgFactory.createClause();
            final CoordinatedPhraseElement subject = this.nlgFactory.createCoordinatedPhrase(labels.get(0),
                    labels.get(1));
            // TODO: can "both" be a modifier that could be applied either
            // before or after the subject?????
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
            return sameStartValuePhrase;

        }
        return sameStartValuePhrase;
    }

    private NLGElement getOppositeTrendsSummary(final GraphSegment graphSegment, final boolean intersectingGraph,
            final boolean mentionedMaxGapYet, final boolean mentionedMinGapYet, final List<NPPhraseSpec> labels) {

        NLGElement oppositeTrendsPhrase;
        if (graphSegment.isIntersecting()) {
            oppositeTrendsPhrase = getOppositeTrendsIntersectingPhrase(graphSegment, labels);
        } else {
            oppositeTrendsPhrase = getOppositeTrendsNonIntersectingPhrase(graphSegment, intersectingGraph,
                    mentionedMaxGapYet, mentionedMinGapYet, labels);
        }
        LOG.info(REALISER.realiseSentence(oppositeTrendsPhrase));
        return oppositeTrendsPhrase;
    }

    private NLGElement getOppositeTrendsIntersectingPhrase(final GraphSegment graphSegment,
            final List<NPPhraseSpec> labels) {
        PPPhraseSpec preposition = null;
        CoordinatedPhraseElement endValuesPhrase = null;
        CoordinatedPhraseElement riseAndFallPhrase;
        if (0 == graphSegment.getGapBetweenSeriesEndValues()) {
            preposition = getEndTimePhrase(this.synonymService.getSynonym(Constants.BY), graphSegment);
            final SeriesSegment firstSeriesSegment = graphSegment.getSeriesSegment(0);
            final String conjunction = "to " + this.valueService.formatValueWithUnits(firstSeriesSegment.getEndValue(),
                    firstSeriesSegment.getUnits() + "and ");
            riseAndFallPhrase = getOppositeTrendsPhrase(graphSegment, labels, preposition, conjunction);
            riseAndFallPhrase.addPostModifier("to the same value");
        } else {
            riseAndFallPhrase = getOppositeTrendsPhrase(graphSegment, labels, preposition, null);
            riseAndFallPhrase.addPostModifier("and they cross");
            preposition = this.nlgFactory.createPrepositionPhrase(this.synonymService.getSynonym(Constants.NEXT));
            endValuesPhrase = describeSeriesWithDifferentEndValues(labels, graphSegment);

            endValuesPhrase
                    .addPreModifier(getEndTimePhrase(this.synonymService.getSynonym(Constants.BY), graphSegment));
        }

        final CoordinatedPhraseElement parentPhrase = this.nlgFactory.createCoordinatedPhrase();
        parentPhrase.addCoordinate(riseAndFallPhrase);
        if (null != endValuesPhrase) {
            parentPhrase.setConjunction("so that");
            parentPhrase.addCoordinate(endValuesPhrase);
            parentPhrase.addPreModifier(preposition);
        }

        return parentPhrase;
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
        differentValuesPhrase.addCoordinate(lowerSeriesPhrase);
        // TODO: introduce a conjunction service.
        differentValuesPhrase.setConjunction("while");
        if (LOG.isDebugEnabled()) {
            LOG.debug("Diferent start values phrase: {}", REALISER.realiseSentence(differentValuesPhrase));
        }
        return differentValuesPhrase;
    }

    private NLGElement getOppositeTrendsNonIntersectingPhrase(final GraphSegment graphSegment,
            final boolean intersectingGraph, final boolean mentionedMaxGapYet, final boolean mentionedMinGapYet,
            final List<NPPhraseSpec> labels) {

        final CoordinatedPhraseElement riseAndFallPhrase = getOppositeTrendsPhrase(graphSegment, labels,
                getEndTimePhrase(this.synonymService.getSynonym(Constants.UNTIL), graphSegment), null);
        final CoordinatedPhraseElement parentPhrase = this.nlgFactory.createCoordinatedPhrase();
        parentPhrase.addCoordinate(riseAndFallPhrase);
        parentPhrase.setConjunction(this.synonymService.getSynonym(Constants.SO));
        parentPhrase
                .addCoordinate(getGapPhrase(graphSegment, intersectingGraph, mentionedMaxGapYet, mentionedMinGapYet));
        return parentPhrase;
    }

    private CoordinatedPhraseElement getOppositeTrendsPhrase(final GraphSegment graphSegment,
            final List<NPPhraseSpec> labels, final PPPhraseSpec endTimePhrase, String conjunction) {
        final CoordinatedPhraseElement riseAndFallPhrase = this.nlgFactory.createCoordinatedPhrase();
        SeriesSegment higherSeriesAtStart = graphSegment.getHigherSeriesAtStart();
        if (null == higherSeriesAtStart) {
            higherSeriesAtStart = graphSegment.getSeriesSegment(0);
        }
        final int higherSeriesIndex = graphSegment.indexOf(higherSeriesAtStart);
        riseAndFallPhrase.addPreModifier(endTimePhrase);

        riseAndFallPhrase.addCoordinate(getTrendPhrase(higherSeriesAtStart, labels.get(higherSeriesIndex)));
        if (null == conjunction) {
            conjunction = this.synonymService.getSynonym(Constants.BUT);
        }
        riseAndFallPhrase.setConjunction(conjunction);
        riseAndFallPhrase.addCoordinate(getTrendPhrase(graphSegment.getSeriesSegment(1 - higherSeriesIndex),
                labels.get(1 - higherSeriesIndex)));
        // This in combination with REALISER.setCommaSepCuephrase(true) above,
        // should add a comma here, but doesn't seem to.
        riseAndFallPhrase.setFeature(Feature.CUE_PHRASE, true);
        return riseAndFallPhrase;
    }

    // ** For the gap between two series. */
    private SPhraseSpec getGapPhrase(final GraphSegment graphSegment, final boolean intersectingGraph,
            final boolean mentionedMaxGapYet, final boolean mentionedMinGapYet) {
        final SPhraseSpec gapPhrase = this.nlgFactory.createClause();
        String verbString = null;
        switch (graphSegment.getGapTrend()) {
        case CONVERGING:
            verbString = Constants.DECREASE;
            break;
        case DIVERGING:
            verbString = Constants.INCREASE;
            break;
        case PARALLEL:
            verbString = Constants.STAY_SAME;
            break;
        default:
            break;
        }

        gapPhrase.setSubject("the gap between them");
        gapPhrase.setVerb(this.synonymService.getSynonym(verbString));
        if (graphSegment.isGlobalMaximumGapAtSegmentEnd()) {
            gapPhrase
                    .addComplement(this.nlgFactory.createPrepositionPhrase("to",
                            this.valueService.formatValueWithUnits(graphSegment
                                    .getGapBetweenSeriesEndValues(),
                            graphSegment.getSeriesSegment(0).getUnits() + getMaximumValuePhrase(mentionedMaxGapYet))));
        } else if (graphSegment.isGlobalMinimumGapAtSegmentEnd()) {
            gapPhrase.addComplement(this.nlgFactory.createPrepositionPhrase("to",
                    this.valueService.formatValueWithUnits(graphSegment.getGapBetweenSeriesEndValues(),
                            graphSegment.getSeriesSegment(0).getUnits()
                                    + getMinimumValuePhrase(mentionedMinGapYet, intersectingGraph, graphSegment))));
        }
        return gapPhrase;
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
    private SPhraseSpec getTrendPhrase(final SeriesSegment seriesSegment, final NPPhraseSpec label) {
        String verbString = null;
        switch (seriesSegment.getGradientType()) {
        case NEGATIVE:
            verbString = Constants.DECREASE;
            break;
        case POSITIVE:
            verbString = Constants.INCREASE;
            break;
        case ZERO:
            verbString = Constants.CONSTANT;
            break;
        default:
            break;
        }
        return this.nlgFactory.createClause(label, this.synonymService.getSynonym(verbString));
    }

    private PhraseElement getSameTrendsSummary(final GraphSegment graphSegment, final boolean intersectingGraph,
            final boolean mentionedMaxGapYet, final boolean mentionedMinGapYet, final List<NPPhraseSpec> labels) {
        final SPhraseSpec sameTrendsPhrase = this.nlgFactory.createClause();

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

}
