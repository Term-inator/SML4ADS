package com.ecnu.adsmls.model;

import java.util.ArrayList;
import java.util.List;

public class MCommonTransition {
    private long id;

    private long sourceId;

    private long targetId;

    private List<MPosition> linkPoints = new ArrayList<>();

    private String guard;

    private MPosition treeTextPosition;

    public MCommonTransition(long id, long sourceId, long targetId, List<MPosition> linkPoints, String guard, MPosition treeTextPosition) {
        this.id = id;
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.linkPoints = linkPoints;
        this.guard = guard;
        this.treeTextPosition = treeTextPosition;
    }

    public MCommonTransition() {}

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

    public String getGuard() {
        return guard;
    }

    public void setGuard(String guard) {
        this.guard = guard;
    }

    public MPosition getTreeTextPosition() {
        return treeTextPosition;
    }

    public void setTreeTextPosition(MPosition treeTextPosition) {
        this.treeTextPosition = treeTextPosition;
    }
}
