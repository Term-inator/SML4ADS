package com.ecnu.adsmls.model;

public class MConfig {
    private String pythonEnv;

    private String simulatorType;

    private String simulatorPath;

    public MConfig(String pythonEnv, String simulatorType, String simulatorPath) {
        this.pythonEnv = pythonEnv;
        this.simulatorType = simulatorType;
        this.simulatorPath = simulatorPath;
    }

    public MConfig() {
    }

    public String getPythonEnv() {
        return pythonEnv;
    }

    public void setPythonEnv(String pythonEnv) {
        this.pythonEnv = pythonEnv;
    }

    public String getSimulatorType() {
        return simulatorType;
    }

    public void setSimulatorType(String simulatorType) {
        this.simulatorType = simulatorType;
    }

    public String getSimulatorPath() {
        return simulatorPath;
    }

    public void setSimulatorPath(String simulatorPath) {
        this.simulatorPath = simulatorPath;
    }
}
