package com.ecnu.adsmls.utils.register.impl;

import com.ecnu.adsmls.utils.register.Value;

public class NotNegative extends Between {
    public NotNegative() {
        super(new Value(0), new Value(Double.POSITIVE_INFINITY), "[)");
    }
}
