package com.ecnu.adsmls.components.editor;


import javafx.scene.Group;
import javafx.scene.Node;


public abstract class TreeComponent {
    protected Group graphicNode;

    public TreeComponent() {
        graphicNode = new Group();
    }

    public abstract Node getNode();
}
