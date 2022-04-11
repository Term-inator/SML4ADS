package com.ecnu.adsmls.model;

import java.util.ArrayList;
import java.util.List;

public class MModel {
    private String simulatorType;

    private String map;

    private String weather;

    private String source;

    private double timeStep;

    private Double simulationTime;

    private List<MCar> cars = new ArrayList<>();

    private List<String> requirements = new ArrayList<>();

    public MModel(String simulatorType,String map, String weather, String source, double timeStep, double simulationTime, List<MCar> cars, List<String> requirements) {
        this.simulatorType = simulatorType;
        this.map = map;
        this.weather = weather;
        this.source = source;
        this.timeStep = timeStep;
        this.simulationTime = simulationTime;
        this.cars = cars;
        this.requirements = requirements;
    }

    public MModel() {
    }

    public String getSimulatorType() {
        return simulatorType;
    }

    public void setSimulatorType(String simulatorType) {
        this.simulatorType = simulatorType;
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

    public double getTimeStep() {
        return timeStep;
    }

    public void setTimeStep(double timeStep) {
        this.timeStep = timeStep;
    }

    public Double getSimulationTime() {
        return simulationTime;
    }

    public void setSimulationTime(Double simulationTime) {
        this.simulationTime = simulationTime;
    }

    public List<MCar> getCars() {
        return cars;
    }

    public void setCars(List<MCar> cars) {
        this.cars = cars;
    }

    public List<String> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<String> requirements) {
        this.requirements = requirements;
    }
}
