package com.ecnu.adsmls.components.editor;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Objects;

public class TreeEditor {
    private GridPane palette;
    final ToggleGroup group = new ToggleGroup();
    private String componentSelected;
    private Pane canvas;

    public TreeEditor() {
        palette = new GridPane();
        palette.setPadding(new Insets(8, 0, 0, 0));
        palette.setVgap(8);
        palette.setAlignment(Pos.TOP_CENTER);
        palette.setPrefWidth(100);
        canvas = new Pane();
        canvas.setPrefWidth(1200);
        canvas.setPrefHeight(800);
        initPalette();
        initCanvas();
    }

    private void initPalette() {
        ToggleButton tb0 = new ToggleButton("Behavior");
        tb0.setUserData("Behavior");
        ToggleButton tb1 = new ToggleButton("BranchPoint");
        tb1.setUserData("BranchPoint");
        ToggleButton tb2 = new ToggleButton("Transition");
        tb2.setUserData("Transition");

        palette.addRow(0, tb0);
        palette.addRow(1, tb1);
        palette.addRow(2, tb2);
        for(Node n : palette.getChildren()) {
            if(n instanceof ToggleButton) {
                ((ToggleButton) n).setToggleGroup(group);
            }
        }

        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if(newValue == null) {
                    componentSelected = null;
                }
                else {
                    componentSelected = (String) newValue.getUserData();
                }
            }
        });
    }

    public void initCanvas() {
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if(componentSelected == null) {
                return;
            }
            else {
                if(Objects.equals(componentSelected, "Behavior")) {
                    Position position = new Position(e.getX(), e.getY());
                    Behavior behavior = new Behavior(position);
                    canvas.getChildren().add(behavior.getNode());
                }
                // TODO
            }
        });
    }

    public Node getNode() {
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(palette, canvas);
        splitPane.setDividerPositions(.1f);
//        splitPane.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 255), null, null)));
        return splitPane;
    }
}
