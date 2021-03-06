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
import uk.co.lgs.text.service.synonym.Constants;
import uk.co.lgs.text.service.synonym.SynonymService;
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

    @Autowired
    private SynonymService synonymService;

    private final NLGFactory nlgFactory = new NLGFactory(LEXICON);

    private NPPhraseSpec thisGraph;
    private NPPhraseSpec it;
    private VPPhraseSpec isCalled;

    @Override
    public String getSummary(final GraphModel model) {
        this.thisGraph = this.nlgFactory.createNounPhrase(this.synonymService.getSynonym(Constants.THIS_GRAPH));
        this.it = this.nlgFactory.createNounPhrase(this.synonymService.getSynonym(Constants.IT));
        this.isCalled = this.nlgFactory.createVerbPhrase(this.synonymService.getSynonym(Constants.BE_CALLED));

        // REALISER.setCommaSepCuephrase(true);
        final DocumentElement wholeSummary = this.nlgFactory.createDocument();

        wholeSummary.addComponent(getIntro(model));
        wholeSummary.addComponent(getBody(model));
        // TODO: Find a less hacky way of ensuring commas before these phrases
        return REALISER.realise(wholeSummary).getRealisation().trim()
                .replaceAll(" " + Constants.ITS_MAXIMUM_VALUE, ", " + Constants.ITS_MAXIMUM_VALUE)
                .replaceAll(" " + Constants.ITS_MINIMUM_VALUE, ", " + Constants.ITS_MINIMUM_VALUE);
    }

    // TODO: Don't want this to be a separate paragraph, but there may be
    // multiple sentences...
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

    // FIXME: If start of graph has global maximum or minimum value or gap then
    // say so here!!!!
    private NLGElement describeStartOfGraph(final GraphModel model) {
        final GraphSegment firstSegment = model.getGraphSegments().get(0);
        final List<NPPhraseSpec> labels = this.labelService.getLabelsForCommonUse(model);
        final SeriesSegment higherSeries = firstSegment.getHigherSeriesAtStart();
        final SeriesSegment firstSeriesSegment = firstSegment.getSeriesSegment(0);

        final String startTime = firstSegment.getStartTime();
        final CoordinatedPhraseElement parentPhrase = this.nlgFactory.createCoordinatedPhrase();
        final NLGElement higherSeriesAtStartPhrase;
        final PPPhraseSpec preposition = this.nlgFactory.createPrepositionPhrase(Constants.IN, startTime);

        if (null == higherSeries) {
            higherSeriesAtStartPhrase = describeSeriesWithSameStartValue(labels, firstSeriesSegment, preposition);
        } else {
            higherSeriesAtStartPhrase = describeSeriesWithDifferentStartValues(labels, firstSegment, higherSeries,
                    preposition);
        }
        parentPhrase.addCoordinate(higherSeriesAtStartPhrase);

        if (firstSegment.isGlobalMaximumGapAtSegmentStart()) {
            parentPhrase.addCoordinate(getMaximumGapPhrase(firstSegment));
        } else if (firstSegment.isGlobalMinimumGapAtSegmentStart()) {
            parentPhrase.addCoordinate(getMinimumGapPhrase(firstSegment, model.isIntersecting()));
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Start of graph phrase: {}", REALISER.realiseSentence(higherSeriesAtStartPhrase));
        }
        return parentPhrase;
    }

    private SPhraseSpec getMaximumGapPhrase(final GraphSegment firstSegment) {
        final SPhraseSpec gapPhrase = getGapPhrase(firstSegment);
        gapPhrase.addPostModifier(this.graphSegmentSummaryService.getMaximumValue(false));
        return gapPhrase;
    }

    private SPhraseSpec getMinimumGapPhrase(final GraphSegment firstSegment, final boolean intersecting) {
        final SPhraseSpec gapPhrase = getGapPhrase(firstSegment);
        gapPhrase.addPostModifier(this.graphSegmentSummaryService.getMinimumValue(false, intersecting));
        return gapPhrase;
    }

    private SPhraseSpec getGapPhrase(final GraphSegment firstSegment) {
        final SPhraseSpec gapPhrase = this.nlgFactory.createClause();
        gapPhrase.setSubject(this.synonymService.getSynonym(Constants.THE_GAP_BETWEEN_THEM));
        gapPhrase.setVerb(this.nlgFactory.createVerbPhrase("be"));
        gapPhrase.addComplement(getGapValue(firstSegment, null));
        return gapPhrase;
    }

    private PPPhraseSpec getGapValue(final GraphSegment graphSegment, final String preposition) {
        return this.nlgFactory.createPrepositionPhrase(this.synonymService.getSynonym(preposition),
                this.valueService.formatValueWithUnits(graphSegment.getGapBetweenSeriesStartValues(),
                        graphSegment.getSeriesSegment(0).getUnits()));
    }

    private PhraseElement describeSeriesWithSameStartValue(final List<NPPhraseSpec> labels,
            final SeriesSegment firstSeriesSegment, final PPPhraseSpec preposition) {
        // If getHigherSeriesAtStart() is null then both have the same
        // value.
        final SPhraseSpec sameStartPhrase = this.nlgFactory.createClause();

        final CoordinatedPhraseElement subject = this.nlgFactory.createCoordinatedPhrase(labels.get(0), labels.get(1));

        sameStartPhrase.addPreModifier(preposition);
        subject.addPreModifier(this.synonymService.getSynonym(Constants.BOTH));
        sameStartPhrase.setSubject(subject);
        sameStartPhrase.setVerb(this.nlgFactory.createVerbPhrase(this.synonymService.getSynonym(Constants.HAS)));
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
        final VPPhraseSpec higherVerb = this.nlgFactory
                .createVerbPhrase(this.synonymService.getSynonym(Constants.BE_HIGHER));
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
        final VPPhraseSpec lowerVerb = this.nlgFactory.createVerbPhrase(this.synonymService.getSynonym(Constants.HAS));
        final NPPhraseSpec lowerSeriesValue = this.nlgFactory.createNounPhrase(
                this.valueService.formatValueWithUnits(lowerSeries.getStartValue(), lowerSeries.getUnits()));
        final SPhraseSpec lowerSeriesPhrase = this.nlgFactory.createClause(lowerSeriesNoun, lowerVerb,
                lowerSeriesValue);

        final CoordinatedPhraseElement differentValuesPhrase = this.nlgFactory.createCoordinatedPhrase();
        differentValuesPhrase.addCoordinate(higherSeriesPhrase);
        differentValuesPhrase.addCoordinate(lowerSeriesPhrase);
        // TODO: introduce a conjunction service.
        differentValuesPhrase.setConjunction(this.synonymService.getSynonym(Constants.WHILE));
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
        for (final PhraseElement label : this.labelService.getLabelsForInitialUse(model)) {
            series.addCoordinate(label);
        }
        final SPhraseSpec graphShowsSeries = this.nlgFactory.createClause(graph,
                this.synonymService.getSynonym(Constants.SHOW), series);
        final PPPhraseSpec timeRage = this.nlgFactory.createPrepositionPhrase();
        // TODO: vary the preposition (from/to, until, etc...)
        timeRage.setPreposition(this.synonymService.getSynonym(Constants.BETWEEN));
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
