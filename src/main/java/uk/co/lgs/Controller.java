package uk.co.lgs;


import java.io.File;
import java.util.Scanner;

import uk.co.lgs.domain.graph.Graph;
import uk.co.lgs.domain.loader.Loader;
import uk.co.lgs.domain.loader.exception.LoaderException;
import uk.co.lgs.domain.loader.iscatter.IscatterLoaderImpl;

public class Controller {

    public static void main(String[] args) throws LoaderException {

        // create a scanner so we can read the command-line input
        Scanner scanner = new Scanner(System.in);

        // prompt for the user's name
        System.out.print("Enter the directory containing data.csv and schema.csv");

        // get their input as a String
        String directory = scanner.next();
        scanner.close();
        File parentDir = new File(directory);
        Loader loader = new IscatterLoaderImpl(parentDir);
        Graph graph = loader.getGraph();
        System.out.print(graph.toString());

    }

}
