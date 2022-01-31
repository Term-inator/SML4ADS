package com.ecnu.adsmls.components.editor;

import com.ecnu.adsmls.utils.Position;
import com.ecnu.adsmls.utils.Vector2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

public class Behavior extends Area {
    private final double r = 16;

    private List<Transition> inTransitions = new ArrayList<>();
    private List<Transition> outTransitions = new ArrayList<>();

    public Behavior(long id, Position position) {
        super(id, position);
    }

    public void addInTransition(Transition transition) {
        this.inTransitions.add(transition);
    }

    public void addOutTransition(Transition transition) {
        this.outTransitions.add(transition);
    }

    @Override
    public Position getLinkPoint(Position adjacentPoint) {
        Position center = getCenterPoint();
        Vector2D vector = new Vector2D(center, adjacentPoint);
        double rad = vector.radWithXAxis();
        double x = this.r * Math.cos(rad);
        double y = - this.r * Math.sin(rad);
        return new Position(center.x + x, center.y + y);
    }

    @Override
    public Position getCenterPoint() {
        double x = this.position.x + this.r;
        double y = this.position.y + this.r;
        return new Position(x, y);
    }

    @Override
    public void enableDrag(Node node) {
        super.enableDrag(node);
        // Transition 跟随拖动
        this.graphicNode.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            for (Transition t : this.inTransitions) {
                t.getPositions().get(t.getPositions().size() - 1).relocate(this.getCenterPoint());
                t.updateNode();
            }
            for (Transition t : this.outTransitions) {
                t.getPositions().get(0).relocate(this.getCenterPoint());
                t.updateNode();
            }
        });
    }

    @Override
    public Node getNode() {
        Circle circle = new Circle();
        this.position.x -= this.r;
        this.position.y -= this.r;
        circle.setCenterX(this.position.x + this.r);
        circle.setCenterY(this.position.y + this.r);
        circle.setRadius(this.r);
        circle.setFill(Color.WHITE);
        circle.setStrokeWidth(2);
        circle.setStroke(Color.ROYALBLUE);

        graphicNode.getChildren().addAll(circle);
        this.enableDrag(graphicNode);

        return graphicNode;
    }
}
