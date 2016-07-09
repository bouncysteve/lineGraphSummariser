package uk.co.lgs.text.service.segment.graph;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
import uk.co.lgs.model.segment.graph.GraphSegment;
import uk.co.lgs.model.segment.graph.category.GapTrend;
import uk.co.lgs.model.segment.series.SeriesSegment;
import uk.co.lgs.text.service.label.LabelService;
import uk.co.lgs.text.service.synonym.Constants;
import uk.co.lgs.text.service.synonym.SynonymService;
import uk.co.lgs.text.service.value.ValueService;

/**
 * I am responsible for constucting text summaries of segments.
 *
 * @author bouncysteve
 *
 */
@Deprecated
public class OldGraphSegmentSummaryServiceImpl {
    private static final Logger LOG = LoggerFactory.getLogger(OldGraphSegmentSummaryServiceImpl.class);
    private static final Lexicon LEXICON = Lexicon.getDefaultLexicon();
    private static final Realiser REALISER = new Realiser(LEXICON);

    private final NLGFactory nlgFactory = new NLGFactory(LEXICON);

    @Autowired
    private LabelService labelService;

    @Autowired
    private SynonymService synonymService;

    @Autowired
    private ValueService valueService;

    public DocumentElement getSummary(final GraphSegment graphSegment) {
        final DocumentElement compareSeries = this.nlgFactory.createSentence();

        compareSeries.addComponent(describeHigherSeriesAtStart(graphSegment));
        if (graphSegment.getFirstSeriesTrend().equals(graphSegment.getSecondSeriesTrend())) {
            compareSeries.addComponent(describeTwoSeriesWithSameGradientType(graphSegment));
        } else {
            compareSeries.addComponent(describeTrendOfInitiallyHigherSeries(graphSegment));
            compareSeries.addComponent(describeTrendOfInitiallyLowerSeries(graphSegment));
        }
        // Describe the change in the gap.
        compareSeries.addComponent(describeGapChange(graphSegment));
        // (Don't mention higher series at end, as it will be repeated at the
        // start of the next segment), unless this is the last segment of the
        // graph.

        // FIXME: describe the end state of the whole graph!!!!!!!!!!!!!!!!

        return this.nlgFactory.createSentence(compareSeries);
    }

    private PhraseElement describeHigherSeriesAtStart(final GraphSegment graphSegment) {
        final List<NPPhraseSpec> labels = this.labelService.getLabelsForCommonUse(graphSegment);
        final SeriesSegment higherSeries = graphSegment.getHigherSeriesAtStart();
        final String startTime = graphSegment.getStartTime();
        final SPhraseSpec higherSeriesAtStartPhrase = this.nlgFactory.createClause();
        NLGElement subject;
        NLGElement verb;
        NLGElement object = null;

        if (null == higherSeries) {
            // If getHigherSeriesAtStart() is null then both have the same
            // value.
            subject = this.nlgFactory.createCoordinatedPhrase(labels.get(0), labels.get(1));
            // TODO: can "both" be a modifier that could be applied either
            // before or after the subject?????
            verb = this.nlgFactory.createVerbPhrase("both have value");
            final SeriesSegment firstSeriesSegment = graphSegment.getSeriesSegment(0);

            object = this.nlgFactory.createNounPhrase(this.valueService
                    .formatValueWithUnits(firstSeriesSegment.getStartValue(), firstSeriesSegment.getUnits()));
        } else {
            subject = labels.get(graphSegment.indexOf(higherSeries));
            verb = this.nlgFactory.createVerbPhrase("is higher");
        }

        higherSeriesAtStartPhrase.setSubject(subject);
        final PPPhraseSpec preposition = this.nlgFactory.createPrepositionPhrase("at", startTime);
        higherSeriesAtStartPhrase.setVerb(verb);
        if (null != object) {
            higherSeriesAtStartPhrase.setObject(object);
        }
        higherSeriesAtStartPhrase.addComplement(preposition);

        LOG.info(REALISER.realiseSentence(higherSeriesAtStartPhrase));
        return higherSeriesAtStartPhrase;
    }

    private PhraseElement describeGapChange(final GraphSegment graphSegment) {
        // TODO: consider intersection ("crosses")
        final SPhraseSpec gapPhrase = this.nlgFactory.createClause();
        final NPPhraseSpec subject = this.nlgFactory.createNounPhrase("the difference between the two");
        final VPPhraseSpec verb = this.nlgFactory.createVerbPhrase(getVerbForGap(graphSegment.getGapTrend()));
        gapPhrase.setVerb(verb);
        gapPhrase.setSubject(subject);
        return gapPhrase;
    }

    private VPPhraseSpec getVerbForGap(final GapTrend gapTrend) {
        VPPhraseSpec gapVerb;
        String gapString = null;
        switch (gapTrend) {
        case CONVERGING:
            gapString = this.synonymService.getSynonym(Constants.CONVERGE);
            break;
        case DIVERGING:
            gapString = this.synonymService.getSynonym(Constants.DIVERGE);
            break;
        case PARALLEL:
            gapString = this.synonymService.getSynonym(Constants.PARALLEL);
            break;
        default:
            break;
        }
        gapVerb = this.nlgFactory.createVerbPhrase(gapString);
        return gapVerb;
    }

    private PhraseElement describeTrend(final SeriesSegment seriesSegment, final GraphSegment graphSegment) {
        final SPhraseSpec trendPhrase = this.nlgFactory.createClause();
        final NPPhraseSpec subject = this.labelService.getLabelForCommonUse(graphSegment, seriesSegment);
        final NLGElement verb = this.nlgFactory.createVerbPhrase(getVerbForTrend(seriesSegment.getGradientType()));
        trendPhrase.setSubject(subject);
        trendPhrase.setVerb(verb);
        if (GradientType.ZERO.equals(seriesSegment.getGradientType())) {
            final NLGElement object = this.nlgFactory.createNounPhrase(
                    this.valueService.formatValueWithUnits(seriesSegment.getStartValue(), seriesSegment.getUnits()));
            final PPPhraseSpec preposition = this.nlgFactory.createPrepositionPhrase("at", object);
            trendPhrase.addComplement(preposition);
        }
        LOG.info(REALISER.realiseSentence(trendPhrase));
        return trendPhrase;
    }

    private PhraseElement describeTrendOfInitiallyHigherSeries(final GraphSegment graphSegment) {
        SeriesSegment higherInitialSeries = graphSegment.getHigherSeriesAtStart();
        if (null == higherInitialSeries) {
            higherInitialSeries = graphSegment.getSeriesSegments().get(0);
        }
        return describeTrend(higherInitialSeries, graphSegment);
    }

    private NLGElement describeTrendOfInitiallyLowerSeries(final GraphSegment graphSegment) {
        final SeriesSegment otherSeries = graphSegment.getHigherSeriesAtStart().equals(graphSegment.getSeriesSegment(0))
                ? graphSegment.getSeriesSegment(1) : graphSegment.getSeriesSegment(0);
        return describeTrend(otherSeries, graphSegment);
    }

    private PhraseElement describeTwoSeriesWithSameGradientType(final GraphSegment graphSegment) {
        // FIXME: this needs to describe the relative gradients and say which is
        // steeper, etc....
        final List<NPPhraseSpec> labels = this.labelService.getLabelsForCommonUse(graphSegment);
        final SPhraseSpec sameTrendPhrase = this.nlgFactory.createClause();
        NLGElement subject;
        subject = this.nlgFactory.createCoordinatedPhrase(labels.get(0), labels.get(1));
        NLGElement verb;
        verb = this.nlgFactory.createVerbPhrase("both " + getVerbForTrend(graphSegment.getFirstSeriesTrend()));
        sameTrendPhrase.setSubject(subject);
        sameTrendPhrase.setVerb(verb);
        LOG.info(REALISER.realiseSentence(sameTrendPhrase));
        return sameTrendPhrase;
    }

    private String getVerbForTrend(final GradientType trend) {
        String trendString = "";
        switch (trend) {
        case NEGATIVE:
            trendString = this.synonymService.getSynonym(Constants.FALL);
            break;
        case POSITIVE:
            trendString = this.synonymService.getSynonym(Constants.RISE);
            break;
        case ZERO:
            trendString = this.synonymService.getSynonym(Constants.CONSTANT);
            break;
        default:
            break;
        }

        return trendString;
    }

}
