package uk.co.lgs.model.graph;

import java.util.ArrayList;
import java.util.List;

import uk.co.lgs.domain.graph.GraphData;
import uk.co.lgs.model.graph.service.SegmentationServiceImpl;
import uk.co.lgs.model.segment.exception.SegmentCategoryNotFoundException;
import uk.co.lgs.model.segment.graph.GraphSegment;

public class GraphModelImpl implements GraphModel {

    private List<GraphSegment> graphSegments;

    private List<String> labels;

    private String title;

    private boolean collated = false;

    public GraphModelImpl(GraphData graphData) throws SegmentCategoryNotFoundException {
        this.graphSegments = new SegmentationServiceImpl().segment(graphData);
        this.labels = graphData.getHeader();
        this.title = graphData.getTitle();
    }

    public GraphModelImpl() {
        this.graphSegments = new ArrayList<GraphSegment>();
    }

    @Override
    public List<GraphSegment> getGraphSegments() {
        return this.graphSegments;
    }

    @Override
    public List<String> getLabels() {
        return this.labels;
    }

    @Override
    public int getSegmentCount() {
        return this.graphSegments.size();
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        if (this.isCollated()) {
            builder.append("*********GRAPH MODEL OBJECT (COLLATED SEGMENTS)*********").append("\n");
        } else {
            builder.append("*********GRAPH MODEL OBJECT (SEGMENTS)*********").append("\n");
        }
        builder.append("Title: ").append(this.getTitle()).append("\n");
        builder.append("Labels: ").append(this.getLabels()).append("\n");
        builder.append("Segments: ").append("\n");
        for (GraphSegment segment : this.getGraphSegments()) {
            builder.append("\t").append(segment.toString()).append("\n");
        }
        builder.append("SegmentCount: ").append(this.getSegmentCount()).append("\n");
        builder.append("Length: ").append(this.getLength()).append("\n");
        builder.append("\n");
        return builder.toString();
    }

    @Override
    public int getLength() {
        int length = 0;
        for (GraphSegment segment : this.getGraphSegments()) {
            length += segment.getLength();
        }
        return length;
    }

    @Override
    public void append(GraphSegment segment) {
        this.graphSegments.add(segment);
    }

    @Override
    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean isCollated() {
        return this.collated;
    }

    @Override
    public void setCollated(boolean collated) {
        this.collated = collated;
    }
}
