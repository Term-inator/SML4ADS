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

public class ChooseFileButton {
    private File file;
    private Pane rootLayout;

    private Label lbFilename;

    public ChooseFileButton(Pane rootLayout) {
        this.rootLayout = rootLayout;
    }

    public Node getNode() {
        HBox hBox0 = new HBox();
        hBox0.setAlignment(Pos.CENTER_LEFT);
        Button button = new Button("Choose File");
        this.lbFilename = new Label();
        hBox0.getChildren().addAll(this.lbFilename, button);
        button.setOnMouseClicked(e -> {
            Stage stage = (Stage) rootLayout.getScene().getWindow();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            file = fileChooser.showOpenDialog(stage);
            if(file != null) {
                this.lbFilename.setText(file.getName());
            }
        });
        hBox0.setUserData(this);
        return hBox0;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
        this.lbFilename.setText(this.file.getName());
    }
}
