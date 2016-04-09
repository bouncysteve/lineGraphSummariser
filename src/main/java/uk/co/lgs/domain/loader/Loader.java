package uk.co.lgs.domain.loader;

import java.io.File;

import uk.co.lgs.domain.graph.GraphData;
import uk.co.lgs.domain.loader.exception.LoaderException;

public interface Loader {

    GraphData getGraph(File parentDir) throws LoaderException;
}