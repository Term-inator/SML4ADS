package com.ecnu.adsmls.model;

import java.util.ArrayList;
import java.util.List;

public class ProbabilityTransition {
    private long id;

    private long sourceId;

    private long targetId;

    private List<Position> linkPoints = new ArrayList<>();

    private String weight;

    public ProbabilityTransition(long id, long sourceId, long targetId, List<Position> linkPoints, String weight) {
        this.id = id;
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.linkPoints = linkPoints;
        this.weight = weight;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSourceId() {
        return sourceId;
    }

    public void setSourceId(long sourceId) {
        this.sourceId = sourceId;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public List<Position> getLinkPoints() {
        return linkPoints;
    }

    public void setLinkPoints(List<Position> linkPoints) {
        this.linkPoints = linkPoints;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
