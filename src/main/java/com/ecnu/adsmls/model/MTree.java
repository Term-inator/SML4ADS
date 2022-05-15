package com.ecnu.adsmls.model;

import java.util.ArrayList;
import java.util.List;

public class MTree {
    private long rootId;

    private List<MBehavior> behaviors = new ArrayList<>();

    private List<MBranchPoint> branchPoints = new ArrayList<>();

    private List<MCommonTransition> commonTransitions = new ArrayList<>();

    private List<MProbabilityTransition> probabilityTransitions = new ArrayList<>();

    private String errMsg;

    public MTree(long rootId, List<MBehavior> behaviors, List<MBranchPoint> branchPoints, List<MCommonTransition> commonTransitions, List<MProbabilityTransition> probabilityTransitions, String errMsg) {
        this.rootId = rootId;
        this.behaviors = behaviors;
        this.branchPoints = branchPoints;
        this.commonTransitions = commonTransitions;
        this.probabilityTransitions = probabilityTransitions;
        this.errMsg = errMsg;
    }

    public MTree() {}

    public long getRootId() {
        return rootId;
    }

    public void setRootId(long rootId) {
        this.rootId = rootId;
    }

    public List<MBehavior> getBehaviors() {
        return behaviors;
    }

    public void setBehaviors(List<MBehavior> behaviors) {
        this.behaviors = behaviors;
    }

    public List<MBranchPoint> getBranchPoints() {
        return branchPoints;
    }

    public void setBranchPoints(List<MBranchPoint> branchPoints) {
        this.branchPoints = branchPoints;
    }

    public List<MCommonTransition> getCommonTransitions() {
        return commonTransitions;
    }

    public void setCommonTransitions(List<MCommonTransition> commonTransitions) {
        this.commonTransitions = commonTransitions;
    }

    public List<MProbabilityTransition> getProbabilityTransitions() {
        return probabilityTransitions;
    }

    public void setProbabilityTransitions(List<MProbabilityTransition> probabilityTransitions) {
        this.probabilityTransitions = probabilityTransitions;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
