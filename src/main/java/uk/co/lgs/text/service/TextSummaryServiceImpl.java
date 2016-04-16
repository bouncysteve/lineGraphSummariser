package uk.co.lgs.text.service;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;
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

@Component
public class TextSummaryServiceImpl implements TextSummaryService {

    private static Lexicon lexicon = Lexicon.getDefaultLexicon();
    private static NLGFactory nlgFactory = new NLGFactory(lexicon);
    private static Realiser realiser = new Realiser(lexicon);

    NPPhraseSpec graph = nlgFactory.createNounPhrase("this graph");

    VPPhraseSpec call = nlgFactory.createVerbPhrase("is called");
    VPPhraseSpec show = nlgFactory.createVerbPhrase("show");
    NPPhraseSpec title = nlgFactory.createNounPhrase("title");

    List<DocumentElement> sentences = new ArrayList<DocumentElement>();

    @Override
    public String getSummary(GraphModel model) {
        SPhraseSpec title = getTitle(model);
        if (null != title) {
            this.sentences.add(nlgFactory.createSentence(title));
        }
        this.sentences.add(nlgFactory.createSentence((this.getLabelsAndTimeScale(model))));
        List<DocumentElement> segmentSummaries = getSegmentSummaries(model);
        this.sentences.addAll(segmentSummaries);
        DocumentElement par1 = nlgFactory.createParagraph(this.sentences);

        return realiser.realise(par1).getRealisation();
    }

    protected List<DocumentElement> getSegmentSummaries(GraphModel model) {
        List<DocumentElement> segmentSummaries = new ArrayList<DocumentElement>();
        for (GraphSegment graphSegment : model.getGraphSegments()) {
            segmentSummaries.add(summariseSegment(graphSegment));
        }
        return segmentSummaries;
    }

    protected DocumentElement summariseSegment(GraphSegment graphSegment) {
        // TODO Auto-generated method stub
        return null;
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
        timeRage.addComplement(graphSegments.get(0).getStartTime());
        timeRage.addComplement(graphSegments.get(graphSegments.size() - 1).getEndTime());
        graphShowsSeries.addComplement(timeRage);
        return graphShowsSeries;
    }

}
