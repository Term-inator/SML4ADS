package com.ecnu.adsmls.components.editor;

import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class TreeComponent {
    protected Group graphicNode;
    protected Position position;

    public TreeComponent(Position position) {
        graphicNode = new Group();
        this.position = position;
    }

    public Node getNode() {
        return null;
    }
}
