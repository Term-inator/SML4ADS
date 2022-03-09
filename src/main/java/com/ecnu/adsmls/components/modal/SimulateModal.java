package com.ecnu.adsmls.components.modal;


import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class SimulateModal extends Modal {
    private TreeView<String> treeView = new TreeView<>();

    private Map<String, LinkedHashMap<String, Boolean>> configuration = new HashMap<>();

    public Map<String, Boolean> getCarConfiguration() {
        return configuration.get("car");
    }

    public SimulateModal() {
        super();
    }

    // TODO 其他的 configuration

    @Override
    protected void createWindow() {
        super.createWindow();
        this.window.setTitle("Configurations");

        Label lbTrace = new Label("trace options");
        this.treeView.setPrefHeight(200);
        CheckBoxTreeItem<String> car = new CheckBoxTreeItem<>("car");

        car.getChildren().addAll(
                new CheckBoxTreeItem<>("x"),
                new CheckBoxTreeItem<>("y"),
                new CheckBoxTreeItem<>("v"),
                new CheckBoxTreeItem<>("deviation"),
                new CheckBoxTreeItem<>("road")
        );
        car.setExpanded(true);

        LinkedHashMap<String, Boolean> params = new LinkedHashMap<>();
        params.put("x", false);
        params.put("y", false);
        params.put("v", false);
        params.put("deviation", false);
        params.put("road", false);
        this.configuration.put("car", params);

        for(TreeItem<String> treeItem : car.getChildren()) {
            CheckBoxTreeItem<String> checkBoxTreeItem = (CheckBoxTreeItem<String>) treeItem;
            checkBoxTreeItem.selectedProperty().addListener((observable, oldVal, newVal) -> {
                System.out.println(checkBoxTreeItem.getValue() + " selection state: " + newVal);
                this.configuration.get("car").put(checkBoxTreeItem.getValue(), newVal);
            });
        }

        treeView.setCellFactory(CheckBoxTreeCell.forTreeView());

        this.treeView.setRoot(car);

        this.slot.addRow(0, lbTrace);
        this.slot.addRow(1, this.treeView);
    }

    @Override
    protected void update() {
        // 和其他 Modal 不同，这里不需要 update，因为有监听事件
    }

    @Override
    protected void check() {
    }

    @Override
    protected void then() {
    }
}
