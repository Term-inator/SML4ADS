package com.ecnu.adsmls.utils;

/**
 * 平面几何工具类
 */
public class Geometry {
    public enum quadrant {
        FIRST, SECOND, THIRD, FOURTH
    }

    /**
     * 平面上两点间距离
     * @param p1
     * @param p2
     * @return
     */
    public static double distanceBetween(Position p1, Position p2) {
        double r = Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
        return r;
    }

    /**
     * 向量 p1p2 与 x 轴的夹角（弧度）
     * @param p1
     * @param p2
     * @return
     */
    public static double radWithXAxis(Position p1, Position p2) {
        double r = distanceBetween(p1, p2);
        double rad = Math.acos((p2.x - p1.x) / r);
        if(p2.y > p1.y) {
            rad = 2 * Math.PI - rad;
        }
        return rad;
    }

    /**
     * p1 p1 的中点
     * @param p1
     * @param p2
     * @return
     */
    public static Position centerOf(Position p1, Position p2) {
        return new Position((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    }

    /**
     * 判断 p 是否在 left right 之间（闭区间）
     * @param p
     * @param left
     * @param right
     * @return
     */
    public static boolean between(double p, double left, double right) {
        return (p >= left) && (p <= right);
    }

    /**
     * 支持所有区间
     * @param p
     * @param left
     * @param right
     * @param op
     * @return
     */
    public static boolean between(double p, double left, double right, String op) {
        boolean res = true;
        char op1 = op.charAt(0);
        char op2 = op.charAt(1);
        if(op1 == '(') {
            res &= (p > left);
        }
        else if(op1 == '[') {
            res &= (p >= left);
        }
        if(op2 == ')') {
            res &= (p < right);
        }
        else if(op2 == ']') {
            res &= (p <= right);
        }
        return res;
    }

    /**
     * 判断弧度为 rad 的向量所在的象限
     * @param rad
     * @return
     */
    public static quadrant inQuadrant(double rad) {
        if(between(rad, 0, Math.PI / 2, "[)")) {
            return quadrant.FIRST;
        }
        else if(between(rad, Math.PI / 2, Math.PI, "[)")) {
            return quadrant.SECOND;
        }
        else if(between(rad, Math.PI, 3 * Math.PI / 2, "[)")) {
            return quadrant.THIRD;
        }
        else {
            return quadrant.FOURTH;
        }
    }
}
