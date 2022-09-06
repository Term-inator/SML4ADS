package com.ecnu.adsmls.components.editor.ruleeditor;

import com.ecnu.adsmls.model.MRule;
import com.ecnu.adsmls.utils.register.Function;
import com.ecnu.adsmls.utils.register.FunctionParam;
import com.ecnu.adsmls.utils.register.Input;
import com.ecnu.adsmls.utils.register.exception.DataTypeException;
import com.ecnu.adsmls.utils.register.exception.EmptyParamException;
import com.ecnu.adsmls.utils.register.exception.RequirementException;
import com.ecnu.adsmls.utils.register.impl.LocationRegister;
import com.ecnu.adsmls.utils.register.impl.RuleRegister;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.LinkedHashMap;
import java.util.List;

public class RulePane {
    private GridPane gridPane = new GridPane();

    private ComboBox<String> cbRuleType;

    private GridPane gridPaneRule = new GridPane();
    private Function ruleFunction;
    LinkedHashMap<String, String> ruleParams = new LinkedHashMap<>();

    public RulePane(String ruleType) {
        this.ruleFunction = RuleRegister.getFunction(ruleType);
        this.createNode();
    }

    public void check() throws RequirementException, DataTypeException, EmptyParamException {
        this.ruleParams.clear();

        String ruleParamName = "";
        String ruleParamValue = "";
        for (Node node : this.gridPaneRule.getChildren()) {
            if (node instanceof Label) {
                ruleParamName = ((Label) node).getText();
            } else if (node.getUserData() instanceof Input) {
                ruleParamValue = ((Input) node.getUserData()).getValue();
                this.ruleParams.put(ruleParamName, ruleParamValue);
                this.ruleFunction.updateContext(ruleParamName, ruleParamValue);
            }
        }
        this.ruleFunction.check();
    }

    public MRule save() {
        MRule mRule = new MRule();

        mRule.setRuleType(this.cbRuleType.getValue());

        String ruleParamName = "";
        String ruleParamValue = "";
        for (Node node : this.gridPaneRule.getChildren()) {
            if (node instanceof Label) {
                ruleParamName = ((Label) node).getText();
            } else if (node.getUserData() instanceof Input) {
                ruleParamValue = ((Input) node.getUserData()).getValue();
                this.ruleParams.put(ruleParamName, ruleParamValue);
            }
        }

        mRule.setRuleParams(this.ruleParams);
        return mRule;
    }

    public void load(MRule mRule) {
        this.cbRuleType.getSelectionModel().select(mRule.getRuleType());

        String ruleParamName = "";
        String ruleParamValue = "";
        for (Node node : this.gridPaneRule.getChildren()) {
            if (node instanceof Label) {
                ruleParamName = ((Label) node).getText();
            } else if (node instanceof TextField) {
                ruleParamValue = mRule.getRuleParams().get(ruleParamName);
                ((TextField) node).setText(ruleParamValue);
            } else if (node instanceof ComboBox) {
                ruleParamValue = mRule.getRuleParams().get(ruleParamName);
                ((ComboBox) node).setValue(ruleParamValue);
            }
        }
    }

    public void createNode() {
        this.gridPane.setPadding(new Insets(4, 0, 4, 0));
        this.gridPane.setHgap(8);
        this.gridPane.setVgap(8);

        this.gridPaneRule.setPadding(new Insets(0, 0, 0, 4));
        this.gridPaneRule.setHgap(8);
        this.gridPaneRule.setVgap(8);

        Label lbRuleType = new Label("rule type");
        List<String> ruleTypes = RuleRegister.getFunctionNames();
        this.cbRuleType = new ComboBox<>(FXCollections.observableArrayList(ruleTypes));
        this.cbRuleType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.gridPaneRule.getChildren().clear();
            this.ruleFunction = RuleRegister.getFunction(newValue);
            this.ruleFunction.render(this.gridPaneRule);
        });
        this.cbRuleType.getSelectionModel().select(this.ruleFunction.getFunctionName());

        this.gridPane.addRow(0, lbRuleType, this.cbRuleType);
        this.gridPane.add(this.gridPaneRule, 0, 1, 2, 1);
    }

    public Node getNode() {
        return this.gridPane;
    }
}
