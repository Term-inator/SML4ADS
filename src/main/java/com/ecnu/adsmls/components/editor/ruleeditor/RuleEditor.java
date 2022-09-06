package com.ecnu.adsmls.components.editor.ruleeditor;

import com.alibaba.fastjson.JSON;
import com.ecnu.adsmls.components.editor.FormEditor;
import com.ecnu.adsmls.model.MRule;
import com.ecnu.adsmls.model.MRules;
import com.ecnu.adsmls.utils.FileSystem;
import com.ecnu.adsmls.utils.GridPaneUtils;
import com.ecnu.adsmls.utils.register.exception.DataTypeException;
import com.ecnu.adsmls.utils.register.exception.EmptyParamException;
import com.ecnu.adsmls.utils.register.exception.RequirementException;
import com.ecnu.adsmls.utils.register.impl.RuleRegister;
import javafx.css.Rule;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class RuleEditor extends FormEditor {
    private GridPane gridPaneRule = new GridPane();
    private Map<Integer, RulePane> rulePanes = new LinkedHashMap<>();
    private int ruleId = 0;

    public RuleEditor(String projectPath, File file) {
        super(projectPath, file);
        this.createNode();
    }

    @Override
    public void check() throws EmptyParamException, RequirementException, DataTypeException {
        // empty
    }

    @Override
    public void save() {
        String rules = FileSystem.JSONReader(new File(this.projectPath, this.relativePath));
        MRules mRules = JSON.parseObject(rules, MRules.class);
        if(mRules == null) {
            mRules = new MRules();
        }
        System.out.println(mRules);

        List<MRule> ruleList = new ArrayList<>();
        for(Map.Entry<Integer, RulePane> entry: this.rulePanes.entrySet()) {
            RulePane rulePane = entry.getValue();
            ruleList.add(rulePane.save());
        }
        mRules.setRules(ruleList);
        rules = JSON.toJSONString(mRules);
        FileSystem.JSONWriter(new File(this.projectPath, this.relativePath), rules);
    }

    @Override
    public void load() {
        String rules = FileSystem.JSONReader(new File(this.projectPath, this.relativePath));
        MRules mRules = JSON.parseObject(rules, MRules.class);
        if (mRules == null) {
            return;
        }
        System.out.println(rules);

        for (MRule mRule: mRules.getRules()) {
            RulePane rulePane = new RulePane(RuleRegister.getFunction(mRule.getRuleType()));
            // 设置 rulePane 的数据
            rulePane.load(mRule);
            this.newRule(rulePane);
        }
    }

    @Override
    protected void createNode() {
        super.createNode();
        this.bindKeyEvent();
        this.bindMouseEvent();

        Label lbRules = new Label("rules");

        Button btAddRule = new Button("Add");
        btAddRule.setOnMouseClicked(e -> {
            // 缺省值
            this.newRule(new RulePane(RuleRegister.getFunction(RuleRegister.getFunctionNames().get(0))));
        });

        this.gridPane.addRow(0, lbRules);
        this.gridPane.addRow(1, this.gridPaneRule);
        this.gridPane.addRow(2, btAddRule);
    }

    private void newRule(RulePane rulePane) {
        this.rulePanes.put(this.ruleId++, rulePane);
        this.updateRules();
    }

    private void deleteRule(int index) {
        System.out.println("delete rule " + index);
        RulePane rulePane = this.rulePanes.remove(index);
        this.updateRules();
    }

    private void updateRules() {
        List<Node[]> page = new ArrayList<>();

        int i = 0;
        for(Map.Entry<Integer, RulePane> entry: this.rulePanes.entrySet()) {
            RulePane rule = entry.getValue();
            AnchorPane buttonWrapper = new AnchorPane();
            Button btDelete = new Button("Delete");
            btDelete.setOnAction(e -> {
                this.deleteRule(entry.getKey());
            });
            buttonWrapper.getChildren().add(btDelete);
            AnchorPane.setTopAnchor(btDelete, 4.0);
            AnchorPane.setLeftAnchor(btDelete, 8.0);
            page.add(new Node[] {rule.getNode(), buttonWrapper});
            ++i;
        }

        GridPaneUtils.updateGridPane(this.gridPaneRule, page);
    }

    @Override
    public Node getNode() {
        return this.gridPane;
    }
}
