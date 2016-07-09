package uk.co.lgs.model.graph;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import uk.co.lgs.domain.graph.GraphData;
import uk.co.lgs.model.graph.service.GapService;
import uk.co.lgs.model.graph.service.SegmentationService;
import uk.co.lgs.model.segment.exception.SegmentCategoryNotFoundException;
import uk.co.lgs.model.segment.graph.GraphSegment;
import uk.co.lgs.model.segment.graph.GraphSegmentImpl;
import uk.co.lgs.model.segment.graph.category.GraphSegmentGradient;

/**
 * I represent a whole graph and its features.
 *
 * @author bouncysteve
 *
 */
public class GraphModelImpl implements GraphModel {

    private List<GraphSegment> graphSegments;

    private List<String> labels;

    private String title;

    private boolean collated = false;

    private List<String> units;

    private boolean intersects;

    @Autowired
    private SegmentationService segmentationService;

    @Autowired
    private GapService gapService;

    public GraphModelImpl() {
        this.graphSegments = new ArrayList<>();
    }

    @Override
    public void setGraphData(final GraphData graphData) throws SegmentCategoryNotFoundException {
        final List<GraphSegment> segments = this.segmentationService.segment(graphData);
        this.graphSegments = this.gapService.addGapInfo(segments);
        this.labels = graphData.getHeader();
        this.title = graphData.getTitle();
        this.units = graphData.getUnits();
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
        final StringBuilder builder = new StringBuilder();
        builder.append("\n");
        if (this.isCollated()) {
            builder.append("*********GRAPH MODEL OBJECT (COLLATED SEGMENTS)*********").append("\n");
        } else {
            builder.append("*********GRAPH MODEL OBJECT (SEGMENTS)*********").append("\n");
        }
        if (null != this.getTitle()) {
            builder.append("Title: ").append(this.getTitle()).append("\n");
        }
        builder.append("Labels: ").append(this.getLabels()).append("\n");
        builder.append("Units: ").append(this.getUnits()).append("\n");
        boolean doneSegmentHeader = false;
        GraphSegmentGradient previousSegmentCategory = null;
        for (final GraphSegment segment : this.getGraphSegments()) {
            if (!doneSegmentHeader) {
                builder.append(GraphSegmentImpl.getHeader()).append("\n");
                doneSegmentHeader = true;
            }
            if (!this.isCollated() && null != previousSegmentCategory
                    && !previousSegmentCategory.equals(segment.getGraphSegmentGradientCategory())) {
                builder.append("\n");
            }
            builder.append(segment.toString()).append("\n");
            previousSegmentCategory = segment.getGraphSegmentGradientCategory();
        }
        builder.append("SegmentCount: ").append(this.getSegmentCount()).append("\n");
        builder.append("Length: ").append(this.getLength()).append("\n");
        builder.append("\n");
        return builder.toString();
    }

    @Override
    public int getLength() {
        int length = 0;
        for (final GraphSegment segment : this.getGraphSegments()) {
            length += segment.getLength();
        }
        return length;
    }

    @Override
    public void append(final GraphSegment segment) {
        this.graphSegments.add(segment);
    }

    @Override
    public void setLabels(final List<String> labels) {
        this.labels = labels;
    }

    @Override
    public void setTitle(final String title) {
        this.title = title;
    }

    @Override
    public boolean isCollated() {
        return this.collated;
    }

    @Override
    public void setCollated(final boolean collated) {
        this.collated = collated;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.graphSegments == null ? 0 : this.graphSegments.hashCode());
        result = prime * result + (this.labels == null ? 0 : this.labels.hashCode());
        result = prime * result + (this.title == null ? 0 : this.title.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GraphModelImpl other = (GraphModelImpl) obj;
        if (this.graphSegments == null) {
            if (other.graphSegments != null) {
                return false;
            }
        } else if (!this.graphSegments.equals(other.graphSegments)) {
            return false;
        }
        if (this.labels == null) {
            if (other.labels != null) {
                return false;
            }
        } else if (!this.labels.equals(other.labels)) {
            return false;
        }
        if (this.title == null) {
            if (other.title != null) {
                return false;
            }
        } else if (!this.title.equals(other.title)) {
            return false;
        }
        return true;
    }

    @Override
    public List<String> getUnits() {
        return this.units;
    }

    @Override
    public void setUnits(final List<String> units) {
        this.units = units;
    }

    @Override
    public boolean isIntersecting() {
        return this.intersects;
    }

    @Override
    public void setIntersecting(final boolean intersecting) {
        this.intersects = intersecting;
    }
}
