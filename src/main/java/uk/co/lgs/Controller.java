package uk.co.lgs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import uk.co.lgs.domain.graph.GraphData;
import uk.co.lgs.domain.loader.Loader;
import uk.co.lgs.domain.loader.exception.LoaderException;
import uk.co.lgs.model.graph.GraphModel;
import uk.co.lgs.model.graph.GraphModelImpl;
import uk.co.lgs.model.graph.collator.exception.CollatorException;
import uk.co.lgs.model.graph.service.GapAndGradientCollator;
import uk.co.lgs.model.graph.service.ModelCollator;
import uk.co.lgs.model.segment.exception.SegmentCategoryNotFoundException;
import uk.co.lgs.text.service.graph.GraphSummaryService;

/**
 * I am the main class in the application. I output the state of the main domain
 * and model classes to string and write the text summary(/ies) to files.
 * 
 * @author bouncysteve
 *
 */
@Configuration
@ComponentScan
public class Controller {

    private static final Logger LOG = LoggerFactory.getLogger(Controller.class);
    private static final String GRADIENT_COLLATED_SUMMARY_FILENAME = "collatedSummary.txt";
    private static final String SUMMARY_FILENAME = "summary.txt";

    // TODO: error handling
    public static void main(String[] args) throws LoaderException, SegmentCategoryNotFoundException, CollatorException {

        String directory = args[0];

        File parentDir = new File(directory);
        if (!parentDir.exists() || args.length < 1) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the FULL PATH of the directory containing data.csv and schema.csv");
            directory = scanner.next();
            scanner.close();
            parentDir = new File(directory);
        }
        System.out.println("Parsing directory: " + directory);
        try (ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(Controller.class)) {

            // *************Domain (data) ***************************//
            Loader loader = context.getBean(Loader.class);
            GraphData graphData = loader.getGraph(parentDir);
            System.out.print(graphData.toString());

            // *************Model ***************************//
            GraphModel model = new GraphModelImpl(graphData);
            System.out.print(model.toString());
            GraphSummaryService graphSummariser = context.getBean(GraphSummaryService.class);
            String summary = graphSummariser.getSummary(model);
            System.out.print(summary + "\n" + "\n");
            writeToFile(parentDir, SUMMARY_FILENAME, summary);

            // *************Gradient Collated Model
            // ***************************//
            /*
             * ModelCollator gradientCollator =
             * context.getBean(GradientCollator.class); GraphModel collatedModel
             * = gradientCollator.collate(model);
             * 
             * if (collatedModel.equals(model)) { System.out.println(
             * "GRADIENT COLLATION HAS NO EFFECT, NOT WRITING A COLLATED SUMMARY"
             * ); try { Files.deleteIfExists(new File(parentDir,
             * GRADIENT_COLLATED_SUMMARY_FILENAME).toPath()); } catch
             * (IOException e) { LOG.error(
             * "Unable to delete existing collated summary", e); }
             * 
             * } else { System.out.print(collatedModel.toString()); String
             * collatedSummary = graphSummariser.getSummary(collatedModel);
             * System.out.print(collatedSummary + "\n" + "\n");
             * writeToFile(parentDir, GRADIENT_COLLATED_SUMMARY_FILENAME,
             * collatedSummary); }
             */

            // *************Gap and Gradient Collated Model
            // ***************************//
            ModelCollator gapAndGradientCollator = context.getBean(GapAndGradientCollator.class);
            GraphModel gapAndGradientCollatedModel = gapAndGradientCollator.collate(model);

            if (gapAndGradientCollatedModel.equals(model)) {
                System.out.println("GRADIENT COLLATION HAS NO EFFECT, NOT WRITING A COLLATED SUMMARY");
                try {
                    Files.deleteIfExists(new File(parentDir, GRADIENT_COLLATED_SUMMARY_FILENAME).toPath());
                } catch (IOException e) {
                    LOG.error("Unable to delete existing collated summary", e);
                }

            } else {
                System.out.print(gapAndGradientCollatedModel.toString());
                String collatedSummary = graphSummariser.getSummary(gapAndGradientCollatedModel);
                System.out.print(collatedSummary + "\n" + "\n");
                writeToFile(parentDir, GRADIENT_COLLATED_SUMMARY_FILENAME, collatedSummary);
            }

        }
    }

    private static void writeToFile(File parentDir, String fileName, String content) {
        File targetFile = null;
        Writer output = null;
        try {
            targetFile = new File(parentDir, fileName);
            output = new BufferedWriter(new FileWriter(targetFile));
            output.write(content);
            output.close();
        } catch (Exception e) {
            LOG.error("", e);
            System.out.println("Could not create file");
        }
    }
}
