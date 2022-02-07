package com.ecnu.adsmls.components.editor.impl;


import com.ecnu.adsmls.components.Modal;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.StageStyle;

public class ProbabilityTransitionModal extends Modal {
    private ProbabilityTransition transition;

    private String weight;

    public ProbabilityTransitionModal(ProbabilityTransition transition) {
        super();
        this.transition = transition;
        this.loadData();

        this.setStyle("stageStyle", StageStyle.TRANSPARENT);
        this.setStyle("opacity", 0.87);
        this.setStyle("background",
                new Background(new BackgroundFill(
                        new LinearGradient(1, 1, 1, 0, true, CycleMethod.REFLECT,
                                new Stop(0.0, Color.LIGHTBLUE), new Stop(1.0, Color.WHITE)),
                        new CornerRadii(15), Insets.EMPTY)));
    }

    public String getWeight() {
        return weight;
    }

    private void loadData() {
        this.weight = transition.getWeight();
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
        if(this.valid) {
            transition.updateTreeTextPosition();
        }
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
