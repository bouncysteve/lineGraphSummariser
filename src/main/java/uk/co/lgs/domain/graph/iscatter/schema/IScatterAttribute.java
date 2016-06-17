package uk.co.lgs.domain.graph.iscatter.schema;

/**
 * I am one row of an iScatter schema file.
 * 
 * @see http://michel.wermelinger.ws/chezmichel/iscatter/
 * @author bouncysteve
 *
 */
public interface IScatterAttribute {

    String getId();

    String getName();

    String getDescription();

    String getUnit();

    IScatterType getType();

    IScatterLevel getLevel();
}
