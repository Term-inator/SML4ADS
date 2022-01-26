package com.ecnu.adsmls.components.editor;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public abstract class Area extends TreeComponent implements Draggable {
    public Area(Position position) {
        super(position);
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
            node.relocate(x, y);
            this.position.relocate(x, y);
        });
    }
}
