package com.ecnu.adsmls.components.editor.impl;

import com.ecnu.adsmls.components.Modal;
import com.ecnu.adsmls.utils.Position;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class CommonTransitionModal extends Modal {
    private CommonTransition transition;

    private List<String> guards = new ArrayList<>();

    public CommonTransitionModal(CommonTransition transition) {
        super();
        this.transition = transition;
        this.loadData();
    }

    public List<String> getGuards() {
        return guards;
    }

    private void loadData() {
        this.guards = transition.getGuards();
    }

    @Override
    protected void createWindow() {
        super.createWindow();

        Label lbGuard = new Label("guard");
        TextArea taGuard = new TextArea(this.getCommonTransitionVO());
        taGuard.setPrefRowCount(10);
        taGuard.setPrefColumnCount(20);
        //自动换行
        taGuard.setWrapText(true);

        staticPage.add(0, new Node[] {lbGuard, taGuard});
    }

    @Override
    protected void confirm(ActionEvent e) {
        this.updateGuard();
        this.check();
        if(this.valid) {
            transition.updateTreeTextPosition();
        }
    }

    @Override
    protected void check() {
        this.checkGuard();
    }

    private void checkGuard() {
        // TODO 类型检查
    }

    public void updateGuard() {
        this.guards.clear();
        for(Node node : gridPane.getChildren()) {
            if(node instanceof TextArea) {
                String[] guards = ((TextArea) node).getText().split(";");
                for(String guard : guards) {
                    guard = guard.replaceAll("[\r\n]", "");
                    if(Objects.equals(guard, "")) {
                        continue;
                    }
                    this.guards.add(guard.trim());
                }
            }
        }
    }

    public String getCommonTransitionVO() {
        StringBuilder res = new StringBuilder();
        for(String guard : this.guards) {
            res.append(guard).append(";\n");
        }
        return res.toString();
    }
}
