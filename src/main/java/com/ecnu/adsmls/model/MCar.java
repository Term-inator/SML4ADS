package com.ecnu.adsmls.model;

import java.util.LinkedHashMap;

public class MCar {
    private String name;

    private String model;

    private Double maxSpeed;

    private Double initSpeed;

    private String locationType;

    private LinkedHashMap<String, String> locationParams;

    private boolean heading;

    private Double roadDeviation;

    private String treePath;

    private MTree mTree;

    public MCar(String name, String model, Double maxSpeed, Double initSpeed, String locationType, LinkedHashMap<String, String> locationParams, boolean heading, Double roadDeviation, String treePath) {
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

    public Double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public Double getInitSpeed() {
        return initSpeed;
    }

    public void setInitSpeed(Double initSpeed) {
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

    public Double getRoadDeviation() {
        return roadDeviation;
    }

    public void setRoadDeviation(Double roadDeviation) {
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
