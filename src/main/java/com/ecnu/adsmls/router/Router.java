package com.ecnu.adsmls.router;

import com.ecnu.adsmls.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.util.*;

public class Router {
    private static final int win_width = 900;
    private static final int win_height = 600;

    public static Deque<Scene> sceneStack = new ArrayDeque<>();

    public static Map<String, Scene> sceneMap = new HashMap<>();

    static {
        try {
            sceneMap.put("welcome", welcome());
            sceneMap.put("codepage", codePage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Scene welcome() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("welcome.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), win_width, win_height);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        return scene;
    }

    private static Scene codePage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("codepage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), win_width, win_height);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        return scene;
    }

    public static void linkTo(String pageName) {
        sceneStack.push(sceneMap.get(pageName));
        changeScene();
    }

    public static void back() {
        sceneStack.pop();
        changeScene();
    }

    public static void changeScene() {
        App.getMainStage().setScene(sceneStack.peek());
        App.getMainStage().show();
    }
}
