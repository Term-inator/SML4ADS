package com.ecnu.adsmls.verifier.convert.src.main.java.json.tree.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MTree {

    private List<Behavior> behaviors;
    private List<BranchPoint> branchPoints;
    private List<CommonTransition> commonTransitions;
    private List<ProbabilityTransition> probabilityTransitions;
    private int rootId;

}
