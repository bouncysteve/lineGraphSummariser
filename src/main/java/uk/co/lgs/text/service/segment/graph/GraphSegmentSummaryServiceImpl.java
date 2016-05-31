package uk.co.lgs.text.service.segment.graph;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import simplenlg.framework.DocumentElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;
import uk.co.lgs.model.segment.graph.GraphSegment;
import uk.co.lgs.text.service.segment.series.SeriesSegmentSummaryService;

@Component
public class GraphSegmentSummaryServiceImpl implements GraphSegmentSummaryService {

    private static Lexicon lexicon = Lexicon.getDefaultLexicon();
    private static NLGFactory nlgFactory = new NLGFactory(lexicon);
    private static Realiser realiser = new Realiser(lexicon);

    @Autowired
    private SeriesSegmentSummaryService seriesSegmentSummaryService;

    @Override
    public DocumentElement getSummary(GraphSegment graphSegment) {
        this.seriesSegmentSummaryService.getSummary(graphSegment.getSeriesSegment(0));
        this.seriesSegmentSummaryService.getSummary(graphSegment.getSeriesSegment(1));

        DocumentElement compareSeries = nlgFactory.createSentence();
        PPPhraseSpec prep_1 = nlgFactory.createPrepositionPhrase();

        NPPhraseSpec startTime = nlgFactory.createNounPhrase(graphSegment.getStartTime());
        NPPhraseSpec endTime = nlgFactory.createNounPhrase(graphSegment.getEndTime());
        prep_1.addComplement(startTime);
        prep_1.setPreposition("between");
        prep_1.addComplement(endTime);
        SPhraseSpec clause_1 = nlgFactory.createClause();
        clause_1.setObject(prep_1);

        compareSeries.addComponent(clause_1);

        // TODO: get actual series descriptions
        NLGElement descriptionOfSeriesSegments = nlgFactory.createStringElement("nothing happened");
        compareSeries.addComponent(descriptionOfSeriesSegments);

        /*
         * SeriesSegment firstSeriesSegment = graphSegment.getSeriesSegment(0);
         * SeriesSegment secondSeriesSegment = graphSegment.getSeriesSegment(1);
         * 
         * firstSeriesSegment.getGradient(); secondSeriesSegment.getGradient();
         * 
         * SPhraseSpec behaviour = nlgFactory.createClause();
         * behaviour.setSubject(firstSeriesSegment.getLabel());
         * behaviour.setVerb(gradientTypeDescription(firstSeriesSegment.
         * getGradientType())); NPPhraseSpec startValue =
         * nlgFactory.createNounPhrase(firstSeriesSegment.getStartValue() + "");
         * NPPhraseSpec endValue =
         * nlgFactory.createNounPhrase(firstSeriesSegment.getEndValue() + "");
         * CoordinatedPhraseElement valueChange =
         * nlgFactory.createCoordinatedPhrase(startValue, endValue);
         * valueChange.setFeature(Feature.CONJUNCTION, "to");
         * 
         * if (graphSegment.isIntersecting()) {
         * graphSegment.getValueAtIntersection(); }
         * graphSegment.getSegmentCategory();
         */
        return nlgFactory.createSentence(compareSeries);
    }

}
