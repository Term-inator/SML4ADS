package com.ecnu.adsmls.components.modal;


import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;

import java.util.ArrayList;
import java.util.List;


public class SimulateModal extends Modal {
    private TreeView<String> treeView = new TreeView<>();


    @Override
    protected void createWindow() {
        super.createWindow();
        Label lbTrace = new Label("trace options");
        this.treeView.setPrefHeight(200);
        CheckBoxTreeItem<String> car = new CheckBoxTreeItem<>("car");

        List<CheckBoxTreeItem<String>> carTreeItems = new ArrayList<>();
        carTreeItems.add(new CheckBoxTreeItem<>("x"));
        carTreeItems.add(new CheckBoxTreeItem<>("y"));
        carTreeItems.add(new CheckBoxTreeItem<>("v"));
        carTreeItems.add(new CheckBoxTreeItem<>("deviation"));
        carTreeItems.add(new CheckBoxTreeItem<>("road"));

        for(CheckBoxTreeItem<String> treeItem : carTreeItems) {
            treeItem.selectedProperty().addListener((observable, oldVal, newVal) -> {
                System.out.println(treeItem.getValue() + " selection state: " + newVal);
            });
        }

        car.getChildren().addAll(carTreeItems);
        car.setExpanded(true);

        treeView.setCellFactory(CheckBoxTreeCell.forTreeView());

        this.treeView.setRoot(car);

        this.slot.addRow(0, lbTrace);
        this.slot.addRow(1, this.treeView);
    }

    @Override
    protected void update() {

    }

    @Override
    protected void check() {

    }

    @Override
    protected void then() {

    }
}
