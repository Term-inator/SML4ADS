package com.ecnu.adsmls.model;

import java.util.LinkedHashMap;

public class MCar {
    private String name;

    private String model;

    private double maxSpeed;

    private double initSpeed;

    private String locationType;

    private LinkedHashMap<String, String> locationParams;

    private boolean heading;

    private double roadDeviation;

    private String treePath;

    private MTree mTree;

    public MCar(String name, String model, double maxSpeed, double initSpeed, String locationType, LinkedHashMap<String, String> locationParams, boolean heading, double roadDeviation, String treePath) {
        this.name = name;
        this.model = model;
        this.maxSpeed = maxSpeed;
        this.initSpeed = initSpeed;
        this.locationType = locationType;
        this.locationParams = locationParams;
        this.heading = heading;
        this.roadDeviation = roadDeviation;
        this.treePath = treePath;
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

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public LinkedHashMap<String, String> getLocationParams() {
        return locationParams;
    }

    public void setLocationParams(LinkedHashMap<String, String> locationParams) {
        this.locationParams = locationParams;
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

    public MTree getMTree() {
        return mTree;
    }

    public void setMTree(MTree mTree) {
        this.mTree = mTree;
    }
}
