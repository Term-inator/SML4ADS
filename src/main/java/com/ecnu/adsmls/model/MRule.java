package com.ecnu.adsmls.model;

import java.util.LinkedHashMap;

public class MRule {
    private String ruleType;

    private LinkedHashMap<String, String> ruleParams;

    public MRule(String ruleType, LinkedHashMap<String, String> ruleParams) {
        this.ruleType = ruleType;
        this.ruleParams = ruleParams;
    }

    public MRule() {
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public LinkedHashMap<String, String> getRuleParams() {
        return ruleParams;
    }

    public void setRuleParams(LinkedHashMap<String, String> ruleParams) {
        this.ruleParams = ruleParams;
    }
}
