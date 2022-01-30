package com.ecnu.adsmls.components.editor;


import javafx.scene.Group;
import javafx.scene.Node;


public abstract class TreeComponent {
    private long id;
    protected Group graphicNode;

    public TreeComponent(long id) {
        this.id = id;
        graphicNode = new Group();
    }

    public long getId() {
        return id;
    }

    public abstract Node getNode();
}
