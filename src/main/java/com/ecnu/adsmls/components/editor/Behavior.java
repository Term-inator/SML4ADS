package com.ecnu.adsmls.components.editor;

import com.ecnu.adsmls.utils.Geometry;
import com.ecnu.adsmls.utils.Position;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Behavior extends Area {
    private double r = 16;

    public Behavior(long id, Position position) {
        super(id, position);
    }

    @Override
    public Position getLinkPoint(Position adjacentPoint) {
        Position center = getCenterPoint();
        double rad = Geometry.radWithXAxis(center, adjacentPoint);
        double x = this.r * Math.cos(rad);
        double y = - this.r * Math.sin(rad);
        return new Position(center.x + x, center.y + y);
    }

    @Override
    public Position getCenterPoint() {
        double x = this.position.x + this.r;
        double y = this.position.y + this.r;
        return new Position(x, y);
    }

    @Override
    public Node getNode() {
        Circle circle = new Circle();
        this.position.x -= this.r;
        this.position.y -= this.r;
        circle.setCenterX(this.position.x + this.r);
        circle.setCenterY(this.position.y + this.r);
        circle.setRadius(this.r);
        circle.setFill(Color.WHITE);
        circle.setStrokeWidth(2);
        circle.setStroke(Color.ROYALBLUE);

        graphicNode.getChildren().addAll(circle);
        this.enableDrag(graphicNode);

        return graphicNode;
    }
}
