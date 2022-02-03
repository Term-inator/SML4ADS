package com.ecnu.adsmls.components.editor.impl;

import com.ecnu.adsmls.components.editor.TreeArea;


public class ProbabilityTransition extends Transition {
    public ProbabilityTransition(long id) {
        super(id);
        this.disableLoop();
        this.dashed();
    }

    @Override
    public boolean setTarget(TreeArea target) {
        if(target instanceof BranchPoint) {
            System.out.println("Target of ProbabilityTransition cannot be BranchPoint");
            return false;
        }
        return super.setTarget(target);
    }
}
