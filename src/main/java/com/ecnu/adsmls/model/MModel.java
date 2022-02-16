package com.ecnu.adsmls.model;

import java.util.ArrayList;
import java.util.List;

public class MModel {
    private String map;

    private String weather;

    private String source;

    private String timeStep;

    private List<MCar> cars = new ArrayList<>();

    public MModel(String map, String weather, String source, String timeStep, List<MCar> cars) {
        this.map = map;
        this.weather = weather;
        this.source = source;
        this.timeStep = timeStep;
        this.cars = cars;
    }

    public MModel() {
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTimeStep() {
        return timeStep;
    }

    public void setTimeStep(String timeStep) {
        this.timeStep = timeStep;
    }

    public List<MCar> getCars() {
        return cars;
    }

    public void setCars(List<MCar> cars) {
        this.cars = cars;
    }
}
