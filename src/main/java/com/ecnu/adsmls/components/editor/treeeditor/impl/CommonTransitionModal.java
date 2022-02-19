package com.ecnu.adsmls.components.editor.treeeditor.impl;

import com.ecnu.adsmls.components.modal.Modal;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class CommonTransitionModal extends Modal {
    private CommonTransition transition;

    private TextArea taGuard;

    private List<String> guards = new ArrayList<>();

    public CommonTransitionModal(CommonTransition transition) {
        super();
        this.transition = transition;
        this.loadData();

        this.setStyle("stageStyle", StageStyle.TRANSPARENT);
        this.setStyle("opacity", 0.87);
        this.setStyle("background",
                new Background(new BackgroundFill(
                        new LinearGradient(1, 1, 1, 0, true, CycleMethod.REFLECT,
                                new Stop(0.0, Color.LIGHTBLUE), new Stop(1.0, Color.WHITE)),
                        new CornerRadii(15), Insets.EMPTY)));
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
        this.taGuard = new TextArea(this.transition.getInfo());
        this.taGuard.setPrefRowCount(10);
        this.taGuard.setPrefColumnCount(20);
        //自动换行
        this.taGuard.setWrapText(true);

        this.slot.addRow(0, lbGuard, taGuard);
    }

    @Override
    protected void check() {
        this.checkGuard();
    }

    @Override
    protected void update() {
        this.updateGuard();
    }

    @Override
    protected void then() {
        this.transition.updateTreeTextPosition();
    }

    private void checkGuard() {
        // TODO 类型检查
    }

    public void updateGuard() {
        this.guards.clear();
        String[] guards = this.taGuard.getText().split(";");
        for(String guard : guards) {
            guard = guard.replaceAll("[\r\n]", "");
            if(Objects.equals(guard, "")) {
                continue;
            }
            this.guards.add(guard.trim());
        }
    }
}
