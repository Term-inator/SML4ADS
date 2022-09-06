package com.ecnu.adsmls.components.editor.ruleeditor;

import com.ecnu.adsmls.model.MRule;
import com.ecnu.adsmls.utils.register.Function;
import com.ecnu.adsmls.utils.register.Input;
import com.ecnu.adsmls.utils.register.exception.DataTypeException;
import com.ecnu.adsmls.utils.register.exception.EmptyParamException;
import com.ecnu.adsmls.utils.register.exception.RequirementException;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.LinkedHashMap;

public class RulePane {
    private GridPane gridPane = new GridPane();

    LinkedHashMap<String, String> ruleParams = new LinkedHashMap<>();

    private Function ruleFunction;

    public RulePane(Function ruleFunction) {
        this.ruleFunction = ruleFunction;
        this.createNode();
    }

    public void check() throws RequirementException, DataTypeException, EmptyParamException {
        this.ruleParams.clear();

        String ruleParamName = "";
        String ruleParamValue = "";
        for (Node node : this.gridPane.getChildren()) {
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

        String ruleParamName = "";
        String ruleParamValue = "";
        for (Node node : this.gridPane.getChildren()) {
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
        String ruleParamName = "";
        String ruleParamValue = "";
        for (Node node : this.gridPane.getChildren()) {
            if (node instanceof Label) {
                ruleParamName = ((Label) node).getText();
            } else if (node instanceof TextField) {
                ruleParamValue = mRule.getRuleParams().get(ruleParamName);
                ((TextField) node).setText(ruleParamValue);
            }
        }
    }

    public void createNode() {
        this.gridPane.setPadding(new Insets(4, 0, 4, 0));
        this.gridPane.setHgap(8);

        this.ruleFunction.render(this.gridPane);
    }

    public Node getNode() {
        return this.gridPane;
    }
}
