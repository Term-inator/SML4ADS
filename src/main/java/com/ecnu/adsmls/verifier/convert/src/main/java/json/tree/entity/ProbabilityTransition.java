package com.ecnu.adsmls.verifier.convert.src.main.java.json.tree.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProbabilityTransition {
    private int id;
    private List<Position> linkPoints;
    private int sourceId;
    private int targetId;
    private Position treeTextPosition;
    private String weight;
    // 以下变量通过计算获取
    private int level;
    private int group;
    private int number;
    private BranchPoint sourceBranchPoint;
    private Behavior targetBehavior;
}
