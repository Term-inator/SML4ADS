package com.ecnu.adsmls.components.editor;

import com.ecnu.adsmls.utils.Position;
import com.ecnu.adsmls.utils.Vector2D;
import javafx.collections.ListChangeListener;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

import java.util.ArrayList;
import java.util.List;

public abstract class TreeLink extends TreeComponent {
    private TreeArea source;
    private TreeArea target;

    private List<TreeLinkPoint> linkPoints = new ArrayList<>();

    public TreeLink(long id) {
        super(id);
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

    @Override
    public void active() {

    }

    @Override
    public void inactive() {

    }

    public void updateNode() {
        this.graphicNode.getChildren().clear();

        this.modifyFirstPoint();
        this.modifyLastPoint();

        Path path = new Path();
        path.getElements().add(new MoveTo(this.linkPoints.get(0).position.x, this.linkPoints.get(0).position.y));
        int size = this.linkPoints.size();
        for (int i = 1; i < size; ++i) {
            Position p = this.linkPoints.get(i).position;
            path.getElements().add(new LineTo(p.x, p.y));
        }
        path.setStrokeWidth(2);
        path.setStroke(Color.ROYALBLUE);

        if(size >= 2) {
            Position p1 = this.linkPoints.get(size - 2).position;
            Position p2 = this.linkPoints.get(size - 1).position;
            Vector2D vector = new Vector2D(p2, p1);
            // 以末端为原点
            double rad = vector.radWithXAxis();

            Node arrow = new Arrow(p2, rad, 12).getNode();

            this.graphicNode.getChildren().addAll(path, arrow);
        }
        else {
            this.graphicNode.getChildren().addAll(path);
        }
    }
}
