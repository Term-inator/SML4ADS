package com.ecnu.adsmls.components.mutileveldirectory;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.File;


public class MultiLevelDirectory {
    private File directory;

    private TitledPane titledPane;

    public MultiLevelDirectory(File directory) {
        this.directory = directory;
        this.titledPane = new TitledPane(this.directory.getName(), this.createNode(this.directory));
        this.titledPane.setMinWidth(100);
    }

    private VBox createNode(File root) {
        VBox vBox = new VBox();
        File[] fileList = root.listFiles();
        assert fileList != null;
        for(File file : fileList) {
            if(file.isFile()) {
                Label lbFilename = new Label(file.getName());
                lbFilename.setUserData(file);
                vBox.getChildren().add(lbFilename);
            }
            else {
                TitledPane titledPane = new TitledPane(file.getName(), null);
                titledPane.setUserData(file);
                titledPane.setStyle("-fx-box-border: transparent");
                titledPane.setOnMouseClicked(e -> {
                    titledPane.setContent(this.createNode(file));
                    titledPane.setMinWidth(100);
                });
                vBox.getChildren().add(titledPane);
            }
        }
        vBox.setPadding(new Insets(0, 0, 0, 10));
        return vBox;
//        for(Node node : children) {
//            if()
//        }
//
//        while(!files.isEmpty()) {
//            File file = files.pop();
//            if(file.isFile()) {
//                AnchorPane anchorPane = new AnchorPane();
//                Label lbFilename = new Label(file.getName());
//                anchorPane.getChildren().add(lbFilename);
//                retNode = anchorPane;
//            }
//            else {
//                AnchorPane anchorPane = new AnchorPane();
//                anchorPane.getChildren().add(retNode);
//                TitledPane titledPane = new TitledPane(file.getName(), anchorPane);
//
//                titledPane.setOnMouseClicked(e -> {
//                    MouseButton button = e.getButton();
//                    if(button == MouseButton.SECONDARY) {
//                        System.out.println("secondary");
//                    }
//                });
//            }
//        }
    }

    public Node getNode() {
        return this.titledPane;
    }
}
