package com.ecnu.adsmls.components.modal.impl;

import com.ecnu.adsmls.components.choosefilebutton.ChooseDirectoryButton;
import com.ecnu.adsmls.components.modal.Modal;
import com.ecnu.adsmls.utils.FileSystem;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.Objects;

public class NewProjectModal extends Modal {
    private String directory;
    private String projectName;

    private Node btDir;
    private TextField tfProjectName;

    private boolean succeed = true;

    public NewProjectModal() {
        super();
    }

    public String getDirectory() {
        return directory;
    }

    public String getProjectName() {
        return projectName;
    }

    public boolean isSucceed() {
        return succeed;
    }

    @Override
    protected void createWindow() {
        super.createWindow();
        this.setTitle("New Project");

        Label lbDirName = new Label("directory");
        this.btDir = new ChooseDirectoryButton(this.gridPane).getNode();
        Label lbProjectName = new Label("project name");
        this.tfProjectName = new TextField();

        this.slot.addRow(0, lbDirName, btDir);
        this.slot.addRow(1, lbProjectName, tfProjectName);
    }

    @Override
    protected void check() {
        this.checkDirectory();
        this.checkProjectName();
    }

    @Override
    protected void update() {
        this.updateDirectory();
        this.updateProjectName();
    }

    @Override
    protected void then() {
        this.createProject();
    }

    private void updateDirectory() {
        try {
            this.directory = ((ChooseDirectoryButton) btDir.getUserData()).getFolder().getAbsolutePath();
        } catch (Exception ignored) {

        }
    }

    private void checkDirectory() {
        if (this.directory == null) {
            this.valid = false;
        }
    }

    private void updateProjectName() {
        this.projectName = this.tfProjectName.getText();
    }

    private void checkProjectName() {
        if (Objects.equals(this.projectName, "")) {
            this.valid = false;
        }
    }

    private void createProject() {
        this.succeed = FileSystem.createDir(FileSystem.concatAbsolutePath(this.directory, this.projectName));
    }
}
