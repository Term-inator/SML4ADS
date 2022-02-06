package com.ecnu.adsmls.model;

public class BranchPoint {
    private long id;

    private Position position;

    public BranchPoint(long id, Position position) {
        this.id = id;
        this.position = position;
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
}
