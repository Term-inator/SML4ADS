package com.ecnu.adsmls.components.editor;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

public class Behavior extends ClosedShape {
    private double width = 20;
    private double height = 20;
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

        graphicNode.getChildren().addAll(rect);
        this.enableDrag(graphicNode);

        return graphicNode;
    }
}
