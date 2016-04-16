package uk.co.lgs;

import java.io.File;
import java.util.Scanner;

import org.springframework.context.ApplicationContext;
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
import uk.co.lgs.text.service.TextSummaryService;

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

        ApplicationContext context = new AnnotationConfigApplicationContext(Controller.class);
        Loader loader = context.getBean(Loader.class);
        GraphData graphData = loader.getGraph(parentDir);

        System.out.print(graphData.toString());

        GraphModel model = new GraphModelImpl(graphData);
        System.out.print(model.toString());

        TextSummaryService graphSummariser = context.getBean(TextSummaryService.class);
        String graphSummary = graphSummariser.getSummary(model);
        System.out.print(graphSummary);

        ModelCollator collator = context.getBean(ModelCollator.class);
        GraphModel collectedModel = collator.collate(model);
        System.out.print(collectedModel.toString());
    }
}
