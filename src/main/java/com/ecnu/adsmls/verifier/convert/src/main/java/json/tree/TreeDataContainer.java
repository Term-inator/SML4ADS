package com.ecnu.adsmls.verifier.convert.src.main.java.json.tree;

import com.ecnu.adsmls.verifier.convert.src.main.java.json.tree.entity.Car;
import lombok.Data;

import java.util.List;

@Data
public class TreeDataContainer {
    // 这里对应json的各个部分
    private List<Car> cars;
    private String map;
    private String source;
    private double timeStep;
    private String weather;
    private List<String> requirements;

}
