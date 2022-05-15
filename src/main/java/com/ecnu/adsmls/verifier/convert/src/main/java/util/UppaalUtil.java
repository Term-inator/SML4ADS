package com.ecnu.adsmls.verifier.convert.src.main.java.util;

/**
 * @author wei
 * @description uppaal中的常量
 * @date 2022-04-06 19:03
 */
public class UppaalUtil {

    /*
        Uppaal对double类型的计算不友好，因此计算都转化为整数计算
        K为保留小数部分而放大的倍数，每放大10倍则多保留1位；
        这里是保留了1位小数，因此放大10倍数值
     */
    public static final int K = 10; // 放大倍数

    /*
        Uppaal只支持INT16范围内的数计算
     */
    public static final int INT16_MAX = 32767;
    public static final int INT16_MIN = -32768;

    /*
        已定义好的数据结构路径、函数路径、边的变量路径、自动机模型(Time/EndTrigger)
     */
    public static final String DEFINED_PATH = "src/main/java/com/ecnu/adsmls/verifier/convert/src/main/resources/uppaal/defined.txt";
    public static final String FUNCTION_PATH = "src/main/java/com/ecnu/adsmls/verifier/convert/src/main/resources/uppaal/function.txt";
    public static final String TRANSITION_PATH = "src/main/java/com/ecnu/adsmls/verifier/convert/src/main/resources/uppaal/transition.txt";
    public static final String AUTOMATON_PATH = "src/main/java/com/ecnu/adsmls/verifier/convert/src/main/resources/uppaal/Timer.txt";
    public static final String END_TRIGGER_PATH = "src/main/java/com/ecnu/adsmls/verifier/convert/src/main/resources/uppaal/EndTrigger.txt";

    /*
        地图信息
     */
    public static final int ROAD_LANESECTION = 10; // 一个road含有laneSection的数量
    public static final int LANESECTION_LANE = 10; // 一个laneSection含有lane的数量
    public static final int JUNCTION_CONNECTION = 10; // 一个junction含有connection的数量
    public static final int CONNECTION_LANELINK = 10; // 一个connection含有laneLink的数量

    /**
     * @param x 要转化的数值
     * @description 将double类型的数值放大K倍，并转化为int类型
     */
    public static int f(double x) {
        return (int) Math.round(x * K);
    }
}
