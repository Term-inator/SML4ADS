package com.ecnu.adsmls.model;

public class MConfig {
    private int simulationPort;

    public MConfig(int simulationPort) {
        this.simulationPort = simulationPort;
    }

    public MConfig() {
    }

    public Integer getSimulationPort() {
        return simulationPort;
    }

    public void setSimulationPort(int simulationPort) {
        this.simulationPort = simulationPort;
    }
}
