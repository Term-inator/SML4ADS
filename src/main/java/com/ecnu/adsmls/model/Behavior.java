package com.ecnu.adsmls.model;


import java.util.LinkedHashMap;

public class Behavior {
    private long id;

    private Position position;

    private String name;

    private LinkedHashMap<String, String> params = new LinkedHashMap<>();

    // 通过 Transition 存储的 source 和 target 还原 TreeArea 的 inTransitions 和 outTransitions

    public Behavior(long id, Position position, String name, LinkedHashMap<String, String> params) {
        this.id = id;
        this.position = position;
        this.name = name;
        this.params = params;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
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
}
