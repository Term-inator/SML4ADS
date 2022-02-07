package com.ecnu.adsmls.components.editor;

import com.ecnu.adsmls.utils.Position;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.text.Text;


public class TreeText extends Area {
    private TreeComponent component;

    private Text text = new Text();


    public TreeText(TreeComponent component) {
        super(component.getTextPosition());
        this.component = component;
        component.setTreeText(this);

        text.setX(this.position.x);
        text.setY(this.position.y + this.text.getBaselineOffset());
        this.addNode(text);
        this.enableDrag();
    }

    public Text getText() {
        return text;
    }

    public void setText(String text) {
        this.text.setText(text);
    }

    @Override
    public Position getCenterPoint() {
        // TODO
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
        return graphicNode;
    }
}
