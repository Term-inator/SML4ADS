package com.ecnu.adsmls.components.editor;

import com.ecnu.adsmls.utils.Position;
import javafx.scene.Node;

public class BranchPoint extends Area {
    public BranchPoint(long id, Position position) {
        super(id, position);
    }

    @Override
    public Node getNode() {
        return null;
    }
}
