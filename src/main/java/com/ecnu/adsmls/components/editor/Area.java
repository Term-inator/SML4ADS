package com.ecnu.adsmls.components.editor;

import com.ecnu.adsmls.utils.Position;
import javafx.scene.Node;

public abstract class Area extends Component implements Draggable {
    protected Position position;

    public Area(Position position) {
        this.position = position;
    }

    @Override
    public void enableDrag(Node node) {

    }
}
