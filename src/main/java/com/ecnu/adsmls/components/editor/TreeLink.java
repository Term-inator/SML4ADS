package com.ecnu.adsmls.components.editor;

import com.ecnu.adsmls.utils.Position;
import com.ecnu.adsmls.utils.Vector2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

import java.util.ArrayList;
import java.util.List;

public abstract class TreeLink extends TreeComponent {
    private TreeArea source;
    private TreeArea target;

    private List<Position> positions = new ArrayList<>();

    public TreeLink(long id) {
        super(id);
    }

    public TreeArea getSource() {
        return source;
    }

    public void setSource(TreeArea source) {
        this.source = source;
        ((Behavior) this.source).addOutTransition(this);
    }

    public TreeArea getTarget() {
        return target;
    }

    public void setTarget(TreeArea target) {
        this.target = target;
        ((Behavior) this.target).addInTransition(this);
    }

    public List<Position> getPositions() {
        return positions;
    }

    /**
     * 修正第一个点的坐标为 source 的连接点
     */
    private void modifyFirstPoint() {
        if (this.positions.size() >= 2) {
            this.positions.get(0).relocate(this.source.getLinkPoint(this.positions.get(1)));
        }
    }

    /**
     * 修正最后一个点的坐标为 source 的连接点
     */
    private void modifyLastPoint() {
        if(this.target != null) {
            int size = this.positions.size();
            if (size >= 2) {
                Position p = this.target.getLinkPoint(this.positions.get(size - 2));
                this.positions.get(size - 1).relocate(this.target.getLinkPoint(this.positions.get(size - 2)));
            }
        }
    }

    public void updateNode() {
        this.modifyFirstPoint();
        this.modifyLastPoint();

        Path path = new Path();
        path.getElements().add(new MoveTo(this.positions.get(0).x, this.positions.get(0).y));
        int size = this.positions.size();
        for (int i = 1; i < size; ++i) {
            Position p = this.positions.get(i);
            path.getElements().add(new LineTo(p.x, p.y));
        }
        path.setStrokeWidth(2);
        path.setStroke(Color.ROYALBLUE);

        if(size >= 2) {
            Position p1 = this.positions.get(size - 2);
            Position p2 = this.positions.get(size - 1);
            Vector2D vector = new Vector2D(p2, p1);
            // 以末端为原点
            double rad = vector.radWithXAxis();

            Node arrow = new Arrow(p2, rad, 12).getNode();

            graphicNode.getChildren().clear();
            graphicNode.getChildren().addAll(path, arrow);
        }
        else {
            graphicNode.getChildren().addAll(path);
        }
    }
}
