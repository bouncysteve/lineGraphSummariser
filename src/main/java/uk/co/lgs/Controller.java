package uk.co.lgs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Scanner;

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
import uk.co.lgs.model.graph.service.ModelCollator;
import uk.co.lgs.model.segment.exception.SegmentCategoryNotFoundException;
import uk.co.lgs.text.service.graph.GraphSummaryService;

@Configuration
@ComponentScan
public class Controller {

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
            System.out.print(summary);
            writeToFile(parentDir, "summary.txt", summary);

            // *************Collated Model ***************************//
            ModelCollator collator = context.getBean(ModelCollator.class);
            GraphModel collatedModel = collator.collate(model);
            System.out.print(collatedModel.toString());
            String collatedSummary = graphSummariser.getSummary(collatedModel);
            System.out.print(collatedSummary);

            writeToFile(parentDir, "collatedSummary.txt", collatedSummary);
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
            System.out.println("File has been written");

        } catch (Exception e) {
            System.out.println("Could not create file");
        }
    }
}
