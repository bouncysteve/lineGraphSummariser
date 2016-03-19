package uk.co.lgs.domain.graph.iscatter.schema;

import java.util.List;

public interface Schema {
	
	int getAttributesCount();

	IScatterAttribute getAttribute(int position);

	List<IScatterAttribute> getAttributes();
}
