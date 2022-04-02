package com.ecnu.adsmls.utils.register;

public class NotZero implements Requirement {
    public NotZero() {
    }

    @Override
    public boolean check(String value) {
        return Double.parseDouble(value) != 0;
    }
}
