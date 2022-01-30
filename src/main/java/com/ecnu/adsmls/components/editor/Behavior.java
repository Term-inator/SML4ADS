package com.ecnu.adsmls.components.editor;

import com.ecnu.adsmls.utils.Geometry;
import com.ecnu.adsmls.utils.Position;
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
    public Position getLinkPoint(Position adjacentPoint) {
        Position center = getCenterPoint();
        double rad = Geometry.radWithXAxis(center, adjacentPoint);
        System.out.println(rad);
        Position topRight = new Position(this.position.x + this.width, this.position.y);
        Position topLeft = this.position;
        Position bottomLeft = new Position(this.position.x, this.position.y + this.height);
        Position bottomRight = new Position(this.position.x + this.width, this.position.y + this.height);
        double rad1 = Geometry.radWithXAxis(center, topRight);
        double rad2 = Geometry.radWithXAxis(center, topLeft);
        double rad3 = Geometry.radWithXAxis(center, bottomLeft);
        double rad4 = Geometry.radWithXAxis(center, bottomRight);
        if(Geometry.between(rad, rad1, rad2) || Geometry.between(rad, rad3, rad4)) {
            // 交于顶边 或 底边
            double y = - center.y;
            if(adjacentPoint.y > center.y) {
                y += topRight.y;
            }
            else {
                y += bottomLeft.y;
            }
            double x = 0;
            if(rad != Math.PI / 2) {
                x = y / Math.tan(rad);
            }
            return new Position(center.x + x, center.y + y);
        }
        else {
            // 交于左边 或 右边
            double x = - center.x;
            if(adjacentPoint.x > center.x) {
                x += topLeft.x;
            }
            else {
                x += bottomRight.x;
            }
            double y = 0;
            if(rad != 0) {
                y = x * Math.tan(rad);
            }
            return new Position(center.x + x, center.y + y);
        }
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
