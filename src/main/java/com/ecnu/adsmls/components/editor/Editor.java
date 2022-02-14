package com.ecnu.adsmls.components.editor;

import javafx.scene.Node;

public abstract class Editor {
    protected String directory;
    protected String filename;

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public abstract void save();

    public abstract void load();

    protected abstract void createNode();

    public abstract Node getNode();
}
