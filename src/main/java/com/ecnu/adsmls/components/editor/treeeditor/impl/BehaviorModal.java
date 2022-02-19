package com.ecnu.adsmls.components.editor.treeeditor.impl;


import com.ecnu.adsmls.components.modal.Modal;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.StageStyle;

import java.util.*;


public class BehaviorModal extends Modal {
    private Behavior behavior;

    // 行为参数
    private GridPane behaviorParamsGridPane = new GridPane();

    // 行为名
    private String behaviorName = "";
    // 行为参数信息
    private LinkedHashMap<String, String> paramsInfo = new LinkedHashMap<>();
    // 行为参数值
    private LinkedHashMap<String, String> paramsValue = new LinkedHashMap<>();


    public BehaviorModal(Behavior behavior) {
        super();
        this.behavior = behavior;
        this.loadData();

        this.setStyle("stageStyle", StageStyle.TRANSPARENT);
        this.setStyle("opacity", 0.87);
        this.setStyle("background",
                new Background(new BackgroundFill(
                        new LinearGradient(1, 1, 1, 0, true, CycleMethod.REFLECT,
                                new Stop(0.0, Color.LIGHTBLUE), new Stop(1.0, Color.WHITE)),
                        new CornerRadii(15), Insets.EMPTY)));
    }

    public String getBehaviorName() {
        return this.behaviorName;
    }

    public LinkedHashMap<String, String> getParamsValue() {
        return paramsValue;
    }

    private void loadData() {
        this.behaviorName = this.behavior.getName();
        this.paramsValue = this.behavior.getParams();

        int row = 0;
        for(Map.Entry<String, String> param : paramsValue.entrySet()) {
            Label lbParamName = new Label(param.getKey());
            TextField tfParamValue = new TextField(param.getValue());
            this.behaviorParamsGridPane.addRow(row++, lbParamName, tfParamValue);
        }
    }

    @Override
    protected void createWindow() {
        super.createWindow();

        this.behaviorParamsGridPane.setVgap(8);
        this.behaviorParamsGridPane.setHgap(5);

        Label lbBehaviorName = new Label("behavior");
        List<String> behaviorNames = BehaviorRegister.getBehaviorNames();
        ComboBox<String> cbBehavior = new ComboBox<>(FXCollections.observableArrayList(behaviorNames));
        cbBehavior.getSelectionModel().select(this.behaviorName);
        cbBehavior.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.behaviorParamsGridPane.getChildren().clear();

            this.behaviorName = newValue;
            this.paramsInfo = BehaviorRegister.getParams(this.behaviorName);
            // 生成界面
            int row = 0;
            for(Map.Entry<String, String> param : paramsInfo.entrySet()) {
                Label lbParamName = new Label(param.getKey());
                TextField tfParamValue = new TextField();
                this.behaviorParamsGridPane.addRow(row++, lbParamName, tfParamValue);
                // 自适应大小
                this.window.sizeToScene();
            }
        });
        this.slot.addRow(0, lbBehaviorName, cbBehavior);
        this.slot.add(this.behaviorParamsGridPane, 0, 1, 2, 1);
    }

    @Override
    protected void check() {
        this.checkParams();
    }

    @Override
    protected void update() {
        this.updateParamsValue();
    }

    @Override
    protected void then() {
        this.behavior.updateTreeTextPosition();
    }

    public void updateParamsValue() {
        this.paramsValue.clear();
        String paramName = "";
        String paramValue = "";
        for(Node node : this.behaviorParamsGridPane.getChildren()) {
            if(node instanceof Label) {
                paramName = ((Label) node).getText();
            }
            else if(node instanceof TextField) {
                paramValue = ((TextField) node).getText();
                this.paramsValue.put(paramName, paramValue);
            }
        }
    }

    public void checkParams() {
        for(Map.Entry<String, String> param : paramsValue.entrySet()) {
            if(Objects.equals(param.getValue(), "")) {
                this.valid = false;
                break;
            }
            // TODO 类型检查
        }
    }
}
