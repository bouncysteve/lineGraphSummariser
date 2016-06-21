package uk.co.lgs.text.service.segment.graph;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.MapConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.DocumentElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import uk.co.lgs.model.segment.graph.GraphSegment;
import uk.co.lgs.text.service.graph.PropertyNames;
import uk.co.lgs.text.service.segment.series.SeriesSegmentSummaryService;

@Component
public class GraphSegmentSummaryServiceImpl implements GraphSegmentSummaryService {

    private static final Lexicon LEXICON = Lexicon.getDefaultLexicon();
    private static final NLGFactory NLG_FACTORY = new NLGFactory(LEXICON);

    @Autowired
    private SeriesSegmentSummaryService seriesSegmentSummaryService;

    @Autowired
    private SegmentDurationDescriberService segmentDurationDescriberService;

    @Override
    public DocumentElement getSummary(GraphSegment graphSegment) {
        DocumentElement compareSeries = NLG_FACTORY.createSentence();
        compareSeries.addComponent(this.segmentDurationDescriberService.buildDurationDescription(graphSegment));

        compareSeries.addComponent(describeBehaviour(graphSegment));

        return NLG_FACTORY.createSentence(compareSeries);
    }

    private NLGElement describeBehaviour(GraphSegment graphSegment) {
        CoordinatedPhraseElement behaviour = NLG_FACTORY.createCoordinatedPhrase();
        List<Configuration> propertyList = constructConfig(graphSegment);
        Configuration firstSeriesConfig = propertyList.get(0);
        Configuration secondSeriesConfig = propertyList.get(1);
        behaviour.addCoordinate(this.seriesSegmentSummaryService.getSummary(graphSegment.getSeriesSegment(0),
                graphSegment.getSeriesSegment(1), firstSeriesConfig));
        behaviour.addCoordinate(this.seriesSegmentSummaryService.getSummary(graphSegment.getSeriesSegment(1),
                graphSegment.getSeriesSegment(0), secondSeriesConfig));
        return behaviour;
    }

    private List<Configuration> constructConfig(GraphSegment graphSegment) {
        Configuration firstSeriesConfig = new MapConfiguration(new HashMap<String, Object>());
        Configuration secondSeriesConfig = new MapConfiguration(new HashMap<String, Object>());
        if (graphSegment.isIntersecting()) {
            switch (graphSegment.getGraphSegmentGradientCategory()) {
            case ZERO_ZERO_INTERSECTING:
                // Both series have the same value throughout.
                firstSeriesConfig.setProperty(PropertyNames.BOTH_SAME, Boolean.TRUE.toString());
                break;
            case ZERO_NEGATIVE_INTERSECTING:
                // Second series falls below the value of the first series,
                // which
                // remains constant at...
                break;
            case ZERO_POSITIVE_INTERSECTING:
                // Second series rises above the value of the first series,
                // which
                // remains constant at...
                break;
            case NEGATIVE_NEGATIVE_INTERSECTING:
                // Need to know which one is steeper (or has higher initial
                // value)
                // to know which is undertaking
                break;
            case NEGATIVE_POSITIVE_INTERSECTING:
                // S1 increases to y, while S2 decreases to a lower value, x.
                break;
            case POSITIVE_NEGATIVE_INTERSECTING:
                // S1 decreases to y, while S2 increases to a higher value, x.
                break;
            case POSITIVE_POSITIVE_INTERSECTING:
                // Need to know which one is steeper (or has lower initial value
                // to
                // know which is overtaking
                break;
            case NEGATIVE_ZERO_INTERSECTING:
                // S1 decreases to x, below the value of y, which S2 holds
                // until...
                break;
            case POSITIVE_ZERO_INTERSECTING:
                // S1 increases to x, above the value of y, which S2 holds
                // until...
                break;
            default:
                break;
            }
        }
        return Arrays.asList(firstSeriesConfig, secondSeriesConfig);
    }

}
