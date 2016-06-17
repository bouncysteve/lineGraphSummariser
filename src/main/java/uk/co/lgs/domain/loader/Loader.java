package uk.co.lgs.domain.loader;

import java.io.File;

import uk.co.lgs.domain.graph.GraphData;
import uk.co.lgs.domain.loader.exception.LoaderException;

/**
 * I construct a graph from its source data file or store.
 * 
 * @author bouncysteve
 *
 */
public interface Loader {

    GraphData getGraph(File parentDir) throws LoaderException;
}