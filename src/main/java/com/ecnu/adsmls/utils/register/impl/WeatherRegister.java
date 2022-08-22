package com.ecnu.adsmls.utils.register.impl;

import com.ecnu.adsmls.utils.register.Function;
import com.ecnu.adsmls.utils.register.FunctionRegister;
import com.ecnu.adsmls.utils.register.Value;

import java.util.ArrayList;
import java.util.List;

public class WeatherRegister extends FunctionRegister {
    @Override
    public void init() {
        Function carla = new Function("CARLA");
        carla.addParam("cloudiness", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Value(0), new Value(100), "[]"));
        carla.addParam("precipitation", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Value(0), new Value(100), "[]"));
        carla.addParam("precipitation_deposits", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Value(0), new Value(100), "[]"));
        carla.addParam("wind_intensity", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Value(0), new Value(100), "[]"));
        carla.addParam("sun_azimuth_angle", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Value(0), new Value(360), "[]"));
        carla.addParam("sun_altitude_angle", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Value(-90), new Value(90), "[]"));
        carla.addParam("fog_density", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Value(0), new Value(100), "[]"));
        carla.addParam("fog_distance", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Value(0), new Value(Double.MAX_VALUE), "[]"));
        carla.addParam("wetness", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Value(0), new Value(100), "[]"));
        carla.addParam("fog_falloff", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Value(0), new Value(Double.MAX_VALUE), "[]"));
        // TODO problem 以下三个没有明确范围
        carla.addParam("scattering_intensity", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Value(0), new Value(Double.MAX_VALUE), "[]"));
        carla.addParam("mie_scattering_scale", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Value(0), new Value(Double.MAX_VALUE), "[]"));
        carla.addParam("rayleigh_scattering_scale", Function.DataType.DOUBLE, Function.Necessity.REQUIRED);

        functions.add(carla);
    }
}
