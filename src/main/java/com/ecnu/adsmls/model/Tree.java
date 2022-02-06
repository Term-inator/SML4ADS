package com.ecnu.adsmls.model;

import java.util.ArrayList;
import java.util.List;

public class Tree {
    private long rootId;

    private List<Behavior> behaviors = new ArrayList<>();

    private List<BranchPoint> branchPoints = new ArrayList<>();

    private List<CommonTransition> commonTransitions = new ArrayList<>();

    private List<ProbabilityTransition> probabilityTransitions = new ArrayList<>();

    public Tree(long rootId, List<Behavior> behaviors, List<BranchPoint> branchPoints, List<CommonTransition> commonTransitions, List<ProbabilityTransition> probabilityTransitions) {
        this.rootId = rootId;
        this.behaviors = behaviors;
        this.branchPoints = branchPoints;
        this.commonTransitions = commonTransitions;
        this.probabilityTransitions = probabilityTransitions;
    }

    public long getRootId() {
        return rootId;
    }

    public void setRootId(long rootId) {
        this.rootId = rootId;
    }

    public List<Behavior> getBehaviors() {
        return behaviors;
    }

    public void setBehaviors(List<Behavior> behaviors) {
        this.behaviors = behaviors;
    }

    public List<BranchPoint> getBranchPoints() {
        return branchPoints;
    }

    public void setBranchPoints(List<BranchPoint> branchPoints) {
        this.branchPoints = branchPoints;
    }

    public List<CommonTransition> getCommonTransitions() {
        return commonTransitions;
    }

    public void setCommonTransitions(List<CommonTransition> commonTransitions) {
        this.commonTransitions = commonTransitions;
    }

    public List<ProbabilityTransition> getProbabilityTransitions() {
        return probabilityTransitions;
    }

    public void setProbabilityTransitions(List<ProbabilityTransition> probabilityTransitions) {
        this.probabilityTransitions = probabilityTransitions;
    }
}
