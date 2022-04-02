package com.ecnu.adsmls.utils.register;

public class Negative extends Between {
    public Negative() {
        super(new Value(Double.NEGATIVE_INFINITY), new Value(0), "()");
    }
}
