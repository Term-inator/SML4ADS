package com.ecnu.adsmls.model;

import java.util.ArrayList;
import java.util.List;

public class MRequirements {
    private List<MRequirement> requirements = new ArrayList<>();

    public MRequirements(List<MRequirement> requirements) {
        this.requirements = requirements;
    }

    public MRequirements() {
    }

    public List<MRequirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<MRequirement> requirements) {
        this.requirements = requirements;
    }
}
