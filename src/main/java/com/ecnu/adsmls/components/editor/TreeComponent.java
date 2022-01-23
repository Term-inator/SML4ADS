package com.ecnu.adsmls.components.editor;


import javafx.scene.Group;
import javafx.scene.Node;


public abstract class TreeComponent {
    protected Group graphicNode;
    protected Position position;

    public TreeComponent(Position position) {
        graphicNode = new Group();
        this.position = position;
    }

    public abstract Node getNode();
}
