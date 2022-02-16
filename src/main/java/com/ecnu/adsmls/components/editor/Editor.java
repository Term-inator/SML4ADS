package com.ecnu.adsmls.components.editor;

import javafx.scene.Node;

/**
 * 文件编辑器
 */
// TODO 是否已经保存
public abstract class Editor {
    // 文件所在文件夹路径
    protected String directory;
    // 文件名
    protected String filename;

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * 保存文件
     */
    public abstract void save();

    /**
     * 加载文件
     */
    public abstract void load();

    protected abstract void createNode();

    public abstract Node getNode();
}
