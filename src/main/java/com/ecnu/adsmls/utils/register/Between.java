package com.ecnu.adsmls.utils.register;

import com.ecnu.adsmls.utils.Geometry;

public class Between implements Requirement {
    private double l;
    private double r;
    private String op;

    public Between(double l, double r, String op) {
        this.l = l;
        this.r = r;
        this.op = op;
    }

    @Override
    public boolean check(String value) {
        return Geometry.between(Double.parseDouble(value), this.l, this.r, this.op);
    }
}
