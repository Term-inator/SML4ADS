package com.ecnu.adsmls.components.editor.impl;

import com.ecnu.adsmls.components.editor.TreeArea;
import com.ecnu.adsmls.components.editor.TreeText;
import com.ecnu.adsmls.utils.Position;
import com.ecnu.adsmls.utils.Vector2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;


public class Behavior extends TreeArea {
    private final double r = 16;

    private String name;
    private List<Pair<String, String>> params = new ArrayList<>();

    public Behavior(long id, Position position) {
        super(id, position);
    }

    @Override
    public Position getLinkPoint(Position adjacentPoint) {
        Position center = getCenterPoint();
        Vector2D vector = new Vector2D(center, adjacentPoint);
        double rad = vector.radWithXAxis();
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
    public void active() {

    }

    @Override
    public void inactive() {

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
        this.enableDrag();

        return graphicNode;
    }
}
