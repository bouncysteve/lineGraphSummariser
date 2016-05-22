package uk.co.lgs.text.service.segment.graph;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import simplenlg.features.Feature;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.DocumentElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;
import uk.co.lgs.model.gradient.GradientType;
import uk.co.lgs.model.segment.graph.GraphSegment;
import uk.co.lgs.model.segment.series.SeriesSegment;
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

        nlgFactory.createClause("between", graphSegment.getStartTime(), graphSegment.getEndTime());
        SeriesSegment firstSeriesSegment = graphSegment.getSeriesSegment(0);
        SeriesSegment secondSeriesSegment = graphSegment.getSeriesSegment(1);

        firstSeriesSegment.getGradient();
        secondSeriesSegment.getGradient();

        SPhraseSpec behaviour = nlgFactory.createClause();
        behaviour.setSubject(firstSeriesSegment.getLabel());
        behaviour.setVerb(gradientTypeDescription(firstSeriesSegment.getGradientType()));
        NPPhraseSpec startValue = nlgFactory.createNounPhrase(firstSeriesSegment.getStartValue() + "");
        NPPhraseSpec endValue = nlgFactory.createNounPhrase(firstSeriesSegment.getEndValue() + "");
        CoordinatedPhraseElement valueChange = nlgFactory.createCoordinatedPhrase(startValue, endValue);
        valueChange.setFeature(Feature.CONJUNCTION, "to");

        if (graphSegment.isIntersecting()) {
            graphSegment.getValueAtIntersection();
        }
        graphSegment.getSegmentCategory();
        return null;
    }

    protected List<DocumentElement> getSegmentSummaries(GraphSegment graphSegment) {
        List<DocumentElement> seriesSegmentSummaries = new ArrayList<DocumentElement>();
        for (SeriesSegment seriesSegment : graphSegment.getSeriesSegments()) {
            seriesSegmentSummaries.add(this.seriesSegmentSummaryService.getSummary(seriesSegment));
        }
        return seriesSegmentSummaries;
    }

    private String gradientTypeDescription(GradientType gradientType) {
        String description = "";
        switch (gradientType) {
        case NEGATIVE:
            description = "fall";
            break;
        case POSITIVE:
            description = "rise";
            break;
        default:
            description = "remain constant";
            break;
        }
        return description;
    }

}
