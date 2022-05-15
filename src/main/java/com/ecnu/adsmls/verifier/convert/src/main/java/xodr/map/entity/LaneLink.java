package com.ecnu.adsmls.verifier.convert.src.main.java.xodr.map.entity;

import lombok.Data;

/**
 * LaneLink结构体
 * <p>
 * from                    表示当前LaneLink连接的 驶入lane的下标索引
 * to                      表示当前LaneLink连接的 驶出lane的下标索引
 * laneLinks               表示当前连接Road的Lane 连接 驶入Road的Lane的信息
 **/
@Data
public class LaneLink {

    private int from;
    private int to;

    private int index;

}
