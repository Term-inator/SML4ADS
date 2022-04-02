package com.ecnu.adsmls.utils.register;

public class Positive extends Between {
    public Positive() {
        super(new Value(0), new Value(Double.POSITIVE_INFINITY), "()");
    }
}