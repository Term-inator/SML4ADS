package com.ecnu.adsmls;

import com.ecnu.adsmls.router.Router;
import javafx.application.Application;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;

public class App extends Application {
    private static Stage mainStage;

    public static Stage getMainStage() {
        return mainStage;
    }

    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("ADSML-S");
        mainStage = stage;
        Router.linkTo("welcome");
    }

    public static void main(String[] args) {
        launch();
    }
}