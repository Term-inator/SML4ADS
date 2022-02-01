package com.ecnu.adsmls.components.editor;

import com.ecnu.adsmls.utils.Position;
import javafx.scene.Node;

public class BranchPoint extends TreeArea {
    public BranchPoint(long id, Position position) {
        super(id, position);
    }

    @Override
    public Position getLinkPoint(Position adjacentPoint) {
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
        return null;
    }
}
