package com.ecnu.adsmls.utils;

public class Position {
    public double x;
    public double y;

    public Position() {

    }

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void relocate(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
