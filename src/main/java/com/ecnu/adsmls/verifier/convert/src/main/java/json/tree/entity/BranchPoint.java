package com.ecnu.adsmls.verifier.convert.src.main.java.json.tree.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BranchPoint {

    private int id;
    private Position position;
    // 以下需通过计算获得
    private int level;
    private int group;
    private int number;
    private List<ProbabilityTransition> nextTransitions;
    private List<Behavior> nextBehaviors;

}
