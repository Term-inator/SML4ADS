package com.ecnu.adsmls.components.editor;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class TreeEditor {
    private VBox palette;
    private Pane canvas;

    public TreeEditor() {
        palette = new VBox();
        palette.setAlignment(Pos.TOP_CENTER);
        palette.setPrefWidth(100);
        canvas = new Pane();
        canvas.setPrefWidth(1000);
        canvas.setPrefHeight(800);
        initPalette();
    }

    private void initPalette() {
        Button bt1 = new Button("behavior");
        bt1.setOnMouseClicked(e -> {
            Behavior behavior = new Behavior(new Position(100, 100));
            canvas.getChildren().add(behavior.getNode());
        });
        palette.getChildren().add(bt1);
    }

    public Node getNode() {
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(palette, canvas);
        splitPane.setDividerPositions(.1f);
//        splitPane.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 255), null, null)));
        return splitPane;
    }
}
