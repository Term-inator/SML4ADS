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

// TODO createNode HGap 设置当前项目目录为初始目录
public class ChooseDirectoryButton {
    private File folder;
    private Pane rootLayout;

    public ChooseDirectoryButton(Pane rootLayout) {
        this.rootLayout = rootLayout;
    }

    public Node getNode() {
        HBox hBox0 = new HBox();
        hBox0.setAlignment(Pos.CENTER_LEFT);
        Button button = new Button("Choose Directory");
        Label lbDirName = new Label();
        hBox0.getChildren().addAll(lbDirName, button);
        button.setOnMouseClicked(e -> {
            Stage stage = (Stage) rootLayout.getScene().getWindow();
            DirectoryChooser dirChooser = new DirectoryChooser();
            dirChooser.setTitle("Choose Directory");
            folder = dirChooser.showDialog(stage);
            if(folder != null) {
                lbDirName.setText(folder.getAbsolutePath());
            }
        });
        hBox0.setUserData(this);
        return hBox0;
    }

    public File getFolder() {
        return folder;
    }
}
