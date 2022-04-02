package com.ecnu.adsmls.utils.register;

public class NotPositive extends Between {
    public NotPositive() {
        super(new Value(Double.NEGATIVE_INFINITY), new Value(0), "(]");
    }
}
