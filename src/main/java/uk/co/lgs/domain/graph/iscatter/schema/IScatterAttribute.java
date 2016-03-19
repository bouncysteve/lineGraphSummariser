package uk.co.lgs.domain.graph.iscatter.schema;

public interface IScatterAttribute {

	String getId();
	String getName();
	String getDescription();
	String getUnit();
	IScatterType getType();
	IScatterLevel getLevel();
}
