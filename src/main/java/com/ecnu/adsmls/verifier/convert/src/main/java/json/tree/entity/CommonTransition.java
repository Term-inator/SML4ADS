package com.ecnu.adsmls.verifier.convert.src.main.java.json.tree.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CommonTransition {

    private List<String> guard;
    private int id;
    private List<Position> linkPoints;
    private int sourceId;
    private int targetId;
    private Position treeTextPosition;
    // 以下变量通过计算获得
    private int level;
    private int group;
    private int number;
    private Behavior sourceBehavior;
    private Behavior targetBehavior;
    private BranchPoint targetBranchPoint;

}
