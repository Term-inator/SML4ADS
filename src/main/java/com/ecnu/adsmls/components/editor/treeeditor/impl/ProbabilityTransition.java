package com.ecnu.adsmls.components.editor.treeeditor.impl;

import com.ecnu.adsmls.components.editor.treeeditor.TreeArea;


public class ProbabilityTransition extends Transition {
    private String weight = "";

    public ProbabilityTransition(long id) {
        super(id);
        this.disableLoop();
        this.dashed();
    }

    @Override
    public boolean setTarget(TreeArea target) {
        if (target instanceof BranchPoint) {
            System.out.println("Target of ProbabilityTransition cannot be BranchPoint");
            return false;
        }
        return super.setTarget(target);
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
        this.getTreeText().setText(this.getInfo());
    }

    @Override
    public String getInfo() {
        return this.weight;
    }
}
