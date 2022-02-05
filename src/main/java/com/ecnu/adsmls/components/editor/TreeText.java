package com.ecnu.adsmls.components.editor;

import com.ecnu.adsmls.utils.Position;
import javafx.scene.Node;
import javafx.scene.text.Text;


public class TreeText extends Area {
    private TreeComponent component;

    private Text text = new Text();


    public TreeText(TreeComponent component) {
        super(component.getTextPosition());
        this.component = component;
        component.setTreeText(this);
    }

    public String getText() {
        return text.getText();
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
        text.setX(this.position.x);
        text.setY(this.position.y);

        this.addNode(text);
        this.enableDrag();

        return graphicNode;
    }
}
