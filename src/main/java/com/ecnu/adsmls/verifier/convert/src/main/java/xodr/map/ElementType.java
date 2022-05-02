package com.ecnu.adsmls.verifier.convert.src.main.java.xodr.map;

public enum ElementType {

    ROAD(1), LANE_SECTION(2), LANE(3),
    JUNCTION(4), CONNECTION(5), LANE_LINK(6),
    NONE(-1); // 啥都不是

    private Integer value;

    ElementType(int value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

}
