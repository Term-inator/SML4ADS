package com.ecnu.adsmls.utils.register.impl;

import com.ecnu.adsmls.utils.register.Requirement;
import com.ecnu.adsmls.utils.register.exception.RequirementException;

import java.util.Map;

public class NotZero implements Requirement {
    public NotZero() {
    }

    @Override
    public void check(Map<String, String> context, String value) throws RequirementException {
        if(Double.parseDouble(value) == 0) {
            throw new RequirementException(" should not be zero.");
        }
    }
}
