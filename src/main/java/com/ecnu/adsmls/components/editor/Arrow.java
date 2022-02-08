package com.ecnu.adsmls.components.editor;

import com.ecnu.adsmls.utils.Position;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;


public class Arrow extends Component {
    private Position endPoint;
    private double rad;
    private double arrowLen;

    private final double angle = Math.PI / 3;

    private Path path = new Path();

    public Arrow(Position endPoint, double rad, double arrowLen) {
        super();
        this.endPoint = endPoint;
        this.rad = rad;
        this.arrowLen = arrowLen;
    }

    public void relocate(Position endPoint, double rad) {
        this.endPoint = endPoint;
        this.rad = rad;
        this.generatePath();
    }

    private void generatePath() {
        double rad1 = this.rad - Math.PI / 6;
        double rad2 = this.rad + Math.PI / 6;
        double x1 = this.arrowLen * Math.cos(rad1) + this.endPoint.x;
        double y1 = - this.arrowLen * Math.sin(rad1) + this.endPoint.y;
        double x2 = this.arrowLen * Math.cos(rad2) + this.endPoint.x;
        double y2 = - this.arrowLen * Math.sin(rad2) + this.endPoint.y;

        this.path.getElements().clear();

        this.path.getElements().add(new MoveTo(x1, y1));
        this.path.getElements().add(new LineTo(this.endPoint.x, this.endPoint.y));
        this.path.getElements().add(new LineTo(x2, y2));
    }

    @Override
    public void active() {

    }

    @Override
    public void inactive() {

    }

    @Override
    public void updateNode() {
        this.generatePath();

        this.path.setStrokeWidth(2);
        this.path.setStroke(Color.ROYALBLUE);

        if(!this.graphicNode.getChildren().contains(this.path)) {
            this.graphicNode.getChildren().addAll(this.path);
        }
    }
}
