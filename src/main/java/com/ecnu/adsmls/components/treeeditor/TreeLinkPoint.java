package com.ecnu.adsmls.components.treeeditor;

import com.ecnu.adsmls.components.treeeditor.impl.TreeAreaRadius;
import com.ecnu.adsmls.utils.Position;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * 控制 TreeLink 的点
 */
public class TreeLinkPoint extends Area {
    private Position position;
    private TreeLink treeLink;
    private final double r = TreeAreaRadius.TreeLinkPoint.getR();

    public TreeLinkPoint(Position position, TreeLink treeLink) {
        super(position);
        this.position = position;
        this.position.x -= this.r;
        this.position.y -= this.r;
        this.treeLink = treeLink;

        this.createNode();
        this.inactive();
        this.enableDrag();
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position.relocate(position);
        this.position.x -= this.r;
        this.position.y -= this.r;
    }

    @Override
    public Position getCenterPoint() {
        return new Position(this.position.x + this.r, this.position.y + this.r);
    }

    @Override
    public void enableDrag() {
        super.enableDrag();
        this.graphicNode.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            this.treeLink.dragging = true;
        });

        this.graphicNode.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            this.treeLink.dragging = false;
        });

        this.graphicNode.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            // 更新对应的 link
            this.treeLink.updateNode();
        });
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
        circle.setCenterX(this.position.x + this.r);
        circle.setCenterY(this.position.y + this.r);
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
