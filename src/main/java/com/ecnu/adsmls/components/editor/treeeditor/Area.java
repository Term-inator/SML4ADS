package com.ecnu.adsmls.components.editor.treeeditor;

import com.ecnu.adsmls.utils.Position;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;

public abstract class Area extends Component implements Draggable {
    protected Position position;

    public Area(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
        this.updatePosition();
    }

    public abstract void updatePosition();

    public abstract Position getCenterPoint();

    @Override
    public void enableDrag() {
        final Position pos = new Position();

        // 提示用户该结点可拖拽
        this.graphicNode.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            this.graphicNode.setCursor(Cursor.MOVE);

            // 当按压事件发生时，缓存事件发生的位置坐标
            pos.x = e.getX();
            pos.y = e.getY();
        });
        this.graphicNode.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> this.graphicNode.setCursor(Cursor.DEFAULT));

        // 实现拖拽功能
        this.graphicNode.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            double disX = e.getX() - pos.x;
            double disY = e.getY() - pos.y;

            double x = this.position.x + disX;
            double y = this.position.y + disY;

            // 计算出 x、y 后将结点重定位到指定坐标点 (x, y)
            this.graphicNode.relocate(x, y);
            this.position.relocate(x, y);
        });
    }
}
