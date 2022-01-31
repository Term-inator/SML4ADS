package com.ecnu.adsmls.components.editor;

import javafx.scene.Group;
import javafx.scene.Node;

public abstract class Component {
    protected Group graphicNode;

    public Component() {
        this.graphicNode = new Group();
    }

    public abstract Node getNode();
}
