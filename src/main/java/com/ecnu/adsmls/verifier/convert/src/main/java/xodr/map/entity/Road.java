package com.ecnu.adsmls.verifier.convert.src.main.java.xodr.map.entity;

import lombok.Data;

import java.util.List;

/**
 Road的结构体

 elementType = 1        表示Road类型
 roadId                 唯一标识一条Road
 junctionId             junction的id 表示当前Road在那一个junction中作为连接路   -1表示不是连接路 不属于任何一条junction
 junctionIndex          索引值 用于在junction数组中索引
 length                 路的总长度
 predecessorElementType predecessor的类型 根据这个类型 在不同的数组中进行索引
 predecessorIndex       索引值 表示当前Road的前继 可以是Road 也可以是一个junction  需要配合predecessorElementType在对应的类型数组中索引    用-1表示空
 successorElementType   successor的类型 根据这个类型 在不同的数组中进行索引
 successorIndex         索引值 表示当前Road的后继 可以是Road 也可以是一个junction  需要配合successorElementType在对应的类型数组中索引    用-1表示空
 maxSpeed               路的最大限速
 laneSections[]         表示当前Road中的道路段索引数组  道路段的顺序就是数组的顺序   道路段中又有很多不同的道路Lane
 **/
@Data
public class Road {

    private int elementType;
    private int roadId;
    private int junctionIndex; // init: 第一次初始化时无法确定，需要在init中再次初始化
    private int junctionId;
    private double length;
    private int predecessorElementType;
    private int predecessorIndex; // init
    private int successorElementType;
    private int successorIndex; // init
    private double maxSpeed;
    private List<Integer> laneSectionsIndex;

    private List<LaneSection> laneSections;
    private int successorId;
    private int predecessorId;
    private int index;

}
