package com.ecnu.adsmls.components.editor;

import com.ecnu.adsmls.utils.Geometry;
import com.ecnu.adsmls.utils.Position;
import com.ecnu.adsmls.utils.Vector2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineJoin;

import java.util.ArrayList;
import java.util.List;

/**
 * 树的边
 */
public abstract class TreeLink extends TreeComponent {
    private TreeArea source;
    private TreeArea target;

    private List<TreeLinkPoint> linkPoints = new ArrayList<>();
    private Path path = new Path();
    private Arrow arrow;
    private Group linkPointLayer = new Group();

    // 默认支持自环
    protected boolean loop = true;
    private boolean finish = false;

    public TreeLink(long id) {
        super(id);
    }

    public TreeArea getSource() {
        return source;
    }

    public void setSource(TreeArea source) {
        this.source = source;
        this.linkPoints.add(new TreeLinkPoint(this.source.getCenterPoint(), this));
    }

    public TreeArea getTarget() {
        return target;
    }

    public boolean setTarget(TreeArea target) {
        if(!loop && this.source == target) {
            return false;
        }
        this.target = target;
        this.linkPoints.add(new TreeLinkPoint(this.target.getCenterPoint(), this));
        return true;
    }

    public List<TreeLinkPoint> getLinkPoints() {
        return linkPoints;
    }

    public void finish() {
        if(this.finish) {
            return;
        }
        this.finish = true;
        // 整个 Transition 完成了才更新到 source 和 target ，便于中途取消
        this.source.addOutTransition(this);
        this.target.addInTransition(this);
        this.generatePoints();
    }

    public boolean isFinish() {
        return finish;
    }

    /**
     * 禁止自环
     */
    public void disableLoop() {
        this.loop = false;
    }

    /**
     * 变成虚线
     */
    public void dashed() {
        this.path.getStrokeDashArray().addAll(10d);
    }

    @Override
    public Position getTextPosition() {
        int size = this.linkPoints.size();
        assert (size >= 2);
        if(size % 2 == 0) {
            Position p1 = this.linkPoints.get(size / 2 - 1).getCenterPoint();
            Position p2 = this.linkPoints.get(size / 2).getCenterPoint();
            return Geometry.centerOf(p1, p2);
        }
        else {
            Position p = this.linkPoints.get((size - 1) / 2).getCenterPoint();
            return new Position(p.x, p.y);
        }
    }

    /**
     * 修正第一个点的坐标为 source 的连接点
     */
    private void modifyFirstPoint() {
        if (this.linkPoints.size() >= 2) {
            this.linkPoints.get(0).setPosition(this.source.getLinkPoint(this.linkPoints.get(1).getCenterPoint()));
        }
    }

    /**
     * 修正最后一个点的坐标为 source 的连接点
     */
    private void modifyLastPoint() {
        if(this.target != null) {
            int size = this.linkPoints.size();
            if (size >= 2) {
                Position p = this.target.getLinkPoint(this.linkPoints.get(size - 2).getCenterPoint());
                this.linkPoints.get(size - 1).setPosition(p);
            }
        }
    }

    private void generatePoints() {
        for (int i = 1; i < this.linkPoints.size() - 1; ++i) {
            this.linkPoints.get(i).updateNode();
            this.linkPointLayer.getChildren().add(this.linkPoints.get(i).getNode());
        }
    }

    private void showPoints() {
        this.addNode(this.linkPointLayer);
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

    @Override
    public void updateNode() {
        this.modifyFirstPoint();
        this.modifyLastPoint();

        this.path.getElements().clear();
        this.path.getElements().add(new MoveTo(this.linkPoints.get(0).getCenterPoint().x, this.linkPoints.get(0).getCenterPoint().y));
        int size = this.linkPoints.size();
        for (int i = 1; i < size; ++i) {
            Position p = this.linkPoints.get(i).getCenterPoint();
            this.path.getElements().add(new LineTo(p.x, p.y));
        }
        this.path.setStrokeWidth(2);
        this.path.setStroke(Color.ROYALBLUE);
        this.path.setStrokeLineJoin(StrokeLineJoin.ROUND);

        if(size >= 2) {
            Position p1 = this.linkPoints.get(size - 2).getCenterPoint();
            Position p2 = this.linkPoints.get(size - 1).getCenterPoint();
            Vector2D vector = new Vector2D(p2, p1);
            // 以末端为原点
            double rad = vector.radWithXAxis();

            if(this.arrow == null) {
                this.arrow = new Arrow(p2, rad, 12);
            }
            else {
                this.arrow.relocate(p2, rad);
            }

            this.arrow.updateNode();
            this.addNodes(path, this.arrow.getNode());
        }
        else {
            this.addNode(path);
        }

    }

    public void rollback() {
        this.source = null;
        this.target = null;

        linkPoints.clear();
        linkPointLayer.getChildren().clear();
    }

    @Override
    public List<Node> remove() {
        List<Node> res = new ArrayList<>();
        res.addAll(super.remove());
        this.source.getOutTransitions().remove(this);
        this.target.getInTransitions().remove(this);
        return res;
    }
}
