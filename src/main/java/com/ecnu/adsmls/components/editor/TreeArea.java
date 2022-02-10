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

    public Position getPosition() {
        return position;
    }

    public void addInTransition(TreeLink treeLink) {
        this.inTransitions.add(treeLink);
    }

    public void addOutTransition(TreeLink treeLink) {
        this.outTransitions.add(treeLink);
    }

    public List<TreeLink> getInTransitions() {
        return inTransitions;
    }

    public List<TreeLink> getOutTransitions() {
        return outTransitions;
    }

    @Override
    public abstract Position getLinkPoint(Position adjacentPoint);

    public abstract Position getCenterPoint();

    @Override
    public Position getTextPosition() {
        double width = this.graphicNode.getBoundsInLocal().getWidth();
        return new Position(this.position.x + width, this.position.y);
    }

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

            // Transition 跟随拖动
            for (TreeLink l : this.inTransitions) {
                l.getLinkPoints().get(l.getLinkPoints().size() - 1).setPosition(this.getCenterPoint());
                l.updateNode();
            }
            for (TreeLink l : this.outTransitions) {
                l.getLinkPoints().get(0).setPosition(this.getCenterPoint());
                l.updateNode();
            }
        });
    }

    @Override
    public List<Node> remove() {
        List<Node> res = new ArrayList<>(super.remove());
        for(int i = this.inTransitions.size() - 1; i >= 0; --i) {
            res.addAll(this.inTransitions.get(i).remove());
        }
        for(int i = this.outTransitions.size() - 1; i >= 0; --i) {
            res.addAll(this.outTransitions.get(i).remove());
        }
        return res;
    }
}
