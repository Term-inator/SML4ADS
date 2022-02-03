package com.ecnu.adsmls.components.editor.impl;


public class ProbabilityTransition extends Transition {
    public ProbabilityTransition(long id) {
        super(id);
        this.disableLoop();
        this.dashed();
    }
}
