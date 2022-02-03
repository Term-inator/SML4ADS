package com.ecnu.adsmls.components.editor;

import com.ecnu.adsmls.utils.Position;
import javafx.scene.Node;
import javafx.scene.text.Text;


public class TreeText extends Area {
    private TreeComponent component;

    private String text;

    public TreeText(Position position) {
        super(position);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public Position getCenterPoint() {
        return null;
    }

    @Override
    public void active() {

    }

    @Override
    public void inactive() {

    }

    @Override
    public Node getNode() {
        Text text = new Text();
        text.setX(this.position.x);
        text.setY(this.position.y);
        text.setText(this.text);

        this.addNode(text);
        this.enableDrag();

        return graphicNode;
    }
}
