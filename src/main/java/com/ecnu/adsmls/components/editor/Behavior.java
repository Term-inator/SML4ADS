package com.ecnu.adsmls.components.editor;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Behavior extends Area {
    private double width = 150;
    private double height = 100;
    private double arcWidth = 5;
    private double arcHeight = 5;

    public Behavior(Position position) {
        super(position);
    }

    @Override
    public Position getLinkPoint(Position nextPoint) {
        Position center = getCenterPoint();
        return null;
    }

    public Position getCenterPoint() {
        double x = this.position.x + this.width / 2;
        double y = this.position.y + this.height / 2;
        return new Position(x, y);
    }

    @Override
    public Node getNode() {
        Rectangle rect = new Rectangle(width, height);
        rect.setX(position.x - width / 2);
        rect.setY(position.y - height / 2);
        rect.setArcWidth(arcWidth);
        rect.setArcHeight(arcHeight);
        rect.setFill(Color.WHITE);
        rect.setStroke(Color.LIGHTBLUE);

        graphicNode.getChildren().addAll(rect);
        this.enableDrag(graphicNode);

        return graphicNode;
    }


}
