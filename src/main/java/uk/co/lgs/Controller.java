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

    /**
     * Main method of controller - summarises the graph in the given directory.
     *
     * @param args
     *            command line arguments
     * @throws LoaderException
     *             if files can't be loaded
     * @throws CollatorException
     *             if an error on collating the graph
     */
    public static void main(final String[] args) throws LoaderException, CollatorException {

        String directory = args[0];

        File parentDir = new File(directory);
        if (!parentDir.exists() || args.length < 1) {
            final Scanner scanner = new Scanner(System.in);
            LOG.info("Enter the FULL PATH of the directory containing data.csv and schema.csv");
            directory = scanner.next();
            scanner.close();
            parentDir = new File(directory);
        }
        LOG.info("Parsing directory: " + directory);
        try (ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(Controller.class)) {

            // *************Domain (data) ***************************//
            final Loader loader = context.getBean(Loader.class);
            final GraphData graphData = loader.getGraph(parentDir);
            LOG.info(graphData.toString());

            // *************Model ***************************//
            final GraphModel model = new GraphModelImpl();
            model.setGraphData(graphData);
            LOG.info(model.toString());
            final GraphSummaryService graphSummariser = context.getBean(GraphSummaryService.class);
            final String summary = graphSummariser.getSummary(model);
            LOG.info(summary + "\n" + "\n");
            writeToFile(parentDir, SUMMARY_FILENAME, summary);

            // *************Gap and Gradient Collated Model
            // ***************************//
            final ModelCollator gapAndGradientCollator = context.getBean(GapAndGradientCollator.class);
            final GraphModel gapAndGradientCollatedModel = gapAndGradientCollator.collate(model);

            if (gapAndGradientCollatedModel.equals(model)) {
                LOG.info("GRADIENT COLLATION HAS NO EFFECT, NOT WRITING A COLLATED SUMMARY");
                try {
                    Files.deleteIfExists(new File(parentDir, GRADIENT_COLLATED_SUMMARY_FILENAME).toPath());
                } catch (final IOException e) {
                    LOG.error("Unable to delete existing collated summary", e);
                }

            } else {
                LOG.info(gapAndGradientCollatedModel.toString());
                final String collatedSummary = graphSummariser.getSummary(gapAndGradientCollatedModel);
                LOG.info(collatedSummary + "\n" + "\n");
                writeToFile(parentDir, GRADIENT_COLLATED_SUMMARY_FILENAME, collatedSummary);
            }

        }
    }

    private static void writeToFile(final File parentDir, final String fileName, final String content) {
        File targetFile = null;
        Writer output = null;
        try {
            targetFile = new File(parentDir, fileName);
            output = new BufferedWriter(new FileWriter(targetFile));
            output.write(content);
            output.close();
        } catch (final Exception e) {
            LOG.error("", e);
            LOG.info("Could not create file");
        }
    }
}
