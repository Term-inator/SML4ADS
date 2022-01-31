package com.ecnu.adsmls.components.editor;

import com.ecnu.adsmls.components.Arrow;
import com.ecnu.adsmls.utils.Geometry;
import com.ecnu.adsmls.utils.Position;
import com.ecnu.adsmls.utils.Vector2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

import java.util.ArrayList;
import java.util.List;

public class Transition extends TreeComponent {
    private Area source;
    private Area target;

    private List<Position> positions = new ArrayList<>();

    public Transition(long id) {
        super(id);
    }

    public Area getSource() {
        return source;
    }

    public void setSource(Area source) {
        this.source = source;
    }

    public Area getTarget() {
        return target;
    }

    public void setTarget(Area target) {
        this.target = target;
//        this.modifyLastPoint();
    }

    public List<Position> getPositions() {
        return positions;
    }

    private void modifyFirstPoint() {
        if (this.positions.size() == 2) {
            this.positions.set(0, this.source.getLinkPoint(this.positions.get(1)));
        }
    }

    private void modifyLastPoint() {
        if(this.target != null) {
            int size = this.positions.size();
            if (size >= 2) {
                System.out.println("modifying");
                Position p = this.target.getLinkPoint(this.positions.get(size - 2));
                System.out.println(p.x);
                this.positions.set(size - 1, this.target.getLinkPoint(this.positions.get(size - 2)));
            }
        }
    }

    @Override
    public Node getNode() {
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

        if(this.target != null) {
            Position p1 = this.positions.get(size - 2);
            Position p2 = this.positions.get(size - 1);
            Vector2D vector = new Vector2D(p2, p1);
            // 以末端为原点
            double rad = vector.radWithXAxis();
            Node arrow = new Arrow(p2, rad, 12).getNode();
            graphicNode.getChildren().addAll(path, arrow);
        }
        else {
            graphicNode.getChildren().addAll(path);
        }

        return graphicNode;
    }
}
