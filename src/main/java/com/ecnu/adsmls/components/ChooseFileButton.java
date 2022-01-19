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

    public ChooseFileButton(Pane rootLayout) {
        this.rootLayout = rootLayout;
    }

    public Node getNode() {
        HBox hBox0 = new HBox();
        hBox0.setAlignment(Pos.CENTER_LEFT);
        Button button = new Button("Choose File");
        Label lbFileName = new Label();
        hBox0.getChildren().addAll(lbFileName, button);
        button.setOnMouseClicked(e -> {
            Stage stage = (Stage) rootLayout.getScene().getWindow();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            file = fileChooser.showOpenDialog(stage);
            if(file != null) {
                lbFileName.setText(file.getName());
            }
        });
        return hBox0;
    }

    public File getFile() {
        return file;
    }
}
