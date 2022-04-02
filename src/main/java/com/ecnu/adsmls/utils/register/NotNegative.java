package com.ecnu.adsmls.utils.register;

public class NotNegative extends Between {
    public NotNegative() {
        super(0, Double.POSITIVE_INFINITY, "[)");
    }
}
