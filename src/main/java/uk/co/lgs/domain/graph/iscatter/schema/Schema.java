package uk.co.lgs.domain.graph.iscatter.schema;

import java.util.List;

public interface Schema {
	
	int getSeriesCount();

	IScatterAttribute getAttribute(int position);

	List<IScatterAttribute> getAttributes();
}
