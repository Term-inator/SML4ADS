package com.ecnu.adsmls.model;

import java.util.LinkedHashMap;

public class MWeather {
    private LinkedHashMap<String, String> weatherParams;

    public MWeather(LinkedHashMap<String, String> weatherParams) {
        this.weatherParams = weatherParams;
    }

    public MWeather() {
    }

    public LinkedHashMap<String, String> getWeatherParams() {
        return weatherParams;
    }

    public void setWeatherParams(LinkedHashMap<String, String> weatherParams) {
        this.weatherParams = weatherParams;
    }
}
