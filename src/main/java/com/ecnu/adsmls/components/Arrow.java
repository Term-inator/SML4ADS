package com.ecnu.adsmls.components;

import com.ecnu.adsmls.utils.Geometry;
import com.ecnu.adsmls.utils.Position;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class Arrow {
    private Group graphicNode;

    private Position endPoint;
    private double rad;
    private double arrowLen;

    private double angle = Math.PI / 3;

    public Arrow(Position endPoint, double rad, double arrowLen) {
        graphicNode = new Group();
        this.endPoint = endPoint;
        this.rad = rad;
        this.arrowLen = arrowLen;
    }

    public void relocate(Position endPoint, double rad) {
        this.endPoint = endPoint;
        this.rad = rad;
    }

    public Node getNode() {
        double rad1 = this.rad - Math.PI / 6;
        double rad2 = this.rad + Math.PI / 6;
        double x1 = this.arrowLen * Math.cos(rad1) + this.endPoint.x;
        double y1 = - this.arrowLen * Math.sin(rad1) + this.endPoint.y;
        double x2 = this.arrowLen * Math.cos(rad2) + this.endPoint.x;
        double y2 = - this.arrowLen * Math.sin(rad2) + this.endPoint.y;
        System.out.println(this.rad);
        System.out.println(Math.cos(rad1));
        Path path = new Path();
        path.getElements().add(new MoveTo(x1, y1));
        path.getElements().add(new LineTo(this.endPoint.x, this.endPoint.y));
        path.getElements().add(new LineTo(x2, y2));

        path.setStrokeWidth(2);
        path.setStroke(Color.ROYALBLUE);

        graphicNode.getChildren().addAll(path);

        return graphicNode;
    }
}
