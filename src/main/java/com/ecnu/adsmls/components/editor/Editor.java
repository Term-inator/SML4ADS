package com.ecnu.adsmls.components.editor;

import com.ecnu.adsmls.utils.FileSystem;
import javafx.scene.Node;

import java.io.File;

/**
 * 文件编辑器
 */
public abstract class Editor {
    // Project 路径
    protected String projectPath;
    // 文件在 Project 下的相对路径
    protected String relativePath;
    // 对应打开的文件
    protected File file;

    public Editor(String projectPath, File file) {
        this.projectPath = projectPath;
        this.file = file;
        this.relativePath = FileSystem.getRelativePath(this.projectPath, file.getAbsolutePath());
    }

    public File getFile() {
        return file;
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
