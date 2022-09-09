package com.ecnu.adsmls.utils.register.impl;

import com.ecnu.adsmls.utils.SimulatorConstant;
import com.ecnu.adsmls.utils.register.Function;
import com.ecnu.adsmls.utils.register.FunctionRegister;
import com.ecnu.adsmls.utils.register.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class WeatherRegister extends FunctionRegister {
    private final FunctionCategory functionCategory = FunctionCategory.WEATHER;

    @Override
    public void init() {
        Function carla = new Function(SimulatorConstant.SimulatorType.CARLA.value);
        carla.addParam("cloudiness", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Value(0), new Value(100), "[]"));
        carla.addParam("precipitation", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Value(0), new Value(100), "[]"));
        carla.addParam("precipitation_deposits", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Value(0), new Value(100), "[]"));
        carla.addParam("wind intensity", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Value(0), new Value(100), "[]"));
        carla.addParam("sun azimuth angle", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Value(0), new Value(360), "[]"));
        carla.addParam("sun altitude angle", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Value(-90), new Value(90), "[]"));
        carla.addParam("fog density", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Value(0), new Value(100), "[]"));
        carla.addParam("fog distance", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Value(0), new Value(Double.MAX_VALUE), "[]"));
        carla.addParam("wetness", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Value(0), new Value(100), "[]"));
        carla.addParam("fog falloff", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Value(0), new Value(Double.MAX_VALUE), "[]"));
        // 以下三个没有明确范围
        carla.addParam("scattering intensity", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Value(0), new Value(Double.MAX_VALUE), "[]"));
        carla.addParam("mie scattering scale", Function.DataType.DOUBLE, Function.Necessity.REQUIRED,
                new Between(new Value(0), new Value(Double.MAX_VALUE), "[]"));
        carla.addParam("rayleigh scattering scale", Function.DataType.DOUBLE, Function.Necessity.REQUIRED);

        Function lgsvl = new Function(SimulatorConstant.SimulatorType.LGSVL.value);

        functions.put(functionCategory, Arrays.asList(carla, lgsvl));
    }
}
