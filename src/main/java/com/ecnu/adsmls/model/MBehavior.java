package com.ecnu.adsmls.model;


import java.util.LinkedHashMap;

public class MBehavior {
    private long id;

    private MPosition position;

    private String name;

    private LinkedHashMap<String, String> params = new LinkedHashMap<>();

    private MPosition treeTextPosition;

    // 通过 Transition 存储的 source 和 target 还原 TreeArea 的 inTransitions 和 outTransitions

    public MBehavior(long id, MPosition position, String name, LinkedHashMap<String, String> params, MPosition treeTextPosition) {
        this.id = id;
        this.position = position;
        this.name = name;
        this.params = params;
        this.treeTextPosition = treeTextPosition;
    }

    public MBehavior() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MPosition getPosition() {
        return position;
    }

    public void setPosition(MPosition position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedHashMap<String, String> getParams() {
        return params;
    }

    public void setParams(LinkedHashMap<String, String> params) {
        this.params = params;
    }

    public MPosition getTreeTextPosition() {
        return treeTextPosition;
    }

    public void setTreeTextPosition(MPosition treeTextPosition) {
        this.treeTextPosition = treeTextPosition;
    }
}
