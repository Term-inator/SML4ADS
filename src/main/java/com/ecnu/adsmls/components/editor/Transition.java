package com.ecnu.adsmls.components.editor;

import com.ecnu.adsmls.utils.Position;
import javafx.scene.Node;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

import java.util.ArrayList;

public class Transition extends TreeComponent {
    private Area source;
    private Area target;

    private ArrayList<Position> positions = new ArrayList<>();

    public Transition() {
        super();
    }

    public void setSource(Area source) {
        this.source = source;
    }

    public void setTarget(Area target) {
        this.target = target;
    }

    @Override
    public Node getNode() {
//        Position sp = this.source.getLinkPoint(positions.get(0));
//        Position tp = this.target.getLinkPoint(positions.get(positions.size() - 1));

        Path path = new Path();
        path.getElements().add(new MoveTo(0.0f, 50.0f));
        path.getElements().add(new LineTo(100.0f, 100.0f));
        path.getElements().add(new LineTo(100.0f, 200.0f));

        graphicNode.getChildren().addAll(path);

        return graphicNode;
    }
}
