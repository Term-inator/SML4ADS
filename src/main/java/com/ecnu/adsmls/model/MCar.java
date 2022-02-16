package com.ecnu.adsmls.model;

public class MCar {
    private String name;

    private String model;

    private double maxSpeed;

    private double initSpeed;

    private int roadId;

    private int laneSecId;

    private int laneId;

    private String filter;

    private double offset;

    private boolean heading;

    private double roadDeviation;

    private String treePath;

    private String mTree;

    public MCar(String name, String model, double maxSpeed, double initSpeed, int roadId, int laneSecId, int laneId, String filter, double offset, boolean heading, double roadDeviation, String treePath, String mTree) {
        this.name = name;
        this.model = model;
        this.maxSpeed = maxSpeed;
        this.initSpeed = initSpeed;
        this.roadId = roadId;
        this.laneSecId = laneSecId;
        this.laneId = laneId;
        this.filter = filter;
        this.offset = offset;
        this.heading = heading;
        this.roadDeviation = roadDeviation;
        this.treePath = treePath;
        this.mTree = mTree;
    }

    public MCar() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getInitSpeed() {
        return initSpeed;
    }

    public void setInitSpeed(double initSpeed) {
        this.initSpeed = initSpeed;
    }

    public int getRoadId() {
        return roadId;
    }

    public void setRoadId(int roadId) {
        this.roadId = roadId;
    }

    public int getLaneSecId() {
        return laneSecId;
    }

    public void setLaneSecId(int laneSecId) {
        this.laneSecId = laneSecId;
    }

    public int getLaneId() {
        return laneId;
    }

    public void setLaneId(int laneId) {
        this.laneId = laneId;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public double getOffset() {
        return offset;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }

    public boolean getHeading() {
        return heading;
    }

    public void setHeading(boolean heading) {
        this.heading = heading;
    }

    public double getRoadDeviation() {
        return roadDeviation;
    }

    public void setRoadDeviation(double roadDeviation) {
        this.roadDeviation = roadDeviation;
    }

    public String getTreePath() {
        return treePath;
    }

    public void setTreePath(String treePath) {
        this.treePath = treePath;
    }

    public String getMTree() {
        return mTree;
    }

    public void setMTree(String mTree) {
        this.mTree = mTree;
    }
}
