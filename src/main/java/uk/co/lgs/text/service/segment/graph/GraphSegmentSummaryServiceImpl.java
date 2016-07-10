package uk.co.lgs.text.service.segment.graph;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        List<NPPhraseSpec> labels = null;
        final boolean intersectingGraph = model.isIntersecting();

        final List<DocumentElement> segmentSummaries = new ArrayList<>();
        boolean mentionedMaxGapYet = false;
        boolean mentionedMinGapYet = false;
        for (final GraphSegment graphSegment : model.getGraphSegments()) {
            if (null == labels) {
                // TODO: introduce a method which retrieves the labels from the
                // model (they don't change per segment).
                labels = this.labelService.getLabelsForCommonUse(graphSegment);
            }
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

        summary.addComponent(getSameValueAtStartSentence(graphSegment, intersectingGraph, mentionedMaxGapYet,
                mentionedMinGapYet, labels));

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

    private NLGElement getSameValueAtStartSentence(final GraphSegment graphSegment, final boolean intersectingGraph,
            final boolean mentionedMaxGapYet, final boolean mentionedMinGapYet, final List<NPPhraseSpec> labels) {
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
            final PPPhraseSpec preposition = getStartTimePhrase(Constants.AT, graphSegment);
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
        NLGElement oppositeTrendsPhrase = null;
        switch (graphSegment.getGapTrend()) {
        case CONVERGING:
            oppositeTrendsPhrase = getOppositeTrendsConvergingPhrase(graphSegment, intersectingGraph,
                    mentionedMaxGapYet, mentionedMinGapYet, labels);
            break;
        case DIVERGING:
            oppositeTrendsPhrase = getOppositeTrendsDivergingPhrase(graphSegment, intersectingGraph, mentionedMaxGapYet,
                    mentionedMinGapYet, labels);
            break;
        case PARALLEL:
            oppositeTrendsPhrase = getOppositeTrendsConstantGapPhrase(graphSegment, intersectingGraph,
                    mentionedMaxGapYet, mentionedMinGapYet, labels);
            break;
        default:
            break;
        }
        LOG.info(REALISER.realiseSentence(oppositeTrendsPhrase));
        return oppositeTrendsPhrase;
    }

    private NLGElement getOppositeTrendsConstantGapPhrase(final GraphSegment graphSegment,
            final boolean intersectingGraph, final boolean mentionedMaxGapYet, final boolean mentionedMinGapYet,
            final List<NPPhraseSpec> labels) {
        // TODO Auto-generated method stub
        return null;
    }

    private NLGElement getOppositeTrendsDivergingPhrase(final GraphSegment graphSegment,
            final boolean intersectingGraph, final boolean mentionedMaxGapYet, final boolean mentionedMinGapYet,
            final List<NPPhraseSpec> labels) {
        final int higherSeriesIndex = graphSegment.indexOf(graphSegment.getHigherSeriesAtStart());
        final CoordinatedPhraseElement oppositeTrendPhrase = this.nlgFactory.createCoordinatedPhrase();

        oppositeTrendPhrase.addPreModifier(getEndTimePhrase(Constants.UNTIL, graphSegment));
        oppositeTrendPhrase
                .addCoordinate(getTrendPhrase(graphSegment.getHigherSeriesAtStart(), labels.get(higherSeriesIndex)));
        oppositeTrendPhrase.setConjunction(this.synonymService.getSynonym(Constants.BUT));
        oppositeTrendPhrase.addCoordinate(getTrendPhrase(graphSegment.getSeriesSegment(1 - higherSeriesIndex),
                labels.get(1 - higherSeriesIndex)));
        // TODO: use simplenlg properly here
        oppositeTrendPhrase.addPostModifier(", so the gap between them increases");
        return oppositeTrendPhrase;
    }

    private SPhraseSpec getTrendPhrase(final SeriesSegment seriesSegment, final NPPhraseSpec label) {
        String verbString = null;
        switch (seriesSegment.getGradientType()) {
        case NEGATIVE:
            verbString = this.synonymService.getSynonym(Constants.FALL);
            break;
        case POSITIVE:
            verbString = this.synonymService.getSynonym(Constants.RISE);
            break;
        case ZERO:
            verbString = this.synonymService.getSynonym(Constants.CONSTANT);
            break;
        default:
            break;
        }
        return this.nlgFactory.createClause(label, verbString);
    }

    private NLGElement getOppositeTrendsConvergingPhrase(final GraphSegment graphSegment,
            final boolean intersectingGraph, final boolean mentionedMaxGapYet, final boolean mentionedMinGapYet,
            final List<NPPhraseSpec> labels) {
        // TODO Auto-generated method stub
        return null;
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
        final PPPhraseSpec p = this.nlgFactory.createPrepositionPhrase(this.synonymService.getSynonym(preposition),
                graphSegment.getEndTime());
        return p;
    }

    private PPPhraseSpec getStartTimePhrase(final String preposition, final GraphSegment graphSegment) {
        return this.nlgFactory.createPrepositionPhrase(this.synonymService.getSynonym(preposition),
                graphSegment.getStartTime());
    }

}
