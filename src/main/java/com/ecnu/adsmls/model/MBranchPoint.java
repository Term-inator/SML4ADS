package com.ecnu.adsmls.model;

public class MBranchPoint {
    private long id;

    private MPosition position;

    public MBranchPoint(long id, MPosition position) {
        this.id = id;
        this.position = position;
    }

    public MBranchPoint() {}

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
}
