package com.ecnu.adsmls.components.editor;

import com.ecnu.adsmls.utils.Position;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class TreeLinkPoint extends Area {
    private double r = 5;

    public TreeLinkPoint(Position position) {
        super(position);
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
        this.enableDrag(graphicNode);

        return graphicNode;
    }
}
