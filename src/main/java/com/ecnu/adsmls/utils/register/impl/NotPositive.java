package com.ecnu.adsmls.utils.register.impl;

import com.ecnu.adsmls.utils.register.Value;

public class NotPositive extends Between {
    public NotPositive() {
        super(new Value(Double.NEGATIVE_INFINITY), new Value(0), "(]");
    }
}
