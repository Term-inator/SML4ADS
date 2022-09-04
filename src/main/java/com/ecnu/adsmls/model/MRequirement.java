package com.ecnu.adsmls.model;

import java.util.LinkedHashMap;

public class MRequirement {
    public String requirementType;

    public String requirement;

    public MRequirement(String requirementType, String requirement) {
        this.requirementType = requirementType;
        this.requirement = requirement;
    }

    public MRequirement() {
    }

    public String getRequirementType() {
        return requirementType;
    }

    public void setRequirementType(String requirementType) {
        this.requirementType = requirementType;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }
}
