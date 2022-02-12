package com.ecnu.adsmls.components.mutileveldirectory;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MultiLevelDirectory {
    private File directory;

    private TitledPane titledPane;

    public MultiLevelDirectory(File directory) {
        this.directory = directory;
        this.titledPane = new TitledPane();
        this.titledPane.setUserData(this.directory);
        this.titledPane.setPrefWidth(240);
        this.titledPane.setMinWidth(200);
        this.titledPane.setMaxWidth(300);
        this.titledPane.setText(this.directory.getName());
        this.titledPane.setContent(this.createNode(this.directory));
    }

    private VBox createNode(File root) {
        VBox vBox = new VBox();
        vBox.setSpacing(5);

        File[] fileList = root.listFiles();
        List<File> fileBuffer = new ArrayList<>();
        assert fileList != null;
        for(File file : fileList) {
            if(file.isFile()) {
                fileBuffer.add(file);
            }
            else {
                TitledPane titledPane = new TitledPane(file.getName(), null);
                titledPane.setUserData(file);
                titledPane.setStyle("-fx-box-border: transparent");
                titledPane.setMinWidth(100);
                titledPane.setOnMouseClicked(e -> {
                    if(titledPane.isExpanded()) {
                        System.out.println("expanded");
                        return;
                    }
                    titledPane.setContent(this.createNode(file));
                });
                vBox.getChildren().add(titledPane);
            }
        }
        for(File file : fileBuffer) {
            Label lbFilename = new Label(file.getName());
            lbFilename.setFont(Font.font(15));
            lbFilename.setUserData(file);
            vBox.getChildren().add(lbFilename);
        }
        vBox.setPadding(new Insets(0, 0, 0, 10));
        return vBox;
    }

    public Node getNode() {
        return this.titledPane;
    }
}
