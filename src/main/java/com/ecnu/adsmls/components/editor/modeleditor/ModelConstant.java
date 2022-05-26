package com.ecnu.adsmls.components.editor.modeleditor;

import java.util.Map;

public class ModelConstant {
    public enum Simulator {
        CARLA("carla"), LGSVL("lgsvl");

        public String value;

        Simulator(String value) {
            this.value = value;
        }
    }

    protected static final Map<Simulator, String[]> weathers = Map.of(
            Simulator.CARLA, new String[]{
                    "ClearNoon",
                    "CloudyNoon",
                    "WetNoon",
                    "WetCloudyNoon",
                    "SoftRainNoon",
                    "MidRainyNoon",
                    "HardRainNoon",
                    "ClearSunset",
                    "CloudySunset",
                    "WetSunset",
                    "WetCloudySunset",
                    "SoftRainSunset",
                    "MidRainSunset",
                    "HardRainSunset"
            },
            Simulator.LGSVL, new String[]{
                    "clear",
                    "rainy"
            }
    );

    protected static final Map<Simulator, String[]> models = Map.of(
            Simulator.CARLA, new String[]{
                    "random",
                    "vehicle.audi.a2",
                    "vehicle.audi.etron",
                    "vehicle.audi.tt",
                    "vehicle.bmw.grandtourer",
                    "vehicle.carlamotors.carlacola",
                    "vehicle.carlamotors.firetruck",
                    "vehicle.chevrolet.impala",
                    "vehicle.citroen.c3",
                    "vehicle.dodge.charger_2020",
                    "vehicle.dodge.charger_police",
                    "vehicle.dodge.charger_police_2020",
                    "vehicle.ford.ambulance",
                    "vehicle.ford.crown",
                    "vehicle.ford.mustang",
                    "vehicle.jeep.wrangler_rubicon",
                    "vehicle.kawasaki.ninja",
                    "vehicle.lincoln.mkz_2017",
                    "vehicle.lincoln.mkz_2020",
                    "vehicle.mercedes.coupe",
                    "vehicle.mercedes.coupe_2020",
                    "vehicle.mercedes.sprinter",
                    "vehicle.micro.microlino",
                    "vehicle.mini.cooper_s",
                    "vehicle.mini.cooper_s_2021",
                    "vehicle.nissan.micra",
                    "vehicle.nissan.patrol",
                    "vehicle.nissan.patrol_2021",
                    "vehicle.seat.leon",
                    "vehicle.tesla.cybertruck",
                    "vehicle.tesla.model3",
                    "vehicle.toyota.prius",
                    "vehicle.volkswagen.t2",
                    "vehicle.volkswagen.t2_2021"
            },
            Simulator.LGSVL, new String[]{"random"});

    /**
     * 获取蓝图
     */
    public static String[] getModel(Simulator simulator) {
        return models.get(simulator);
    }

    /**
     * 设置天气
     */
    public static String[] getWeather(Simulator simulator) {
        return weathers.get(simulator);
    }
}
