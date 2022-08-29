package com.ecnu.adsmls.utils.register.impl;

import com.ecnu.adsmls.utils.Geometry;
import com.ecnu.adsmls.utils.register.Reference;
import com.ecnu.adsmls.utils.register.Requirement;
import com.ecnu.adsmls.utils.register.Value;
import com.ecnu.adsmls.utils.register.exception.RequirementException;

import java.util.Map;

public class Between implements Requirement {
    private Object l;
    private Object r;
    private String op;

    public Between(Object l, Object r, String op) {
        this.l = l;
        this.r = r;
        this.op = op;
    }

    @Override
    public void check(Map<String, String> context, String value) throws RequirementException {
        double lVal = .0, rVal = .0;
        if (this.l instanceof Value) {
            lVal = ((Value) l).getValue().doubleValue();
        } else if (this.l instanceof Reference) {
            lVal = Double.parseDouble(context.get(((Reference) l).getId()));
        }

        if (this.r instanceof Value) {
            rVal = ((Value) r).getValue().doubleValue();
        } else if (this.r instanceof Reference) {
            rVal = Double.parseDouble(context.get(((Reference) r).getId()));
        }
        if (!Geometry.between(Double.parseDouble(value), lVal, rVal, this.op)) {
            throw new RequirementException(" should be in range " + this.op.charAt(0) + lVal + ", " + rVal + this.op.charAt(1));
        }
    }
}
