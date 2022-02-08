package com.ecnu.adsmls.components.editor;

import com.ecnu.adsmls.utils.Position;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * 控制 TreeLink 的点
 */
public class TreeLinkPoint extends Area {
    private Position position;
    private TreeLink treeLink;
    private final double r = 5;

    public TreeLinkPoint(Position position, TreeLink treeLink) {
        super(position);
        this.position = position;
        this.treeLink = treeLink;

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
        this.graphicNode.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            // 更新对应的 link
            this.treeLink.updateNode();
        });
    }

    @Override
    public void active() {

    }

    @Override
    public void inactive() {

    }

    @Override
    public void updateNode() {
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
    }
}
