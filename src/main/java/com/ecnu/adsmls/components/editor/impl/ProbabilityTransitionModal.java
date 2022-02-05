package com.ecnu.adsmls.components.editor.impl;


import com.ecnu.adsmls.components.Modal;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ProbabilityTransitionModal extends Modal {
    private String weight;

    public ProbabilityTransitionModal(ProbabilityTransition transition) {
        super();

        this.weight = transition.getWeight();
    }

    public String getWeight() {
        return weight;
    }

    @Override
    protected void createWindow() {
        super.createWindow();

        Label lbWeight = new Label("weight");
        TextField tfWeight = new TextField(this.weight);

        staticPage.add(0, new Node[] {lbWeight, tfWeight});
    }

    @Override
    protected void confirm(ActionEvent e) {
        this.updateWeight();
        this.check();
    }

    @Override
    protected void check() {
        this.checkGuard();
    }

    private void checkGuard() {
        // TODO 类型检查
    }

    public void updateWeight() {
        for(Node node : gridPane.getChildren()) {
            if(node instanceof TextField) {
                this.weight = ((TextField) node).getText();
            }
        }
    }

    public String getProbabilityTransitionVO() {
        return this.weight;
    }
}
