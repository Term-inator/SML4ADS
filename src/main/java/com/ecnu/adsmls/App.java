package com.ecnu.adsmls;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;

public class App extends Application {
    private Scene scene;
    private static final int win_width = 900;
    private static final int win_height = 600;

    @Override
    public void start(Stage stage) throws IOException {
        this.codePage();
        stage.setTitle("ADSML-S");
        stage.setScene(scene);
        stage.getScene().getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        stage.show();
    }

    private void codePage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("codepage.fxml"));
        scene = new Scene(fxmlLoader.load(), win_width, win_height);
    }

    public static void main(String[] args) {
        launch();
    }
}