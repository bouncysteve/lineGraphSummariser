package uk.co.lgs.text.service.graph;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.DocumentElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.english.Realiser;
import uk.co.lgs.model.graph.GraphModel;
import uk.co.lgs.model.segment.graph.GraphSegment;
import uk.co.lgs.text.service.segment.graph.GraphSegmentSummaryService;

@Component
public class GraphSummaryServiceImpl implements GraphSummaryService {

    private static final Lexicon LEXICON = Lexicon.getDefaultLexicon();
    private static final NLGFactory NLG_FACTORY = new NLGFactory(LEXICON);
    private static final Realiser REALISER = new Realiser(LEXICON);

    /*
     * It should be OK for these to be static, but they must not be modified!!!
     */
    private static final NPPhraseSpec GRAPH = NLG_FACTORY.createNounPhrase("this graph");
    private static final VPPhraseSpec CALL = NLG_FACTORY.createVerbPhrase("is called");
    private static final VPPhraseSpec SHOW = NLG_FACTORY.createVerbPhrase("show");
    private static final NPPhraseSpec TITLE = NLG_FACTORY.createNounPhrase("title");

    @Autowired
    private GraphSegmentSummaryService graphSegmentSummaryService;

    @Override
    public String getSummary(GraphModel model) {
        DocumentElement wholeSummary = NLG_FACTORY.createDocument();

        wholeSummary.addComponent(getIntro(model));
        wholeSummary.addComponent(getBody(model));
        wholeSummary.addComponent(getAnalysis(model));

        return REALISER.realise(wholeSummary).getRealisation().trim();
    }

    /**
     * Eventually this will generate a final paragraph describing the overall
     * message of the graph.
     * 
     * @param model
     * @return
     */
    private NLGElement getAnalysis(GraphModel model) {
        // TODO Auto-generated method stub
        return null;
    }

    private DocumentElement getIntro(GraphModel model) {
        DocumentElement intro = NLG_FACTORY.createParagraph();
        intro.addComponent(getTitle(model));
        SPhraseSpec labels = getLabelsAndTimeScale(model);
        if (null != labels) {
            intro.addComponent(NLG_FACTORY.createSentence(labels));
        }
        return intro;
    }

    private DocumentElement getBody(GraphModel model) {
        DocumentElement body = NLG_FACTORY.createParagraph();
        for (DocumentElement sentence : getSegmentSummaries(model)) {
            body.addComponent(sentence);
        }
        return body;
    }

    protected List<DocumentElement> getSegmentSummaries(GraphModel model) {
        List<DocumentElement> segmentSummaries = new ArrayList<DocumentElement>();
        for (GraphSegment graphSegment : model.getGraphSegments()) {
            segmentSummaries.add(this.graphSegmentSummaryService.getSummary(graphSegment));
        }
        return segmentSummaries;
    }

    protected SPhraseSpec getTitle(GraphModel model) {
        if (StringUtils.isNotEmpty(model.getTitle())) {
            return NLG_FACTORY.createClause(GRAPH, CALL, model.getTitle());
        }
        return null;
    }

    protected SPhraseSpec getLabelsAndTimeScale(GraphModel model) {
        CoordinatedPhraseElement series = NLG_FACTORY.createCoordinatedPhrase();
        String timeLabel = "";
        for (String label : model.getLabels()) {
            if (StringUtils.isEmpty(timeLabel)) {
                timeLabel = label;
            } else {
                series.addCoordinate(label);
            }
        }
        SPhraseSpec graphShowsSeries = NLG_FACTORY.createClause(GRAPH, "show", series);
        PPPhraseSpec timeRage = NLG_FACTORY.createPrepositionPhrase();
        timeRage.setPreposition("between");
        List<GraphSegment> graphSegments = model.getGraphSegments();
        if (!graphSegments.isEmpty()) {
            timeRage.addComplement(graphSegments.get(0).getStartTime());
            timeRage.addComplement(graphSegments.get(graphSegments.size() - 1).getEndTime());
            graphShowsSeries.addComplement(timeRage);
            return graphShowsSeries;
        }
        return null;
    }
}
