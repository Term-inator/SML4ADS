package com.ecnu.adsmls.verifier.convert.src.main.java.xodr.map.entity;

import lombok.Data;

import java.util.List;

/**
 LaneSection结构体

 elementType = 2          表示LaneSection类型
 roadIndex                索引值 表示当前LaneSection属于那一条道路 可以在roads数组中查找
 roadId                   唯一标志当前LaneSection所属Road
 LaneSectionId            唯一标志一个LaneSection
 startPosition            起始偏移位置, 距离Road起始位置多少m开始
 lanes                    LaneSection的道路Lane的索引值列表
 length                   LaneSection长度
 **/

@Data
public class LaneSection {

    private int elementType;
    private int roadIndex;
    private int roadId;
    private int laneSectionId;
    private double startPosition;
    private List<Integer> lanesIndex;

    @Deprecated
    private double length; // 不需要用这个，直接用startPosition更方便
    private List<Lane> lanes;
    private int index;

}
