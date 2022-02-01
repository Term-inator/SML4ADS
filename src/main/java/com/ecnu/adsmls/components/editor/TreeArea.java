package com.ecnu.adsmls.components.editor;

import com.ecnu.adsmls.utils.Position;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * 封闭图形
 */
public abstract class TreeArea extends TreeComponent implements Draggable, Linkable {
    protected Position position;

    private List<TreeLink> inTransitions = new ArrayList<>();
    private List<TreeLink> outTransitions = new ArrayList<>();

    public TreeArea(long id, Position position) {
        super(id);
        this.position = position;
    }

    public void addInTransition(TreeLink treeLink) {
        this.inTransitions.add(treeLink);
    }

    public void addOutTransition(TreeLink treeLink) {
        this.outTransitions.add(treeLink);
    }

    @Override
    public abstract Position getLinkPoint(Position adjacentPoint);

    public Position getCenterPoint() {
        return null;
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

            // Transition 跟随拖动
            for (TreeLink l : this.inTransitions) {
                l.getLinkPoints().get(l.getLinkPoints().size() - 1).position.relocate(this.getCenterPoint());
                l.updateNode();
            }
            for (TreeLink l : this.outTransitions) {
                l.getLinkPoints().get(0).position.relocate(this.getCenterPoint());
                l.updateNode();
            }
        });
    }
}
