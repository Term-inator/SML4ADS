package com.ecnu.adsmls.verifier.convert.src.main.java.xodr.map.entity;

import lombok.Data;

import java.util.List;

/**
 Junction结构体

 elementType = 4          表示Junction类型
 junctionId               唯一标志一个junction
 connections[]            Connection结构体数组 表示当前junction连接的 incomingRoad 和 connectingRoad

 **/

@Data
public class Junction {

    private int elementType;
    private int junctionId;
    private List<Integer> connectionsIndex;

    private List<Connection> connections;
    private int index;

}
