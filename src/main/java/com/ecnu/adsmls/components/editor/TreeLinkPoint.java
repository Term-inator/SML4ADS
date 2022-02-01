package com.ecnu.adsmls.components.editor;

import com.ecnu.adsmls.utils.Position;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class TreeLinkPoint extends Component implements Draggable {
    protected Position position;
    private TreeLink treeLink;
    private double r = 5;

    /**
     * @param position 中心坐标
     * @param treeLink 所在的 link
     */
    public TreeLinkPoint(Position position, TreeLink treeLink) {
        this.position = position;
        this.treeLink = treeLink;
    }

    @Override
    public void enableDrag(Node node) {
        final Position pos = new Position();

        // 提示用户该结点可点击
        node.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> node.setCursor(Cursor.HAND));
        node.addEventHandler(MouseEvent.MOUSE_EXITED, e -> node.setCursor(Cursor.DEFAULT));

        // 提示用户该结点可拖拽
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            node.setCursor(Cursor.MOVE);

            // 当按压事件发生时，缓存事件发生的位置坐标
            pos.x = e.getX();
            pos.y = e.getY();
        });
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> node.setCursor(Cursor.DEFAULT));

        // 实现拖拽功能
        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            double disX = e.getX() - pos.x;
            double disY = e.getY() - pos.y;

            double x = this.position.x + disX;
            double y = this.position.y + disY;

            // 计算出 x、y 后将结点重定位到指定坐标点 (x, y)
            node.relocate(x - this.r, y - this.r);
            this.position.relocate(x, y);
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
    public Node getNode() {
        Circle circle = new Circle();
        circle.setCenterX(this.position.x);
        circle.setCenterY(this.position.y);
        circle.setRadius(this.r);
        circle.setFill(Color.WHITE);
        circle.setStrokeWidth(2);
        circle.setStroke(Color.ROYALBLUE);

        graphicNode.getChildren().addAll(circle);
        this.enableDrag(graphicNode);

        return graphicNode;
    }
}
