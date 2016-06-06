package uk.co.lgs.text.service.segment.graph;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import simplenlg.framework.DocumentElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import uk.co.lgs.model.segment.graph.GraphSegment;
import uk.co.lgs.text.service.segment.series.SeriesSegmentSummaryService;

@Component
public class GraphSegmentSummaryServiceImpl implements GraphSegmentSummaryService {

    private static final Lexicon LEXICON = Lexicon.getDefaultLexicon();
    private static final NLGFactory NLG_FACTORY = new NLGFactory(LEXICON);

    @Autowired
    private SeriesSegmentSummaryService seriesSegmentSummaryService;

    @Override
    public DocumentElement getSummary(GraphSegment graphSegment) {
        PhraseElement firstSeriesSummary = this.seriesSegmentSummaryService
                .getSummary(graphSegment.getSeriesSegment(0));
        PhraseElement secondSeriesSummary = this.seriesSegmentSummaryService
                .getSummary(graphSegment.getSeriesSegment(1));

        DocumentElement compareSeries = NLG_FACTORY.createSentence();
        compareSeries.addComponent(buildDurationDescription(graphSegment));
        compareSeries.addComponent(firstSeriesSummary);
        compareSeries.addComponent(secondSeriesSummary);

        return NLG_FACTORY.createSentence(compareSeries);
    }

    private SPhraseSpec buildDurationDescription(GraphSegment graphSegment) {
        PPPhraseSpec durationPreposition = NLG_FACTORY.createPrepositionPhrase();

        NPPhraseSpec startTime = NLG_FACTORY.createNounPhrase(graphSegment.getStartTime());
        NPPhraseSpec endTime = NLG_FACTORY.createNounPhrase(graphSegment.getEndTime());
        durationPreposition.addComplement(startTime);
        durationPreposition.setPreposition("between");
        durationPreposition.addComplement(endTime);
        SPhraseSpec durationClause = NLG_FACTORY.createClause();
        durationClause.setObject(durationPreposition);
        return durationClause;
    }

}
