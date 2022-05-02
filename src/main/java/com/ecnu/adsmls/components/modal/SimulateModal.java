package com.ecnu.adsmls.components.modal;


import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;


public class SimulateModal extends Modal {
    // 数据生成选项
//    private TreeView<String> treeView = new TreeView<>();
//
//    private Map<String, LinkedHashMap<String, Boolean>> configuration = new HashMap<>();
//
//    public Map<String, Boolean> getCarConfiguration() {
//        return configuration.get("car");
//    }
    private enum Mode {
        SCENE("scene"),
        SCENARIO("scenario");

        public String value;
        Mode(String value) {
            this.value = value;
        }
    }

    Mode mode;
    RadioButton rbScene;
    RadioButton rbScenario;
    String scenarioNum = "";
    TextField tfScenarioNum;

    public SimulateModal() {
        super();
    }

    public String getMode() {
        return mode.value;
    }

    public boolean isScene() {
        return Objects.equals(this.mode, Mode.SCENE);
    }

    public boolean isScenario() {
        return Objects.equals(this.mode, Mode.SCENARIO);
    }

    public String getScenarioNum() {
        return scenarioNum;
    }

    // TODO 其他的 configuration

    @Override
    protected void createWindow() {
        super.createWindow();
        this.window.setTitle("Configurations");

//        Label lbTrace = new Label("trace options");
//        this.treeView.setPrefHeight(200);
//        CheckBoxTreeItem<String> car = new CheckBoxTreeItem<>("car");
//
//        car.getChildren().addAll(
//                new CheckBoxTreeItem<>("x"),
//                new CheckBoxTreeItem<>("y"),
//                new CheckBoxTreeItem<>("v"),
//                new CheckBoxTreeItem<>("deviation"),
//                new CheckBoxTreeItem<>("road")
//        );
//        car.setExpanded(true);
//
//        LinkedHashMap<String, Boolean> params = new LinkedHashMap<>();
//        params.put("x", false);
//        params.put("y", false);
//        params.put("v", false);
//        params.put("deviation", false);
//        params.put("road", false);
//        this.configuration.put("car", params);
//
//        for(TreeItem<String> treeItem : car.getChildren()) {
//            CheckBoxTreeItem<String> checkBoxTreeItem = (CheckBoxTreeItem<String>) treeItem;
//            checkBoxTreeItem.selectedProperty().addListener((observable, oldVal, newVal) -> {
//                System.out.println(checkBoxTreeItem.getValue() + " selection state: " + newVal);
//                this.configuration.get("car").put(checkBoxTreeItem.getValue(), newVal);
//            });
//        }
//
//        treeView.setCellFactory(CheckBoxTreeCell.forTreeView());
//
//        this.treeView.setRoot(car);
//
//        this.slot.addRow(0, lbTrace);
//        this.slot.addRow(1, this.treeView);

        Label lbMode = new Label("mode");
        ToggleGroup rbGroup = new ToggleGroup();
        this.rbScene = new RadioButton("scene");
        this.rbScene.setToggleGroup(rbGroup);
        this.rbScenario = new RadioButton("scenario");
        this.rbScenario.setToggleGroup(rbGroup);

        Label lbScenarioNum = new Label("image num");
        this.tfScenarioNum = new TextField();
        this.tfScenarioNum.setPromptText("Integer");

        rbGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if(Objects.equals(newValue, this.rbScene)) {
                this.slot.add(lbScenarioNum, 1, 1, 1, 1);
                this.slot.add(this.tfScenarioNum, 2, 1, 1, 1);
            }
            else if(Objects.equals(newValue, this.rbScenario)) {
                this.slot.getChildren().removeAll(lbScenarioNum, tfScenarioNum);
            }
        });
        this.rbScene.setSelected(true);

        this.slot.addRow(0, lbMode);
        this.slot.add(this.rbScene, 0, 1, 1, 1);
        this.slot.addRow(2, this.rbScenario);
    }

    @Override
    protected void update() {
        if(this.rbScene.isSelected()) {
            this.mode = Mode.SCENE;
            this.scenarioNum = this.tfScenarioNum.getText();
        }
        else if(this.rbScenario.isSelected()) {
            this.mode = Mode.SCENARIO;
        }
    }

    @Override
    protected void check() {
        if(this.isScene()) {
            this.scenarioNumCheck();
        }
    }

    @Override
    protected void then() {
    }

    private void scenarioNumCheck() {
        try {
            Integer.parseInt(this.scenarioNum);
        }
        catch (Exception e) {
            e.printStackTrace();
            this.valid = false;
        }
    }
}
