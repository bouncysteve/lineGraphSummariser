package uk.co.lgs.domain.graph.iscatter.schema;

import java.util.List;

/**
 * I represent the contents of an entire iScatter schema.csv file.
 * 
 * @see http://michel.wermelinger.ws/chezmichel/iscatter/
 * @author bouncysteve
 *
 */
public interface IScatterSchema {

    int getAttributesCount();

    IScatterAttribute getAttribute(int position);

    List<IScatterAttribute> getAttributes();

    String getDescription(String id);

    String getUnit(String seriesId);

}
