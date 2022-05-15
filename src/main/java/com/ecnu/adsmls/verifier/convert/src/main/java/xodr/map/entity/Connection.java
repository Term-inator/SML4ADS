package com.ecnu.adsmls.verifier.convert.src.main.java.xodr.map.entity;

import lombok.Data;

import java.util.List;

/**
 * Connection结构体
 * <p>
 * incomingRoadId          驶入Road的id
 * connectingRoadId        驶出Road的id
 * incomingRoadIndex       索引值 表示当前Connection连接的驶入Road
 * connectingRoadIndex     索引值 表示当前Connection连接Road
 * laneLinks               LaneLink结构体数组 表示当前连接Road的Lane 连接 驶入Road的Lane 的信息
 **/

@Data
public class Connection {

    private int direction; // 1左转 2直行 3右转
    private int incomingRoadId;
    private int connectingRoadId;
    private int incomingRoadIndex; //
    private int connectingRoadIndex; //
    private List<Integer> laneLinksIndex;

    private List<LaneLink> laneLinks;
    private int index;

}
