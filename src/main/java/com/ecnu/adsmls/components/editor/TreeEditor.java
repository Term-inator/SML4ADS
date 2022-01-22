package com.ecnu.adsmls.components.editor;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class TreeEditor {
    private VBox palette;
    private Canvas canvas;

    public TreeEditor() {
        palette = new VBox();
        palette.setAlignment(Pos.TOP_CENTER);
        palette.setPrefWidth(100);
        canvas = new Canvas();
        canvas.setWidth(1000);
        canvas.setHeight(800);
        initPalette();
    }

    private void initPalette() {
        Button bt1 = new Button("behavior");
        palette.getChildren().add(bt1);
    }

    public Node getNode() {
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(palette, canvas);
//        splitPane.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 255), null, null)));
        return splitPane;
    }
}
