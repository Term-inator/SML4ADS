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
import java.util.LinkedHashMap;
import java.util.Map;

public class ChooseFileButton {
    private File file;
    private Pane rootLayout;

    private HBox hBox;
    private Label lbFilename;

    private String initDir;
    private Map<String, String> fileFilter = new HashMap<>();

    public ChooseFileButton(Pane rootLayout) {
        this.rootLayout = rootLayout;
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
        Button button = new Button("Choose File");
        this.lbFilename = new Label();
        this.hBox.getChildren().addAll(this.lbFilename, button);
        button.setOnMouseClicked(e -> {
            Stage stage = (Stage) rootLayout.getScene().getWindow();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose File");
            if(this.initDir != null) {
                System.out.println(this.initDir);
                fileChooser.setInitialDirectory(new File(this.initDir));
            }
            for(Map.Entry<String, String> filter : fileFilter.entrySet()) {
                String extension = filter.getKey();
                String description = filter.setValue(extension);
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter(description, extension)
                );
            }
            this.file = fileChooser.showOpenDialog(stage);
            if(this.file != null) {
                this.lbFilename.setText(file.getName());
            }
            stage.sizeToScene();
        });
        // 自适应大小
        hBox.setUserData(this);
    }

    public Node getNode() {
        return hBox;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
        this.lbFilename.setText(this.file.getName());
    }
}
