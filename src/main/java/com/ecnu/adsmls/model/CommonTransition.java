package com.ecnu.adsmls.model;

import java.util.ArrayList;
import java.util.List;

public class CommonTransition {
    private long id;

    private long sourceId;

    private long targetId;

    private List<Position> linkPoints = new ArrayList<>();

    private List<String> guards = new ArrayList<>();

    public CommonTransition(long id, long sourceId, long targetId, List<Position> linkPoints, List<String> guards) {
        this.id = id;
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.linkPoints = linkPoints;
        this.guards = guards;
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

    public List<String> getGuards() {
        return guards;
    }

    public void setGuards(List<String> guards) {
        this.guards = guards;
    }
}
