package com.ecnu.adsmls.model;

import java.util.ArrayList;
import java.util.List;

public class MProbabilityTransition {
    private long id;

    private long sourceId;

    private long targetId;

    private List<MPosition> linkPoints = new ArrayList<>();

    private String weight;

    private MPosition treeTextPosition;

    public MProbabilityTransition(long id, long sourceId, long targetId, List<MPosition> linkPoints, String weight, MPosition treeTextPosition) {
        this.id = id;
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.linkPoints = linkPoints;
        this.weight = weight;
        this.treeTextPosition = treeTextPosition;
    }

    public MProbabilityTransition() {
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

    public List<MPosition> getLinkPoints() {
        return linkPoints;
    }

    public void setLinkPoints(List<MPosition> linkPoints) {
        this.linkPoints = linkPoints;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public MPosition getTreeTextPosition() {
        return treeTextPosition;
    }

    public void setTreeTextPosition(MPosition treeTextPosition) {
        this.treeTextPosition = treeTextPosition;
    }
}
