package com.ecnu.adsmls.components.editor.treeeditor.impl;

import com.ecnu.adsmls.components.editor.treeeditor.TreeArea;
import com.ecnu.adsmls.utils.Position;
import com.ecnu.adsmls.utils.Vector2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;


public class Behavior extends TreeArea {
    private final double r = TreeAreaRadius.Behavior.getR();

    private String name = "";
    private LinkedHashMap<String, String> params = new LinkedHashMap<>();

    public Behavior(long id, Position position) {
        super(id, position);
        this.position.x -= this.r;
        this.position.y -= this.r;

        this.createNode();
        this.inactive();
        this.enableDrag();
    }

    @Override
    public Position getLinkPoint(Position adjacentPoint) {
        Position center = getCenterPoint();
        Vector2D vector = new Vector2D(center, adjacentPoint);
        double rad = vector.radWithXAxis();
        double x = this.r * Math.cos(rad);
        double y = -this.r * Math.sin(rad);
        return new Position(center.x + x, center.y + y);
    }

    @Override
    public Position getCenterPoint() {
        double x = this.position.x + this.r;
        double y = this.position.y + this.r;
        return new Position(x, y);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedHashMap<String, String> getParams() {
        return params;
    }

    public void setParams(LinkedHashMap<String, String> params) {
        this.params = params;
        this.getTreeText().setText(this.getInfo());
    }

    @Override
    public String getInfo() {
        StringBuilder res = new StringBuilder();
        res.append(this.name).append("\n");
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (!Objects.equals(param.getValue(), "")) {
                res.append(param.getKey()).append(" = ").append(param.getValue()).append("\n");
            }
        }
        return res.toString();
    }

    @Override
    public void active() {
        super.active();
    }

    @Override
    public void inactive() {
        super.inactive();
    }

    @Override
    public void createNode() {
        Circle circle = new Circle();
        circle.setRadius(this.r);
        circle.setFill(Color.WHITE);
        circle.setStrokeWidth(2);
        this.shape = circle;
    }

    @Override
    public void updatePosition() {
        ((Circle) this.shape).setCenterX(this.position.x + this.r);
        ((Circle) this.shape).setCenterY(this.position.y + this.r);
    }

    @Override
    public void updateNode() {
        this.updatePosition();
        this.addNode(this.shape);
    }
}
