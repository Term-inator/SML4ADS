package com.ecnu.adsmls.components.editor;

import com.ecnu.adsmls.utils.Position;
import com.ecnu.adsmls.utils.Vector2D;
import javafx.collections.ListChangeListener;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class TreeLink extends TreeComponent {
    private TreeArea source;
    private TreeArea target;

    private List<TreeLinkPoint> linkPoints = new ArrayList<>();
    private Path path = new Path();
    private Arrow arrow;
    private Group linkPointLayer = new Group();

    private boolean finish = false;

    public TreeLink(long id) {
        super(id);

        // TODO
        // 提示用户该结点可点击
        this.graphicNode.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> this.graphicNode.setCursor(Cursor.HAND));
        this.graphicNode.addEventHandler(MouseEvent.MOUSE_EXITED, e -> this.graphicNode.setCursor(Cursor.DEFAULT));
    }

    public TreeArea getSource() {
        return source;
    }

    public void setSource(TreeArea source) {
        this.source = source;
        this.source.addOutTransition(this);
    }

    public TreeArea getTarget() {
        return target;
    }

    public void setTarget(TreeArea target) {
        this.target = target;
        this.target.addInTransition(this);
    }

    public List<TreeLinkPoint> getLinkPoints() {
        return linkPoints;
    }

    public void finish() {
        if(this.finish) {
            return;
        }
        this.finish = true;
        this.generatePoints();
    }

    public boolean getFinish() {
        return finish;
    }

    /**
     * 修正第一个点的坐标为 source 的连接点
     */
    private void modifyFirstPoint() {
        if (this.linkPoints.size() >= 2) {
            this.linkPoints.get(0).position.relocate(this.source.getLinkPoint(this.linkPoints.get(1).position));
        }
    }

    /**
     * 修正最后一个点的坐标为 source 的连接点
     */
    private void modifyLastPoint() {
        if(this.target != null) {
            int size = this.linkPoints.size();
            if (size >= 2) {
                Position p = this.target.getLinkPoint(this.linkPoints.get(size - 2).position);
                this.linkPoints.get(size - 1).position.relocate(this.target.getLinkPoint(this.linkPoints.get(size - 2).position));
            }
        }
    }

    private void generatePoints() {
        for (int i = 1; i < this.linkPoints.size() - 1; ++i) {
            this.linkPointLayer.getChildren().add(this.linkPoints.get(i).getNode());
            System.out.println(this.linkPoints.get(i).position.toString());
        }
        System.out.println(this.linkPointLayer.getChildren());
    }

    private void showPoints() {
        if(!this.graphicNode.getChildren().contains(this.linkPointLayer)) {
            this.graphicNode.getChildren().add(this.linkPointLayer);
        }
    }

    private void hidePoints() {
        this.graphicNode.getChildren().remove(this.linkPointLayer);
    }

    @Override
    public void active() {
        this.showPoints();
    }

    @Override
    public void inactive() {
        this.hidePoints();
    }

    public void updateNode() {
        this.modifyFirstPoint();
        this.modifyLastPoint();

        this.path.getElements().clear();
        this.path.getElements().add(new MoveTo(this.linkPoints.get(0).position.x, this.linkPoints.get(0).position.y));
        int size = this.linkPoints.size();
        for (int i = 1; i < size; ++i) {
            Position p = this.linkPoints.get(i).position;
            this.path.getElements().add(new LineTo(p.x, p.y));
        }
        this.path.setStrokeWidth(2);
        this.path.setStroke(Color.ROYALBLUE);

        if(size >= 2) {
            Position p1 = this.linkPoints.get(size - 2).position;
            Position p2 = this.linkPoints.get(size - 1).position;
            Vector2D vector = new Vector2D(p2, p1);
            // 以末端为原点
            double rad = vector.radWithXAxis();

            if(this.arrow == null) {
                this.arrow = new Arrow(p2, rad, 12);
            }
            else {
                this.arrow.relocate(p2, rad);
            }

            // TODO 封装
            if(!this.graphicNode.getChildren().contains(path)) {
                this.graphicNode.getChildren().addAll(path);
            }

            if(!this.graphicNode.getChildren().contains(this.arrow.getNode())) {
                this.graphicNode.getChildren().addAll(this.arrow.getNode());
            }
        }
        else {
            if(!this.graphicNode.getChildren().contains(path)) {
                this.graphicNode.getChildren().addAll(path);
            }
        }
    }
}
