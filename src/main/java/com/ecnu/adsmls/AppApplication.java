package com.ecnu.adsmls;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class AppApplication extends Application {
    private Scene scene;
    private static final int win_width = 900;
    private static final int win_height = 600;

    @Override
    public void start(Stage stage) throws IOException {
        this.codePage();
        stage.setTitle("ADSML-S");
        stage.setScene(scene);
        stage.show();
    }

    private void codePage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppApplication.class.getResource("codepage.fxml"));
        scene = new Scene(fxmlLoader.load(), win_width, win_height);
    }

    public static void main(String[] args) {
        launch();
    }
}