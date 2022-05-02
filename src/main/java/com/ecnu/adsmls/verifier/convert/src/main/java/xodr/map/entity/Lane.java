package com.ecnu.adsmls.verifier.convert.src.main.java.xodr.map.entity;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 Lane结构体

 elementType = 3          表示Lane类型
 roadId                   当前Lane所属Road的id
 roadIndex                当前Lane所属Road的索引值
 laneSectionIndex         当前Lane所属LaneSection的索引值
 laneId                   标志当前Lane在LaneSection中的相对位置 中心线为0 左边的道路id一次递减 右边的道路id一次递增
 type                     表示当前lane是什么类型 1表示driving
 predecessorIndex         前继Lane的索引值
 successorIndex           后继Lane的索引值
 laneChange               int类型 由于车道从右到左id递增 用该变量表示当前lane是否允许变道 -1表示不知道 1表示允许向左变道 2表示允许向右变道 3表示两边都允许 4表示两边都不允许

 **/

@Data
public class Lane {

    private int elementType;
    private int roadId;
    private int roadIndex;
    private int laneSectionIndex;
    private int laneId; // 表示lane相对位置的id，往左依次递加1，往右依次递减1，中心线为0
    private int type; // 1 driving, 0 啥都不能干
    private int predecessorIndex = -1; //
    private int successorIndex = -1; //
    private int laneChange; // 见下方：1可以左变道 2可以右变道 3可以左右变道 4可以个寂寞

    private int laneSectionId;
    private int singleId; // 与laneId不同，singleId是真正的唯一id，表示标识符
    private int index;
    private int predecessorLaneId; // 与laneId同类，表示相对位置; 暂时用0表示不存在
    private int predecessorSingleId = -1; // 标识符
    private int successorLaneId;
    private int successorSingleId = -1;
    private double width; // 路的宽度，通过宽度和偏移来计算lane

    // laneChange: type -> uppaal number
    private static final Map<String, Integer> LANE_CHANGE_TYPE = new HashMap<String, Integer>() {{
        put("increase", 1); // 可以左变道
        put("decrease", 2); // 可以右变道
        put("both", 3); // 可以左右变道
        put("none", 4); // 可以个寂寞
    }};

}
