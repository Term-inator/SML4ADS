package com.ecnu.adsmls.components.modal;

import com.ecnu.adsmls.components.ChooseDirectoryButton;
import com.ecnu.adsmls.components.modal.Modal;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.Objects;

public class NewTreeModal extends Modal {
    private String directory;

    private String filename;

    public NewTreeModal() {
        super();
    }

    public String getFilename() {
        return filename;
    }

    public String getDirectory() {
        return directory;
    }

    @Override
    protected void createWindow() {
        super.createWindow();

        Label lbDirName = new Label("directory");
        ChooseDirectoryButton btDir = new ChooseDirectoryButton(this.gridPane);
        Label lbFilename = new Label("filename");
        TextField tfFilename = new TextField();

        staticPage.add(0, new Node[] {lbDirName, btDir.getNode()});
        staticPage.add(1, new Node[] {lbFilename, tfFilename});
    }

    @Override
    protected void check() {
        this.checkDirectory();
        this.checkFilename();
    }

    @Override
    protected void update() {
        this.updateDirectory();
        this.updateFileName();
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

    private void updateFileName() {
        TextField tfFilename = (TextField) this.staticPage.get(1)[1];
        this.filename = tfFilename.getText();
    }

    private void checkFilename() {
        // TODO 文件名检查
        if(Objects.equals(this.filename, "")) {
            this.valid = false;
        }
    }
}
