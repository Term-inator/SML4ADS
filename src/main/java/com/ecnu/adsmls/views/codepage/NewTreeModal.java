package com.ecnu.adsmls.views.codepage;

import com.ecnu.adsmls.components.Modal;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.Objects;

public class NewTreeModal extends Modal {
    private String filename;

    public NewTreeModal() {
        super();
    }

    public String getFilename() {
        return filename;
    }

    @Override
    protected void createWindow() {
        super.createWindow();

        Label lbFilename = new Label("filename");
        TextField tfFilename = new TextField();

        staticPage.add(0, new Node[] {lbFilename, tfFilename});
    }

    @Override
    protected void confirm(ActionEvent e) {
        this.updateFileName();
        this.check();
        if(this.valid) {
        }
    }

    @Override
    protected void check() {
        this.checkFilename();
    }

    private void updateFileName() {
        for(Node node : gridPane.getChildren()) {
            if(node instanceof TextField) {
                this.filename = ((TextField) node).getText();
            }
        }
    }

    private void checkFilename() {
        // TODO 文件名检查
        if(Objects.equals(this.filename, "")) {
            this.valid = false;
        }
    }
}
