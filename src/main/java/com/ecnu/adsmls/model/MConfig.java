package com.ecnu.adsmls.model;

public class MConfig {
    private String pythonEnv;

    public MConfig(String pythonEnv) {
        this.pythonEnv = pythonEnv;
    }

    public MConfig() {
    }

    public String getPythonEnv() {
        return pythonEnv;
    }

    public void setPythonEnv(String pythonEnv) {
        this.pythonEnv = pythonEnv;
    }
}
