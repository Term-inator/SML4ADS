package com.ecnu.adsmls.components.modal;

import com.ecnu.adsmls.utils.FileSystem;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.File;
import java.util.Objects;

// TODO abstract NewFileModal
public class NewTreeModal extends Modal {
    private File directory;

    private String filename;

    // 文件是否新建成功
    private boolean succeed = true;

    public NewTreeModal() {
        super();
    }

    public String getFilename() {
        return filename;
    }

    public void setDirectory(File directory) {
        if(directory.isFile()) {
            this.directory = directory.getParentFile();
        }
        else {
            this.directory = directory;
        }
    }

    public File getDirectory() {
        return directory;
    }

    public boolean isSucceed() {
        return succeed;
    }

    @Override
    protected void createWindow() {
        super.createWindow();

        Label lbFilename = new Label("filename");
        TextField tfFilename = new TextField();

        staticPage.add(0, new Node[] {lbFilename, tfFilename});
    }

    @Override
    protected void check() {
        this.checkFilename();
    }

    @Override
    protected void update() {
        this.updateFileName();
    }

    @Override
    protected void then() {
        this.createTree();
    }

    private void updateFileName() {
        TextField tfFilename = (TextField) this.staticPage.get(0)[1];
        this.filename = tfFilename.getText();
    }

    private void checkFilename() {
        // TODO 文件名检查
        if(Objects.equals(this.filename, "")) {
            this.valid = false;
        }
    }

    private void createTree() {
        this.succeed = FileSystem.createFile(this.directory, this.filename + FileSystem.Suffix.TREE.value);
    }
}
