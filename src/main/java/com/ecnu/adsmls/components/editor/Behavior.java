package com.ecnu.adsmls.components.editor;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class Behavior extends Area {
    private double width = 100;
    private double height = 100;
    private double arcWidth = 5;
    private double arcHeight = 5;

    public Behavior(Position position) {
        super(position);
    }

    @Override
    public Node getNode() {
        Rectangle rect = new Rectangle(width, height);
        rect.setX(position.x - width / 2);
        rect.setY(position.y - height / 2);
        rect.setArcWidth(arcWidth);
        rect.setArcHeight(arcHeight);
        rect.setFill(Color.WHITE);

        graphicNode.getChildren().addAll(rect);
        this.enableDrag(graphicNode);

        return graphicNode;
    }
}
