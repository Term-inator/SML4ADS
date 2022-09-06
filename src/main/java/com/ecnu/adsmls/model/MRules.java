package com.ecnu.adsmls.model;

import java.util.ArrayList;
import java.util.List;

public class MRules {
    private List<MRule> rules = new ArrayList<>();

    public MRules(List<MRule> rules) {
        this.rules = rules;
    }

    public MRules() {
    }

    public List<MRule> getRules() {
        return rules;
    }

    public void setRules(List<MRule> rules) {
        this.rules = rules;
    }
}
