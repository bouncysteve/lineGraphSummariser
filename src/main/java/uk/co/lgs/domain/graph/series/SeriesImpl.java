package uk.co.lgs.domain.graph.series;

import uk.co.lgs.domain.graph.iscatter.schema.IScatterLevel;
import uk.co.lgs.domain.graph.iscatter.schema.IScatterType;

public class SeriesImpl implements Series {

    String id;
    String name;
    String description;
    String unit;
    IScatterType type;
    IScatterLevel level;

}
