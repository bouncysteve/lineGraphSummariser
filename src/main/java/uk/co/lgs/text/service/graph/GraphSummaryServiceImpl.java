package uk.co.lgs.text.service.graph;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.DocumentElement;
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

    private static Lexicon lexicon = Lexicon.getDefaultLexicon();
    private static NLGFactory nlgFactory = new NLGFactory(lexicon);
    private static Realiser realiser = new Realiser(lexicon);

    @Autowired
    private GraphSegmentSummaryService graphSegmentSummaryService;

    private NPPhraseSpec graph = nlgFactory.createNounPhrase("this graph");

    private VPPhraseSpec call = nlgFactory.createVerbPhrase("is called");
    private VPPhraseSpec show = nlgFactory.createVerbPhrase("show");
    private NPPhraseSpec title = nlgFactory.createNounPhrase("title");

    /*
     * NLGElement getCannedSentence() { this.subject.addModifier("old");
     * this.p.setSubject(this.subject);
     * 
     * this.verb.addPreModifier("carefully");// Adverb phrase, passed as a //
     * string this.p.setVerb(this.verb);
     * 
     * this.p.setObject(this.object);
     * 
     * this.pp.addComplement(this.target); this.pp.setPreposition("for");
     * this.p.addComplement(this.pp);
     * 
     * this.p.addComplement("despite the earliness of the hour"); //
     * Prepositional // phrase, // string NLGElement s1 =
     * nlgFactory.createSentence("Steve likes coffee"); // System.out.println(
     * "1: " + realiser.realiseSentence(s1)); return s1; }
     */

    List<DocumentElement> sentences = new ArrayList<DocumentElement>();

    @Override
    public String getSummary(GraphModel model) {
        SPhraseSpec title = getTitle(model);
        if (null != title) {
            this.sentences.add(nlgFactory.createSentence(title));
        }
        SPhraseSpec labels = getLabelsAndTimeScale(model);
        if (null != labels) {
            this.sentences.add(nlgFactory.createSentence(labels));
        }
        List<DocumentElement> segmentSummaries = getSegmentSummaries(model);
        this.sentences.addAll(segmentSummaries);
        DocumentElement par1 = nlgFactory.createParagraph(this.sentences);

        return realiser.realise(par1).getRealisation().trim();
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
            return nlgFactory.createClause(this.graph, this.call, model.getTitle());
        }
        return null;
    }

    protected SPhraseSpec getLabelsAndTimeScale(GraphModel model) {
        CoordinatedPhraseElement series = nlgFactory.createCoordinatedPhrase();
        String timeLabel = "";
        for (String label : model.getLabels()) {
            if (StringUtils.isEmpty(timeLabel)) {
                timeLabel = label;
            } else {
                series.addCoordinate(label);
            }
        }
        SPhraseSpec graphShowsSeries = nlgFactory.createClause(this.graph, "show", series);
        PPPhraseSpec timeRage = nlgFactory.createPrepositionPhrase();
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
