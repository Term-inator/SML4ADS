package com.ecnu.adsmls.components;

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

public class ChooseFileButton {
    private File file;
    private Pane rootLayout;

    private HBox hBox;
    private Label lbFilename;
    private Button btChoose;
    // 默认不带清空按钮
    private boolean clearable = false;
    private Button btClear;

    private String initDir;
    private Map<String, String> fileFilter = new HashMap<>();

    public ChooseFileButton(Pane rootLayout) {
        this.rootLayout = rootLayout;
        this.createNode();
    }

    public ChooseFileButton(Pane rootLayout, Map<String, String> fileFilter) {
        this.rootLayout = rootLayout;
        this.fileFilter = fileFilter;
        this.createNode();
    }

    public ChooseFileButton(Pane rootLayout, String initDir) {
        this.rootLayout = rootLayout;
        this.initDir = initDir;
        this.createNode();
    }

    public ChooseFileButton(Pane rootLayout, String initDir, Map<String, String> fileFilter) {
        this.rootLayout = rootLayout;
        this.initDir = initDir;
        this.fileFilter = fileFilter;
        this.createNode();
    }

    private void createNode() {
        this.hBox = new HBox();
        this.hBox.setSpacing(5);
        this.hBox.setAlignment(Pos.CENTER_LEFT);
        this.btChoose = new Button("Choose File");
        this.lbFilename = new Label();
        this.btChoose.setOnMouseClicked(e -> chooseFile());
        this.btClear = new Button("Clear");
        this.btClear.setOnAction(e -> clearFile());
        this.hBox.getChildren().addAll(this.lbFilename, this.btChoose);
        hBox.setUserData(this);
    }

    private void chooseFile() {
        Stage stage = (Stage) this.rootLayout.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose File");
        if (this.initDir != null) {
            System.out.println(this.initDir);
            fileChooser.setInitialDirectory(new File(this.initDir));
        }
        for (Map.Entry<String, String> filter : fileFilter.entrySet()) {
            String extension = filter.getKey();
            String description = filter.setValue(extension);
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(description, extension)
            );
        }
        File result = fileChooser.showOpenDialog(stage);
        if (result != null) {
            this.setFile(result);
        }
        // 自适应大小
        stage.sizeToScene();
    }

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
