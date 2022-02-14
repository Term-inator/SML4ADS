package com.ecnu.adsmls.components.editor.treeeditor;

import com.ecnu.adsmls.utils.Position;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;


public class Arrow extends Component {
    private TreeLink treeLink;
    private Position endPoint;
    private double rad;
    private double arrowLen;

    private final double angle = Math.PI / 3;

    public Arrow(TreeLink treeLink, Position endPoint, double rad, double arrowLen) {
        super();
        this.treeLink = treeLink;
        this.endPoint = endPoint;
        this.rad = rad;
        this.arrowLen = arrowLen;

        this.createNode();
        this.inactive();
    }

    public void relocate(Position endPoint, double rad) {
        this.endPoint = endPoint;
        this.rad = rad;
        this.createNode();
    }

    @Override
    public void createNode() {
        Path path;
        if(this.shape == null) {
            path = new Path();
        }
        else {
            path = (Path) this.shape;
            path.getElements().clear();
        }

        this.shape = path;
    }

    @Override
    public void active() {
        super.active();
    }

    @Override
    public void inactive() {
        if(this.treeLink.isSelected()) {
            return;
        }
        super.inactive();
    }

    @Override
    public void updateNode() {
        Path path = (Path) this.shape;

        double rad1 = this.rad - Math.PI / 6;
        double rad2 = this.rad + Math.PI / 6;
        double x1 = this.arrowLen * Math.cos(rad1) + this.endPoint.x;
        double y1 = - this.arrowLen * Math.sin(rad1) + this.endPoint.y;
        double x2 = this.arrowLen * Math.cos(rad2) + this.endPoint.x;
        double y2 = - this.arrowLen * Math.sin(rad2) + this.endPoint.y;

        path.setStrokeWidth(2);
        path.getElements().add(new MoveTo(x1, y1));
        path.getElements().add(new LineTo(this.endPoint.x, this.endPoint.y));
        path.getElements().add(new LineTo(x2, y2));
        this.addNode(this.shape);
    }
}
