package com.ecnu.adsmls.components.choosebutton;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public abstract class ChooseButton {
    protected File file;
    protected Pane rootLayout;

    protected HBox hBox;
    protected Label lbFilename;
    protected Button btChoose;
    protected Map<String, String> fileFilter = new HashMap<>();
    // 默认不带清空按钮
    protected boolean clearable = false;
    protected Button btClear;

    protected String initDir;

    public ChooseButton(Pane rootLayout) {
        this.rootLayout = rootLayout;
        this.createNode();
    }

    public ChooseButton(Pane rootLayout, String initDir) {
        this.rootLayout = rootLayout;
        this.initDir = initDir;
        this.createNode();
    }

    protected void createNode() {
        this.hBox = new HBox();
        this.hBox.setSpacing(5);
        this.hBox.setAlignment(Pos.CENTER_LEFT);
        this.btChoose = new Button();
        this.lbFilename = new Label();
        this.btChoose.setOnMouseClicked(e -> chooseFile());
        this.btClear = new Button("Clear");
        this.btClear.setOnAction(e -> clearFile());
        this.hBox.getChildren().addAll(this.lbFilename, this.btChoose);
    }

    protected abstract void chooseFile();

    private void clearFile() {
        this.setFile(null);
    }

    private void showClearButton() {
        if (!this.hBox.getChildren().contains(this.btClear)) {
            this.hBox.getChildren().add(this.btClear);
        }
    }

    private void hideClearButton() {
        this.hBox.getChildren().remove(this.btClear);
    }

    public void setClearable(boolean clearable) {
        this.clearable = clearable;
    }

    public Node getNode() {
        return hBox;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
        if (file != null) {
            this.lbFilename.setText(this.file.getAbsolutePath());
            if (this.clearable) {
                this.showClearButton();
            }
        }
        else {
            this.lbFilename.setText("");
            if (this.clearable) {
                this.hideClearButton();
            }
        }
    }
}
