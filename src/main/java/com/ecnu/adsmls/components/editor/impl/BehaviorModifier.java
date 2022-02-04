package com.ecnu.adsmls.components.editor.impl;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.print.PageRange;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class BehaviorModifier {
    private Stage window;

    private GridPane gridPane;
    private ArrayList<Node[]> staticPage = new ArrayList<>();
    private ArrayList<Node[]> behaviorParamsPage = new ArrayList<>();

    private String behaviorName = "";
    private List<Pair<String, String>> paramsInfo;
    private List<Pair<String, String>> paramsValue = new ArrayList<>();

    private boolean valid = true;

    public BehaviorModifier() {
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
        cbBehavior.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.behaviorParamsPage.clear();
            this.behaviorName = newValue.toString();
            paramsInfo = BehaviorRegister.getParams(this.behaviorName);
            // 生成界面
            for(Pair<String, String> param : paramsInfo) {
                Label lbParamName = new Label(param.getKey());
                TextField tfParamValue = new TextField();
                this.behaviorParamsPage.add(new Node[] {lbParamName, tfParamValue});
            }
            this.updateGridPane();
        });

        Button btConfirm = new Button("Confirm");
        btConfirm.setOnAction(e -> {
            this.readParamsValue();
            this.checkParams();
            this.window.close();
        });

        Button btCancel = new Button("Cancel");
        btCancel.setOnAction(e -> {
            this.valid = false;
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
        this.gridPane.addRow(0, this.staticPage.get(0));

        int r = 1;
        for(; r < this.behaviorParamsPage.size(); ++r) {
            this.gridPane.addRow(r, this.behaviorParamsPage.get(r - 1));
        }

        this.gridPane.addRow(r, this.staticPage.get(1));

        window.sizeToScene();
    }

    public String getBehaviorName() {
        if(!valid) {
            return "";
        }
        return this.behaviorName;
    }

    public void readParamsValue() {
        String paramName = "";
        String paramValue = "";
        for(Node node : gridPane.getChildren()) {
            if(node instanceof Label) {
                paramName = ((Label) node).getText();
            }
            else if(node instanceof TextField) {
                paramValue = ((TextField) node).getText();
                this.paramsValue.add(new Pair<>(paramName, paramValue));
            }
        }
    }

    public List<Pair<String, String>> getParamsValue() {
        return paramsValue;
    }

    public void checkParams() {
        for(Pair<String, String> param : this.paramsValue) {
            if(Objects.equals(param.getValue(), "")) {
                this.valid = false;
                break;
            }
            // TODO 类型检查
        }
    }

    public String getBehaviorVO() {
        if(!this.valid) {
            return "";
        }
        StringBuilder res = new StringBuilder();
        res.append(this.behaviorName).append("\n");
        for(Pair<String, String> param : this.paramsValue) {
            res.append(param.getKey()).append(" = ").append(param.getValue()).append("\n");
        }
        return res.toString();
    }

    public Stage getWindow() {
        return window;
    }
}
