package com.ecnu.adsmls.utils;

/**
 * 平面坐标
 */
public class Position {
    public double x;
    public double y;

    public Position() {

    }

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * 移动坐标到 x y
     * @param x
     * @param y
     */
    public void relocate(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
