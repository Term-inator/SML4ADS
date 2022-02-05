package com.ecnu.adsmls.components.editor.impl;

import java.util.ArrayList;
import java.util.List;

public class CommonTransition extends Transition {
    private List<String> guards = new ArrayList<>();

    public CommonTransition(long id) {
        super(id);
    }

    public List<String> getGuards() {
        return guards;
    }

    public void setGuards(List<String> guards) {
        this.guards = guards;
    }
}
