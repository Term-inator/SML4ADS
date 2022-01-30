package com.ecnu.adsmls.utils;

public class Geometry {
    public enum quadrant {
        FIRST, SECOND, THIRD, FOURTH
    }

    public static double distanceBetween(Position p1, Position p2) {
        double r = Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
        return r;
    }

    public static double radWithXAxis(Position p1, Position p2) {
        double r = distanceBetween(p1, p2);
        double rad = Math.acos((p2.x - p1.x) / r);
        if(p2.y > p1.y) {
            rad = 2 * Math.PI - rad;
        }
        return rad;
    }

    public static boolean between(double p, double left, double right) {
        return (p >= left) && (p <= right);
    }

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
