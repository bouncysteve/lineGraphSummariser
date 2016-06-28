package uk.co.lgs.text.service.graph;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import simplenlg.features.Feature;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.DocumentElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.PhraseElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.english.Realiser;
import uk.co.lgs.model.graph.GraphModel;
import uk.co.lgs.model.segment.graph.GraphSegment;
import uk.co.lgs.text.service.label.LabelService;
import uk.co.lgs.text.service.segment.graph.GraphSegmentSummaryService;

/**
 * I am responsible for generating a text summary of a graph.
 *
 * @author bouncysteve
 *
 */
@Component
public class GraphSummaryServiceImpl implements GraphSummaryService {

    private static final Lexicon LEXICON = Lexicon.getDefaultLexicon();
    private static final NLGFactory NLG_FACTORY = new NLGFactory(LEXICON);
    private static final Realiser REALISER = new Realiser(LEXICON);

    /*
     * It should be OK for these to be static, but they must not be modified!!!
     */
    private static final NPPhraseSpec GRAPH = NLG_FACTORY.createNounPhrase("this graph");
    private static final NPPhraseSpec IT = NLG_FACTORY.createNounPhrase("it");
    private static final VPPhraseSpec CALL = NLG_FACTORY.createVerbPhrase("is called");

    @Autowired
    private GraphSegmentSummaryService graphSegmentSummaryService;

    @Autowired
    private LabelService labelService;

    @Override
    public String getSummary(final GraphModel model) {
        REALISER.setCommaSepCuephrase(true);
        final DocumentElement wholeSummary = NLG_FACTORY.createDocument();

        wholeSummary.addComponent(getIntro(model));
        wholeSummary.addComponent(getBody(model));

        return REALISER.realise(wholeSummary).getRealisation().trim();
    }

    private DocumentElement getIntro(final GraphModel model) {
        final DocumentElement intro = NLG_FACTORY.createParagraph();
        final SPhraseSpec title = getTitle(model);
        final NPPhraseSpec graph;
        // SimpleNLG does not process referring expressions so we must do it
        // longhand here...
        if (null != title) {
            graph = IT;
        } else {
            graph = GRAPH;
        }
        final SPhraseSpec labels = getLabelsAndTimeScale(model, graph);
        if (null != title) {
            // This should put a comma after the phrase, but doesn't seem to...
            title.setFeature(Feature.CUE_PHRASE, true);
            intro.addComponent(title);
        }
        if (null != labels) {
            intro.addComponent(labels);
        }
        return intro;
    }

    private DocumentElement getBody(final GraphModel model) {
        final DocumentElement body = NLG_FACTORY.createParagraph();
        for (final DocumentElement sentence : getSegmentSummaries(model)) {
            body.addComponent(sentence);
        }
        return body;
    }

    protected List<DocumentElement> getSegmentSummaries(final GraphModel model) {
        final List<DocumentElement> segmentSummaries = new ArrayList<>();
        for (final GraphSegment graphSegment : model.getGraphSegments()) {
            segmentSummaries.add(this.graphSegmentSummaryService.getSummary(graphSegment));
        }
        return segmentSummaries;
    }

    protected SPhraseSpec getTitle(final GraphModel model) {
        if (StringUtils.isNotEmpty(model.getTitle())) {
            return NLG_FACTORY.createClause(GRAPH, CALL, model.getTitle());
        }
        return null;
    }

    protected SPhraseSpec getLabelsAndTimeScale(final GraphModel model, final NPPhraseSpec graph) {
        final CoordinatedPhraseElement series = NLG_FACTORY.createCoordinatedPhrase();
        for (final PhraseElement label : this.labelService.getLabelsForInitialUse(model.getLabels())) {
            series.addCoordinate(label);
        }
        final SPhraseSpec graphShowsSeries = NLG_FACTORY.createClause(graph, "show", series);
        final PPPhraseSpec timeRage = NLG_FACTORY.createPrepositionPhrase();
        // TODO: vary the preposition (from/to, until, etc...)
        timeRage.setPreposition("between");
        final List<GraphSegment> graphSegments = model.getGraphSegments();
        if (!graphSegments.isEmpty()) {
            timeRage.addComplement(graphSegments.get(0).getStartTime());
            timeRage.addComplement(graphSegments.get(graphSegments.size() - 1).getEndTime());
            graphShowsSeries.addComplement(timeRage);
            return graphShowsSeries;
        }
        return null;
    }
}
