package uk.co.lgs.text.service.segment.graph;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import simplenlg.framework.DocumentElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.SPhraseSpec;
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
            // Describe trend of the initially higher series (or the first
            // series if they are tied)
            SeriesSegment higherInitialSeries = graphSegment.getHigherSeriesAtStart();
            if (null == higherInitialSeries) {
                higherInitialSeries = graphSegment.getSeriesSegments().get(0);
            }
            compareSeries.addComponent(describeTrend(higherInitialSeries));

            // Describe the trend of the other series
            final SeriesSegment otherSeries = higherInitialSeries.equals(graphSegment.getSeriesSegment(0))
                    ? graphSegment.getSeriesSegment(1) : graphSegment.getSeriesSegment(0);
            compareSeries.addComponent(describeTrend(otherSeries));
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

    private PhraseElement describeHigherSeriesAtStart(final GraphSegment graphSegment) {
        final List<PhraseElement> labels = this.labelService.getLabelsForCommonUse(graphSegment);
        final SeriesSegment higherSeries = graphSegment.getHigherSeriesAtStart();
        final String startTime = graphSegment.getStartTime();
        // If getHigherSeriesAtStart() is null then both have the same value.
        PhraseElement higherSeriesLabel = this.nlgFactory.createNounPhrase("both");
        if (null != higherSeries) {
            higherSeriesLabel = labels.get(graphSegment.indexOf(higherSeries));
        }
        final SPhraseSpec higherSeriesPhrase = this.nlgFactory.createClause();
        higherSeriesPhrase.setSubject(higherSeriesLabel);
        higherSeriesPhrase.setVerb("is higher in");
        higherSeriesPhrase.setObject(startTime);
        return higherSeriesPhrase;
    }

    private PhraseElement describeGapChange(final GraphSegment graphSegment) {
        // TODO Auto-generated method stub
        return null;
    }

    private PhraseElement describeTrend(final SeriesSegment higherInitialSeries) {
        // TODO Auto-generated method stub
        return null;
    }

    private PhraseElement describeTwoSeriesWithSameGradientType(final GraphSegment graphSegment) {
        final List<PhraseElement> labels = this.labelService.getLabelsForCommonUse(graphSegment);
        final SPhraseSpec sameTrendPhrase = this.nlgFactory.createClause();
        sameTrendPhrase.setSubject(labels.get(0));
        sameTrendPhrase.setSubject(labels.get(1));
        sameTrendPhrase.setVerb(getPhraseForTrend(graphSegment.getFirstSeriesTrend()));

        return sameTrendPhrase;
    }

    private PhraseElement getPhraseForTrend(final GradientType trend) {
        PhraseElement trendPhrase = null;
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
