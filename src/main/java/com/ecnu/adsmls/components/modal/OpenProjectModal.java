package com.ecnu.adsmls.components.modal;

import com.ecnu.adsmls.components.ChooseDirectoryButton;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class OpenProjectModal extends Modal {
    private String directory;

    public OpenProjectModal() {
    }

    public String getDirectory() {
        return directory;
    }

    @Override
    protected void createWindow() {
        super.createWindow();

        Label lbDirName = new Label("project");
        ChooseDirectoryButton btDir = new ChooseDirectoryButton(this.gridPane);

        staticPage.add(0, new Node[] {lbDirName, btDir.getNode()});
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
}
