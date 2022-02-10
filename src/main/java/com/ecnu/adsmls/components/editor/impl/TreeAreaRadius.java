package com.ecnu.adsmls.components.editor.impl;

public enum TreeAreaRadius {
    Behavior("behavior", 16),
    BranchPoint("branch point", 8),
    TreeLinkPoint("tree link point", 5);

    private String name;

    private int r;

    TreeAreaRadius(String name, int r) {
        this.name = name;
        this.r = r;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }
}
