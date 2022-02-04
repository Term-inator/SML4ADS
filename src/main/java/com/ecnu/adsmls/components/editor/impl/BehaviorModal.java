package com.ecnu.adsmls.components.editor.impl;


import com.ecnu.adsmls.components.Modal;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.*;


public class BehaviorModal extends Modal {
    private ArrayList<Node[]> behaviorParamsPage = new ArrayList<>();

    // 行为名
    private String behaviorName = "";
    // 行为参数信息
    private LinkedHashMap<String, String> paramsInfo = new LinkedHashMap<>();
    // 行为参数值
    private LinkedHashMap<String, String> paramsValue = new LinkedHashMap<>();


    public BehaviorModal(Behavior behavior) {
        super();
        this.behaviorName = behavior.getName();
        this.paramsValue = behavior.getParams();

        for(Map.Entry<String, String> param : paramsValue.entrySet()) {
            Label lbParamName = new Label(param.getKey());
            TextField tfParamValue = new TextField(param.getValue());
            this.behaviorParamsPage.add(new Node[] {lbParamName, tfParamValue});
        }
    }

    public String getBehaviorName() {
        return this.behaviorName;
    }

    public LinkedHashMap<String, String> getParamsValue() {
        return paramsValue;
    }

    @Override
    protected void createWindow() {
        super.createWindow();

        List<String> behaviorNames = BehaviorRegister.getBehaviorNames();
        ComboBox cbBehavior = new ComboBox(FXCollections.observableArrayList(behaviorNames));
        cbBehavior.getSelectionModel().select(this.behaviorName);
        cbBehavior.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.behaviorParamsPage.clear();
            this.behaviorName = newValue.toString();
            this.paramsInfo = BehaviorRegister.getParams(this.behaviorName);
            // 生成界面
            for(Map.Entry<String, String> param : paramsInfo.entrySet()) {
                Label lbParamName = new Label(param.getKey());
                TextField tfParamValue = new TextField();
                this.behaviorParamsPage.add(new Node[] {lbParamName, tfParamValue});
            }
            this.updateGridPane();
        });
        staticPage.add(0, new Node[] {cbBehavior});
    }

    @Override
    protected void confirm(ActionEvent e) {
        this.updateParamsValue();
        this.checkParams();
    }

    @Override
    protected void check() {
        this.checkParams();
    }

    @Override
    protected void updateGridPane() {
        this.gridPane.getChildren().clear();
        int rowIndex = 0;
        this.gridPane.addRow(rowIndex++, this.staticPage.get(0));

        for(int r = 0; r < this.behaviorParamsPage.size(); ++r) {
            this.gridPane.addRow(rowIndex++, this.behaviorParamsPage.get(r));
        }

        this.gridPane.addRow(rowIndex++, this.staticPage.get(1));

        window.sizeToScene();
    }


    public void updateParamsValue() {
        this.paramsValue.clear();
        String paramName = "";
        String paramValue = "";
        for(Node node : gridPane.getChildren()) {
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

    // 显示在界面上的字符串
    public String getBehaviorVO() {
        StringBuilder res = new StringBuilder();
        res.append(this.behaviorName).append("\n");
        for(Map.Entry<String, String> param : paramsValue.entrySet()) {
            res.append(param.getKey()).append(" = ").append(param.getValue()).append("\n");
        }
        return res.toString();
    }
}
