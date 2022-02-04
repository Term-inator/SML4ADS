package com.ecnu.adsmls.components.editor.impl;

import com.ecnu.adsmls.components.Modal;
import javafx.event.ActionEvent;

import java.util.ArrayList;
import java.util.List;

public class CommonTransitionModal extends Modal {
    private List<String> guards = new ArrayList<>();

    public CommonTransitionModal(CommonTransition transition) {
        super();
    }

    @Override
    protected void confirm(ActionEvent e) {

    }

    @Override
    protected void check() {

    }
}
