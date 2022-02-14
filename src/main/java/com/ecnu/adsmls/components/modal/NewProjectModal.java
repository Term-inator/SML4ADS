package com.ecnu.adsmls.components.modal;

import com.ecnu.adsmls.components.ChooseDirectoryButton;
import com.ecnu.adsmls.utils.FileSystem;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.Objects;

public class NewProjectModal extends Modal {
    private String directory;

    private String projectName;

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

        Label lbDirName = new Label("directory");
        ChooseDirectoryButton btDir = new ChooseDirectoryButton(this.gridPane);
        Label lbProjectName = new Label("project name");
        TextField tfProjectName = new TextField();

        staticPage.add(0, new Node[] {lbDirName, btDir.getNode()});
        staticPage.add(1, new Node[] {lbProjectName, tfProjectName});
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
        ChooseDirectoryButton btDir = (ChooseDirectoryButton) this.staticPage.get(0)[1].getUserData();
        try {
            this.directory = btDir.getFolder().getAbsolutePath();
        }
        catch (Exception ignored) {

        }
    }

    private void checkDirectory() {
        // TODO 路径检查
    }

    private void updateProjectName() {
        TextField tfFilename = (TextField) this.staticPage.get(1)[1];
        this.projectName = tfFilename.getText();
    }

    private void checkProjectName() {
        // TODO 文件名检查
        if(Objects.equals(this.projectName, "")) {
            this.valid = false;
        }
    }

    private void createProject() {
        this.succeed = FileSystem.createDir(directory + '/' + projectName);
    }
}
