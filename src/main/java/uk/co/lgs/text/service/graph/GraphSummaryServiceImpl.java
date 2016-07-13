package uk.co.lgs.text.service.graph;

import java.util.List;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import simplenlg.features.Feature;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.DocumentElement;
import simplenlg.framework.NLGElement;
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
import uk.co.lgs.model.segment.series.SeriesSegment;
import uk.co.lgs.text.service.label.LabelService;
import uk.co.lgs.text.service.segment.graph.GraphSegmentSummaryService;
import uk.co.lgs.text.service.value.ValueService;

/**
 * I am responsible for generating a text summary of a graph.
 *
 * @author bouncysteve
 *
 */
@Component
public class GraphSummaryServiceImpl implements GraphSummaryService {

    private static final Lexicon LEXICON = Lexicon.getDefaultLexicon();

    private static final Realiser REALISER = new Realiser(LEXICON);

    private static final Logger LOG = LoggerFactory.getLogger(GraphSummaryServiceImpl.class);

    @Autowired
    private GraphSegmentSummaryService graphSegmentSummaryService;

    @Autowired
    private LabelService labelService;

    @Autowired
    private ValueService valueService;

    private final NLGFactory nlgFactory = new NLGFactory(LEXICON);

    private final NPPhraseSpec thisGraph = this.nlgFactory.createNounPhrase("this graph");
    private final NPPhraseSpec it = this.nlgFactory.createNounPhrase("it");
    private final VPPhraseSpec isCalled = this.nlgFactory.createVerbPhrase("is called");

    @Override
    public String getSummary(final GraphModel model) {
        REALISER.setCommaSepCuephrase(true);
        final DocumentElement wholeSummary = this.nlgFactory.createDocument();

        wholeSummary.addComponent(getIntro(model));
        wholeSummary.addComponent(getBody(model));

        return REALISER.realise(wholeSummary).getRealisation().trim();
    }

    private DocumentElement getIntro(final GraphModel model) {
        final DocumentElement intro = this.nlgFactory.createParagraph();
        final SPhraseSpec title = getTitle(model);
        final NPPhraseSpec graph;
        // SimpleNLG does not process referring expressions so we must do it
        // longhand here...
        if (null != title) {
            graph = this.it;
        } else {
            graph = this.thisGraph;
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
        final DocumentElement body = this.nlgFactory.createParagraph();
        final NLGElement startOfGraph = this.nlgFactory.createSentence(describeStartOfGraph(model));
        body.addComponent(startOfGraph);
        for (final DocumentElement segmentSummary : this.graphSegmentSummaryService.getSegmentSummaries(model)) {
            body.addComponent(segmentSummary);
        }
        return body;
    }

    private NLGElement describeStartOfGraph(final GraphModel model) {
        final GraphSegment firstSegment = model.getGraphSegments().get(0);
        final List<NPPhraseSpec> labels = this.labelService.getLabelsForCommonUse(model);
        final SeriesSegment higherSeries = firstSegment.getHigherSeriesAtStart();
        final SeriesSegment firstSeriesSegment = firstSegment.getSeriesSegment(0);

        final String startTime = firstSegment.getStartTime();
        final NLGElement higherSeriesAtStartPhrase;
        final PPPhraseSpec preposition = this.nlgFactory.createPrepositionPhrase("at", startTime);

        if (null == higherSeries) {
            higherSeriesAtStartPhrase = describeSeriesWithSameStartValue(labels, firstSeriesSegment, preposition);
        } else {
            higherSeriesAtStartPhrase = describeSeriesWithDifferentStartValues(labels, firstSegment, higherSeries,
                    preposition);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Start of graph phrase: {}", REALISER.realiseSentence(higherSeriesAtStartPhrase));
        }
        return higherSeriesAtStartPhrase;
    }

    private PhraseElement describeSeriesWithSameStartValue(final List<NPPhraseSpec> labels,
            final SeriesSegment firstSeriesSegment, final PPPhraseSpec preposition) {
        // If getHigherSeriesAtStart() is null then both have the same
        // value.
        final SPhraseSpec sameStartPhrase = this.nlgFactory.createClause();

        final CoordinatedPhraseElement subject = this.nlgFactory.createCoordinatedPhrase(labels.get(0), labels.get(1));

        sameStartPhrase.addPreModifier(preposition);
        subject.addPreModifier("both");
        sameStartPhrase.setSubject(subject);
        sameStartPhrase.setVerb(this.nlgFactory.createVerbPhrase("have"));
        sameStartPhrase.setObject(this.nlgFactory.createNounPhrase(this.valueService
                .formatValueWithUnits(firstSeriesSegment.getStartValue(), firstSeriesSegment.getUnits())));
        if (LOG.isDebugEnabled()) {
            LOG.debug("Same start values phrase: {}", REALISER.realiseSentence(sameStartPhrase));
        }
        return sameStartPhrase;
    }

    private CoordinatedPhraseElement describeSeriesWithDifferentStartValues(final List<NPPhraseSpec> labels,
            final GraphSegment segment, final SeriesSegment higherSeries, final PPPhraseSpec preposition) {
        final NPPhraseSpec higherSeriesNoun = labels.get(segment.indexOf(higherSeries));
        final VPPhraseSpec higherVerb = this.nlgFactory.createVerbPhrase("is higher");
        final NPPhraseSpec higherSeriesValue = this.nlgFactory.createNounPhrase(
                this.valueService.formatValueWithUnits(higherSeries.getStartValue(), higherSeries.getUnits()));
        higherSeriesValue.addPreModifier("with");
        // TODO: introduce randomisation that either uses this premodifier on
        // the verb or the whole phrase.
        higherSeriesValue.addPostModifier(preposition);

        final SPhraseSpec higherSeriesPhrase = this.nlgFactory.createClause(higherSeriesNoun, higherVerb,
                higherSeriesValue);

        final SeriesSegment lowerSeries = segment.getSeriesSegment(1 - segment.indexOf(higherSeries));
        final NPPhraseSpec lowerSeriesNoun = labels.get(segment.indexOf(lowerSeries));
        final VPPhraseSpec lowerVerb = this.nlgFactory.createVerbPhrase("have");
        final NPPhraseSpec lowerSeriesValue = this.nlgFactory.createNounPhrase(
                this.valueService.formatValueWithUnits(lowerSeries.getStartValue(), lowerSeries.getUnits()));
        final SPhraseSpec lowerSeriesPhrase = this.nlgFactory.createClause(lowerSeriesNoun, lowerVerb,
                lowerSeriesValue);

        final CoordinatedPhraseElement differentValuesPhrase = this.nlgFactory.createCoordinatedPhrase();
        differentValuesPhrase.addCoordinate(higherSeriesPhrase);
        differentValuesPhrase.addCoordinate(lowerSeriesPhrase);
        // TODO: introduce a conjunction service.
        differentValuesPhrase.setConjunction("while");
        if (LOG.isDebugEnabled()) {
            LOG.debug("Diiferent start values phrase: {}", REALISER.realiseSentence(differentValuesPhrase));
        }
        return differentValuesPhrase;
    }

    private SPhraseSpec getTitle(final GraphModel model) {
        String title = model.getTitle();
        if (StringUtils.isNotEmpty(title)) {
            title = StringUtils.chompLast(title, ".");
            return this.nlgFactory.createClause(this.thisGraph, this.isCalled, "\"" + title + "\"");
        }
        return null;
    }

    private SPhraseSpec getLabelsAndTimeScale(final GraphModel model, final NPPhraseSpec graph) {
        final CoordinatedPhraseElement series = this.nlgFactory.createCoordinatedPhrase();
        for (final PhraseElement label : this.labelService.getLabelsForInitialUse(model.getLabels())) {
            series.addCoordinate(label);
        }
        final SPhraseSpec graphShowsSeries = this.nlgFactory.createClause(graph, "show", series);
        final PPPhraseSpec timeRage = this.nlgFactory.createPrepositionPhrase();
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
