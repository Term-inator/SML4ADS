package com.ecnu.adsmls.components.editor;

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
