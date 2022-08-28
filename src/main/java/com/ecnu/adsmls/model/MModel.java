package com.ecnu.adsmls.model;

import java.util.ArrayList;
import java.util.List;

public class MModel {
    private String simulatorType;

    private String mapType;

    private String map;

    private String weatherType;

    private String weather;

    /**
     * custom weather
     */
    private MWeather mWeather;

    private double timeStep;

    private Double simulationTime;

    private String scenarioEndTrigger;

    private List<MCar> cars = new ArrayList<>();

    private List<String> requirements = new ArrayList<>();

    public MModel(String simulatorType, String mapType, String map, String weatherType, String weather, MWeather mWeather, double timeStep, double simulationTime, String scenarioEndTrigger, List<MCar> cars, List<String> requirements) {
        this.simulatorType = simulatorType;
        this.mapType = mapType;
        this.map = map;
        this.weatherType = weatherType;
        this.weather = weather;
        this.timeStep = timeStep;
        this.simulationTime = simulationTime;
        this.scenarioEndTrigger = scenarioEndTrigger;
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

    public String getMapType() {
        return mapType;
    }

    public void setMapType(String mapType) {
        this.mapType = mapType;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public void setWeatherType(String weatherType) {
        this.weatherType = weatherType;
    }

    public String getWeatherType() {
        return weatherType;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public MWeather getMWeather() {
        return mWeather;
    }

    public void setMWeather(MWeather mWeather) {
        this.mWeather = mWeather;
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

    public String getScenarioEndTrigger() {
        return scenarioEndTrigger;
    }

    public void setScenarioEndTrigger(String scenarioEndTrigger) {
        this.scenarioEndTrigger = scenarioEndTrigger;
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
