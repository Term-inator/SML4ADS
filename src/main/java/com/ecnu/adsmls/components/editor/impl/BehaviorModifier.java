package com.ecnu.adsmls.components.editor.impl;


import javafx.collections.FXCollections;
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
import javafx.util.Pair;

import java.util.*;


public class BehaviorModifier {
    private Stage window;

    private GridPane gridPane;
    private ArrayList<Node[]> staticPage = new ArrayList<>();
    private ArrayList<Node[]> behaviorParamsPage = new ArrayList<>();

    // 行为名
    private String behaviorName = "";
    // 行为参数信息
    private Map<String, String> paramsInfo = new LinkedHashMap<>();
    // 行为参数值
    private Map<String, String> paramsValue = new LinkedHashMap<>();

    // 行为参数填写是否合法
    private boolean valid = true;

    // 是否点击了确认
    private boolean confirm = true;

    public BehaviorModifier(Behavior behavior) {
        this.behaviorName = behavior.getName();
        this.paramsValue = behavior.getParams();

        window = new Stage(StageStyle.TRANSPARENT);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setOpacity(0.87);

        gridPane = new GridPane();
        gridPane.setPadding(new Insets(15, 20, 15, 20));
        gridPane.setVgap(8);
        //draw special window
        gridPane.setBackground(new Background(
                new BackgroundFill(
                        new LinearGradient(1, 1, 1, 0, true, CycleMethod.REFLECT,
                                new Stop(0.0, Color.LIGHTBLUE), new Stop(1.0, Color.WHITE)),
                        new CornerRadii(15),
                        Insets.EMPTY)));



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

        for(Map.Entry<String, String> param : paramsValue.entrySet()) {
            Label lbParamName = new Label(param.getKey());
            TextField tfParamValue = new TextField(param.getValue());
            this.behaviorParamsPage.add(new Node[] {lbParamName, tfParamValue});
        }

        Button btConfirm = new Button("Confirm");
        btConfirm.setOnAction(e -> {
            this.updateParamsValue();
            this.checkParams();
            if(this.valid) {
                this.window.close();
            }
        });

        Button btCancel = new Button("Cancel");
        btCancel.setOnAction(e -> {
            this.confirm = false;
            this.window.close();
        });

        staticPage.add(new Node[] {cbBehavior});
        staticPage.add(new Node[] {btConfirm, btCancel});

        this.updateGridPane();

        Scene scene = new Scene(gridPane);
        scene.setFill(Color.TRANSPARENT);

        window.setScene(scene);
    }

    private void updateGridPane() {
        this.gridPane.getChildren().clear();
        int rowIndex = 0;
        this.gridPane.addRow(rowIndex++, this.staticPage.get(0));

        for(int r = 0; r < this.behaviorParamsPage.size(); ++r) {
            this.gridPane.addRow(rowIndex++, this.behaviorParamsPage.get(r));
        }

        this.gridPane.addRow(rowIndex++, this.staticPage.get(1));

        window.sizeToScene();
    }

    public String getBehaviorName() {
        return this.behaviorName;
    }

    public void setBehaviorName(String behaviorName) {
        this.behaviorName = behaviorName;
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

    public Map<String, String> getParamsValue() {
        return paramsValue;
    }

    public void setParamsValue(Map<String, String> paramsValue) {
        this.paramsValue = paramsValue;
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

    public boolean isConfirm() {
        return this.confirm;
    }

    public String getBehaviorVO() {
        StringBuilder res = new StringBuilder();
        res.append(this.behaviorName).append("\n");
        for(Map.Entry<String, String> param : paramsValue.entrySet()) {
            res.append(param.getKey()).append(" = ").append(param.getValue()).append("\n");
        }
        return res.toString();
    }

    public Stage getWindow() {
        return window;
    }
}
