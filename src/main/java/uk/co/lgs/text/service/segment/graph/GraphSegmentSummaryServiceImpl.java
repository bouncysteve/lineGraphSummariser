package uk.co.lgs.text.service.segment.graph;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import simplenlg.framework.DocumentElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import uk.co.lgs.model.gradient.GradientType;
import uk.co.lgs.model.segment.graph.GraphSegment;
import uk.co.lgs.model.segment.series.SeriesSegment;
import uk.co.lgs.text.service.label.LabelService;
import uk.co.lgs.text.service.synonym.Constants;
import uk.co.lgs.text.service.synonym.SynonymService;

/**
 * I am responsible for constucting text summaries of segments.
 *
 * @author bouncysteve
 *
 */
@Component
public class GraphSegmentSummaryServiceImpl implements GraphSegmentSummaryService {

    private static final Lexicon LEXICON = Lexicon.getDefaultLexicon();

    private final NLGFactory nlgFactory = new NLGFactory(LEXICON);

    @Autowired
    private LabelService labelService;

    @Autowired
    private SynonymService synonymService;

    @Override
    public DocumentElement getSummary(final GraphSegment graphSegment) {
        final DocumentElement compareSeries = this.nlgFactory.createSentence();

        // Mention higher series at start
        compareSeries.addComponent(describeHigherSeriesAtStart(graphSegment));

        if (graphSegment.getFirstSeriesTrend().equals(graphSegment.getSecondSeriesTrend())) {
            // mention the relative gradients
            compareSeries.addComponent(describeTwoSeriesWithSameGradientType(graphSegment));
        } else {
            compareSeries.addComponent(describeTrendOfInitiallyHigherSeries(graphSegment));

            // Describe the trend of the other series
            final SeriesSegment otherSeries = graphSegment.getHigherSeriesAtStart()
                    .equals(graphSegment.getSeriesSegment(0)) ? graphSegment.getSeriesSegment(1)
                            : graphSegment.getSeriesSegment(0);
            compareSeries.addComponent(describeTrend(otherSeries, graphSegment));

        }
        // Describe the change in the gap.
        compareSeries.addComponent(describeGapChange(graphSegment));
        // (Don't mention higher series at end, as it will be repeated at the
        // start of the next segment), unless this is the last segment of the
        // graph.

        // FIXME: describe the end state of the
        // graph!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        return this.nlgFactory.createSentence(compareSeries);
    }

    private PhraseElement describeTrendOfInitiallyHigherSeries(final GraphSegment graphSegment) {
        // Describe trend of the initially higher series (or the first
        // series if they are tied)
        SeriesSegment higherInitialSeries = graphSegment.getHigherSeriesAtStart();
        if (null == higherInitialSeries) {
            higherInitialSeries = graphSegment.getSeriesSegments().get(0);
        }

        // Else describe them one at time
        return describeTrend(higherInitialSeries, graphSegment);
    }

    private PhraseElement describeHigherSeriesAtStart(final GraphSegment graphSegment) {
        final List<NPPhraseSpec> labels = this.labelService.getLabelsForCommonUse(graphSegment);
        final SeriesSegment higherSeries = graphSegment.getHigherSeriesAtStart();
        final String startTime = graphSegment.getStartTime();
        final SPhraseSpec higherSeriesAtStartPhrase = this.nlgFactory.createClause();
        NLGElement subject;
        NLGElement verb;
        if (null != higherSeries) {
            subject = labels.get(graphSegment.indexOf(higherSeries));
            verb = this.nlgFactory.createVerbPhrase("is higher");
        } else {
            // If getHigherSeriesAtStart() is null then both have the same
            // value.
            subject = this.nlgFactory.createCoordinatedPhrase(labels.get(0), labels.get(1));
            // TODO: can "both" be a modifier that could be applied either
            // before or after the subject?????
            verb = this.nlgFactory.createVerbPhrase("both have value");
            higherSeriesAtStartPhrase
                    .setObject(this.nlgFactory.createNounPhrase(graphSegment.getValueAtIntersection().toString()));
        }

        higherSeriesAtStartPhrase.setSubject(subject);
        final PPPhraseSpec preposition = this.nlgFactory.createPrepositionPhrase("at", startTime);
        higherSeriesAtStartPhrase.setVerb(verb);
        higherSeriesAtStartPhrase.addComplement(preposition);
        return higherSeriesAtStartPhrase;
    }

    private PhraseElement describeGapChange(final GraphSegment graphSegment) {
        // TODO Auto-generated method stub
        return null;
    }

    private PhraseElement describeTrend(final SeriesSegment seriesSegment, final GraphSegment graphSegment) {
        final SPhraseSpec sameTrendPhrase = this.nlgFactory.createClause();
        final NPPhraseSpec subject = this.labelService.getLabelForCommonUse(graphSegment, seriesSegment);
        sameTrendPhrase.setVerb(getVerbForTrend(seriesSegment.getGradientType()));
        sameTrendPhrase.setSubject(subject);
        return sameTrendPhrase;
    }

    private PhraseElement describeTwoSeriesWithSameGradientType(final GraphSegment graphSegment) {
        // FIXME: this needs to describe the relative gradients and say which is
        // steeper, etc....
        final List<NPPhraseSpec> labels = this.labelService.getLabelsForCommonUse(graphSegment);
        final SPhraseSpec sameTrendPhrase = this.nlgFactory.createClause();
        NLGElement subject;
        subject = this.nlgFactory.createCoordinatedPhrase(labels.get(0), labels.get(1));
        sameTrendPhrase.setVerb(getVerbForTrend(graphSegment.getFirstSeriesTrend()));
        sameTrendPhrase.setSubject(subject);
        return sameTrendPhrase;
    }

    private VPPhraseSpec getVerbForTrend(final GradientType trend) {
        VPPhraseSpec trendPhrase = null;
        switch (trend) {
        case NEGATIVE:
            trendPhrase = this.nlgFactory.createVerbPhrase(this.synonymService.getSynonym(Constants.FALL));
            break;
        case POSITIVE:
            trendPhrase = this.nlgFactory.createVerbPhrase(this.synonymService.getSynonym(Constants.RISE));
            break;
        case ZERO:
            trendPhrase = this.nlgFactory.createVerbPhrase(this.synonymService.getSynonym(Constants.CONSTANT));
            break;
        default:
            break;
        }
        return trendPhrase;
    }

}
