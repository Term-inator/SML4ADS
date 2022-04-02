package com.ecnu.adsmls.utils.register;

public class NotPositive extends Between {
    public NotPositive() {
        super(Double.NEGATIVE_INFINITY, 0, "(]");
    }
}
