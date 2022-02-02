package com.ecnu.adsmls.components.editor;

import javafx.scene.Node;

public class Transition extends TreeLink {
    public Transition(long id) {
        super(id);
    }

    @Override
    public void active() {
        super.active();
    }

    @Override
    public void inactive() {
        super.inactive();
    }

    @Override
    public Node getNode() {
        return graphicNode;
    }
}
