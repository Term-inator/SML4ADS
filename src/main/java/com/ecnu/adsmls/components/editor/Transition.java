package com.ecnu.adsmls.components.editor;

import javafx.scene.Node;

public class Transition extends TreeLink {
    public Transition(long id) {
        super(id);
    }

    @Override
    public Node getNode() {
        this.updateNode();
        return graphicNode;
    }
}
