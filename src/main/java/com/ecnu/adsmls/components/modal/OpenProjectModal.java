package com.ecnu.adsmls.components.modal;

import com.ecnu.adsmls.components.ChooseDirectoryButton;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class OpenProjectModal extends Modal {
    private String directory;

    private Node btDir;

    public OpenProjectModal() {
    }

    public String getDirectory() {
        return directory;
    }

    @Override
    protected void createWindow() {
        super.createWindow();

        Label lbDirName = new Label("project");
        this.btDir = new ChooseDirectoryButton(this.gridPane).getNode();

        this.slot.addRow(0, lbDirName, this.btDir);
    }
    @Override
    protected void update() {
        this.updateDirectory();
    }

    @Override
    protected void check() {
        this.checkDirectory();
    }

    @Override
    protected void then() {
    }

    private void updateDirectory() {
        try {
            this.directory = ((ChooseDirectoryButton) this.btDir.getUserData()).getFolder().getAbsolutePath();
        }
        catch (Exception ignored) {

        }
    }

    private void checkDirectory() {
        // TODO 路径检查
    }
}
