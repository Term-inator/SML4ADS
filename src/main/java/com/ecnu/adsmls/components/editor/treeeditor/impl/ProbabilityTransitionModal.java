package com.ecnu.adsmls.components.editor.treeeditor.impl;


import com.ecnu.adsmls.components.modal.Modal;
import javafx.geometry.Insets;
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

    private TextField tfWeight;

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
        this.tfWeight = new TextField(this.weight);

        this.slot.addRow(0, lbWeight, this.tfWeight);
    }

    @Override
    protected void check() {
        this.checkWeight();
    }

    @Override
    protected void update() {
        this.updateWeight();
    }

    @Override
    protected void then() {
        this.transition.updateTreeTextPosition();
    }

    private void checkWeight() {
        try {
            Integer.parseInt(this.weight);
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("Weight should be Integer.");
            this.valid = false;
        }
    }

    public void updateWeight() {
        this.weight = this.tfWeight.getText();
    }
}
