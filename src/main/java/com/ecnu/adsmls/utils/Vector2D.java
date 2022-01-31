package com.ecnu.adsmls.utils;

public class Vector2D {
    public Position start;
    public Position end;

    public Vector2D(Position start, Position end) {
        this.start = start;
        this.end = end;
    }

    public double getLength() {
        return Geometry.distanceBetween(this.start, this.end);
    }

    public double radWithXAxis() {
        return Geometry.radWithXAxis(this.start, this.end);
    }
}
