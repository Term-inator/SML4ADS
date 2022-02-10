package com.ecnu.adsmls.components.editor;

import com.ecnu.adsmls.utils.Position;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;


public class TreeText extends Area {
    private TreeComponent component;

    public TreeText(TreeComponent component) {
        super(component.getTextPosition());
        this.component = component;
        component.setTreeText(this);

        this.createNode();
        this.inactive();
        this.enableDrag();
    }

    public Text getText() {
        return (Text) this.shape;
    }

    public void setText(String text) {
        ((Text) this.shape).setText(text);
    }

    @Override
    public Position getCenterPoint() {
        // TODO
        return null;
    }

    @Override
    public void active() {
        this.shape.setFill(Color.ORANGE);
    }

    @Override
    public void inactive() {
        this.shape.setFill(Color.BLACK);
    }

    @Override
    public void createNode() {
        Text text = new Text();
        text.setX(this.position.x);
        text.setY(this.position.y + text.getBaselineOffset());

        this.shape = text;
    }

    @Override
    public void updateNode() {
        ((Text) this.shape).setX(this.position.x);
        ((Text) this.shape).setY(this.position.y + this.shape.getBaselineOffset());
        this.addNode(this.shape);
    }
}
