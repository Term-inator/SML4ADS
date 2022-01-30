package com.ecnu.adsmls.utils;

public class Geometry {
    public static double distanceBetween(Position p1, Position p2) {
        double r = Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
        return r;
    }

    public static double radWithXAxis(Position p1, Position p2) {
        double r = distanceBetween(p1, p2);
        double rad = Math.acos((p2.x - p1.x) / r);
        if(p2.y < p1.y) {
            rad += Math.PI;
        }
        return rad;
    }

    public static boolean between(double p, double left, double right) {
        return (p >= left) && (p <= right);
    }
}
