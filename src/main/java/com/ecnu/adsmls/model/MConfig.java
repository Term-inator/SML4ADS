package com.ecnu.adsmls.model;

public class MConfig {
    private String simulatorType;

    private int simulationPort;

    public MConfig(String simulatorType, int simulationPort) {
        this.simulatorType = simulatorType;
        this.simulationPort = simulationPort;
    }

    public MConfig() {
    }

    public String getSimulatorType() {
        return simulatorType;
    }

    public void setSimulatorType(String simulatorType) {
        this.simulatorType = simulatorType;
    }

    public Integer getSimulationPort() {
        return simulationPort;
    }

    public void setSimulationPort(int simulationPort) {
        this.simulationPort = simulationPort;
    }
}
