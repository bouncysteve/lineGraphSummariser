package uk.co.lgs;

import java.io.File;
import java.util.Scanner;

import uk.co.lgs.domain.graph.GraphData;
import uk.co.lgs.domain.loader.Loader;
import uk.co.lgs.domain.loader.exception.LoaderException;
import uk.co.lgs.domain.loader.iscatter.IscatterLoaderImpl;

public class Controller {

    // TODO: error handling
    public static void main(String[] args) throws LoaderException {

        String directory = args[0];
        File parentDir = new File(directory);
        if (!parentDir.exists() || args.length < 1) {
            // create a scanner so we can read the command-line input
            Scanner scanner = new Scanner(System.in);
            // prompt for the directory path
            System.out.print("Enter the FULL PATH of the directory containing data.csv and schema.csv");
            // get their input as a String
            directory = scanner.next();
            scanner.close();
            parentDir = new File(directory);
        }

        Loader loader = new IscatterLoaderImpl(parentDir);
        GraphData graphData = loader.getGraph();
        System.out.print(graphData.toString());
    }
}
