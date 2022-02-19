package com.ecnu.adsmls.components;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

// TODO 设置当前项目目录为初始目录
public class ChooseDirectoryButton {
    private File folder;
    private Pane rootLayout;

    private HBox hBox;
    private Label lbDirName;

    public ChooseDirectoryButton(Pane rootLayout) {
        this.rootLayout = rootLayout;
        this.createNode();
    }

    private void createNode() {
        this.hBox = new HBox();
        this.hBox.setSpacing(5);
        this.hBox.setAlignment(Pos.CENTER_LEFT);
        Button button = new Button("Choose Directory");
        this.lbDirName = new Label();
        this.hBox.getChildren().addAll(this.lbDirName, button);
        button.setOnMouseClicked(e -> {
            Stage stage = (Stage) rootLayout.getScene().getWindow();
            DirectoryChooser dirChooser = new DirectoryChooser();
            dirChooser.setTitle("Choose Directory");
            this.folder = dirChooser.showDialog(stage);
            if(this.folder != null) {
                this.lbDirName.setText(folder.getAbsolutePath());
            }
            stage.sizeToScene();
        });
        hBox.setUserData(this);
    }

    public Node getNode() {
        return hBox;
    }

    public File getFolder() {
        return folder;
    }
}
