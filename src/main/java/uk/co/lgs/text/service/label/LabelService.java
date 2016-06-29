package uk.co.lgs.text.service.label;

import java.util.List;

import simplenlg.phrasespec.NPPhraseSpec;
import uk.co.lgs.model.graph.GraphModel;
import uk.co.lgs.model.segment.graph.GraphSegment;
import uk.co.lgs.model.segment.series.SeriesSegment;

/**
 * @author bouncysteve
 *
 */
public interface LabelService {

    /**
     * In the first sentence of the description the full label should be used,
     * including the short form in brackets for use in the rest of the text.
     *
     * @param graphModel
     *            the graph
     * @return a list of labels for use in simpleNLG.
     */
    List<NPPhraseSpec> getLabelsForInitialUse(GraphModel graphModel);

    /**
     * In the first sentence of the description the full label should be used,
     * including the short form in brackets for use in the rest of the text.
     *
     * @param graphSegment
     *            the graph segmment
     * @return a list of labels for use in simpleNLG.
     */
    List<NPPhraseSpec> getLabelsForInitialUse(GraphSegment graphSegment);

    /**
     * In the first sentence of the description the full label should be used,
     * including the short form in brackets for use in the rest of the text.
     *
     * @param labels
     *            a list of labels, the first one is the time series label.
     * @return
     */
    List<NPPhraseSpec> getLabelsForInitialUse(List<String> labels);

    /**
     * In the main body of the text a series' label may be too long to repeat.
     * This method attempts to generate a shorter form. If it is not possible to
     * generate a shorter form then the original label is used instead.
     *
     * @param graphSegment
     * @return
     */
    List<NPPhraseSpec> getLabelsForCommonUse(GraphSegment graphSegment);

    /**
     * Return the label for the given seriesSegment.
     *
     * @param graphSegment
     * @param seriesSegment
     * @return
     */
    NPPhraseSpec getLabelForCommonUse(GraphSegment graphSegment, SeriesSegment seriesSegment);
}
