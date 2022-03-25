package com.ecnu.adsmls.components.editor.treeeditor.impl;

public class CommonTransition extends Transition {
    private String guard;

    public CommonTransition(long id) {
        super(id);
    }

    public String getGuard() {
        return guard;
    }

    public void setGuard(String guard) {
        this.guard = guard;
        this.getTreeText().setText(this.getInfo());
    }

    @Override
    public String getInfo() {
        StringBuilder res = new StringBuilder();
        if(this.guard != null) {
            res.append(this.guard);
        }
        return res.toString();
    }
}
