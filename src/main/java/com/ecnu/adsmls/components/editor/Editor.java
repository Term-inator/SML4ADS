package com.ecnu.adsmls.components.editor;

import javafx.scene.Node;

/**
 * 文件编辑器
 */
// TODO 是否已经保存
public abstract class Editor {
    // Project 路径
    protected String projectPath;
    // 文件在 Project 下的相对路径
    protected String relativePath;

    public Editor(String projectPath, String relativePath) {
        this.projectPath = projectPath;
        this.relativePath = relativePath;
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
