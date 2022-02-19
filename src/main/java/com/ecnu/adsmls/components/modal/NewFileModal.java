package com.ecnu.adsmls.components.modal;

import com.ecnu.adsmls.utils.FileSystem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.File;
import java.util.Objects;

public class NewFileModal extends Modal {
    private File directory;
    private String filename;

    private String labelName;
    private String suffix;

    private TextField tfFilename;

    // 文件是否新建成功
    private boolean succeed = true;

    public NewFileModal(String labelName, String suffix) {
        super();
        this.labelName = labelName;
        this.suffix = suffix;
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

        Label lbFilename = new Label(this.labelName);
        this.tfFilename = new TextField();

        this.slot.addRow(0, lbFilename, this.tfFilename);
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
        this.filename = this.tfFilename.getText();
    }

    private void checkFilename() {
        // 非空
        if(Objects.equals(this.filename, "")) {
            this.valid = false;
        }
    }

    private void createTree() {
        if(Objects.equals(this.suffix, "")) {
            this.succeed = FileSystem.createDir(FileSystem.concatAbsolutePath(this.directory.getAbsolutePath(), this.filename));
        }
        else {
            this.succeed = FileSystem.createFile(this.directory, this.filename + this.suffix);
        }
    }
}
