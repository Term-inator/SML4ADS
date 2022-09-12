package com.ecnu.adsmls.components.editor.treeeditor.impl;


import com.ecnu.adsmls.components.modal.Modal;
import com.ecnu.adsmls.utils.register.Function;
import com.ecnu.adsmls.utils.register.FunctionParam;
import com.ecnu.adsmls.utils.register.FunctionRegister;
import com.ecnu.adsmls.utils.register.exception.DataTypeException;
import com.ecnu.adsmls.utils.register.exception.EmptyParamException;
import com.ecnu.adsmls.utils.register.exception.RequirementException;
import com.ecnu.adsmls.utils.register.impl.BehaviorRegister;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.StageStyle;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class BehaviorModal extends Modal {
    private Behavior behavior;

    // 行为参数
    private GridPane behaviorParamsGridPane = new GridPane();

    private TextArea errorArea;

    // 行为名
    private String behaviorName = "";
    // 行为函数
    private Function behaviorFunction;
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
        this.paramsValue = (LinkedHashMap<String, String>) this.behavior.getParams().clone();

        this.behaviorFunction = FunctionRegister.getFunction(FunctionRegister.FunctionCategory.BEHAVIOR, this.behaviorName);
        int row = 0;
        for (Map.Entry<String, String> param : this.paramsValue.entrySet()) {
            FunctionParam functionParam = this.behaviorFunction.getParams().get(row);
            String labelName = param.getKey();
            if (Objects.equals(functionParam.getNecessity(), Function.Necessity.REQUIRED)) {
                labelName = labelName + "*";
            }
            Label lbParamName = new Label(labelName);
            TextField tfParamValue = new TextField(param.getValue());
            this.behaviorParamsGridPane.addRow(row++, lbParamName, tfParamValue);
        }
    }

    private void hideErrorArea() {
        this.slot.getChildren().remove(this.errorArea);
        this.window.sizeToScene();
    }

    private void showErrorArea(String errMsg) {
        this.slot.add(this.errorArea, 0, 2, 2, 1);
        this.errorArea.setText(errMsg);
        this.window.sizeToScene();
    }

    @Override
    protected void createWindow() {
        super.createWindow();

        this.behaviorParamsGridPane.setVgap(8);
        this.behaviorParamsGridPane.setHgap(5);

        Label lbBehaviorName = new Label("behavior");
        List<String> behaviorNames = FunctionRegister.getFunctionNames(FunctionRegister.FunctionCategory.BEHAVIOR);
        ComboBox<String> cbBehavior = new ComboBox<>(FXCollections.observableArrayList(behaviorNames));
        cbBehavior.getSelectionModel().select(this.behaviorName);
        cbBehavior.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.behaviorParamsGridPane.getChildren().clear();

            this.behaviorName = newValue;
            this.behaviorFunction = FunctionRegister.getFunction(FunctionRegister.FunctionCategory.BEHAVIOR, this.behaviorName);
            // 生成界面
            int row = 0;
            for (FunctionParam param : this.behaviorFunction.getParams()) {
                String labelName = param.getParamName();
                if (Objects.equals(param.getNecessity(), Function.Necessity.REQUIRED)) {
                    labelName = labelName + "*";
                }
                Label lbParamName = new Label(labelName);
                TextField tfParamValue = new TextField();
                this.behaviorParamsGridPane.addRow(row++, lbParamName, tfParamValue);
                // 自适应大小
                this.window.sizeToScene();
            }
        });
        this.errorArea = new TextArea();
        this.errorArea.setPrefWidth(200);
        this.errorArea.setPrefRowCount(2);
        this.errorArea.setEditable(false);
        this.hideErrorArea();

        this.slot.addRow(0, lbBehaviorName, cbBehavior);
        this.slot.add(this.behaviorParamsGridPane, 0, 1, 2, 1);
    }

    @Override
    protected void check() {
        try {
            this.behaviorFunction.check();
            this.hideErrorArea();
        } catch (EmptyParamException | DataTypeException | RequirementException e) {
            this.valid = false;
            this.showErrorArea(e.getMessage());
        }
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
        for (Node node : this.behaviorParamsGridPane.getChildren()) {
            if (node instanceof Label) {
                paramName = ((Label) node).getText();
                // 必填的参数对应的参数名
                if (paramName.charAt(paramName.length() - 1) == '*') {
                    paramName = paramName.substring(0, paramName.length() - 1);
                }
            } else if (node instanceof TextField) {
                paramValue = ((TextField) node).getText();
                this.paramsValue.put(paramName, paramValue);
                // 更新上下文
                this.behaviorFunction.updateContext(paramName, paramValue);
            }
        }
    }
}
