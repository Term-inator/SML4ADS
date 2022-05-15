package com.ecnu.adsmls.verifier.convert.src.main.java.json.tree.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Behavior {

    private int id;
    private String name;
    private Map<String, Double> params;
    private Position position;
    private Position treeTextPosition;

    // 以下变量无法通过解析直接获取，需要通过计算获得
    private int level;
    private int group;
    private int number;
    private List<CommonTransition> nextTransitions;
    private List<Behavior> nextBehaviors;
    private List<BranchPoint> nextBranchPoints;

}
