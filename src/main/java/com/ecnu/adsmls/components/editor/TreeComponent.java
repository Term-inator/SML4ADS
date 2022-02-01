package com.ecnu.adsmls.components.editor;


import javafx.scene.Group;
import javafx.scene.Node;


public abstract class TreeComponent extends Component {
    private final long id;

    public TreeComponent(long id) {
        super();
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
