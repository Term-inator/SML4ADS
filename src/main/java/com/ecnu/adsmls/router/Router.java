package com.ecnu.adsmls.router;

import com.ecnu.adsmls.App;
import com.ecnu.adsmls.router.params.CodePageParams;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.util.*;

public class Router {
    private static final int win_width = 900;
    private static final int win_height = 600;

    public static Deque<Scene> sceneStack = new ArrayDeque<>();

    public static Map<String, Scene> sceneMap = new HashMap<>();

    public static Map<String, Route> controllerMap = new HashMap<>();

    static {
        try {
            welcome();
            codePage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void welcome() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("welcome.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), win_width, win_height);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        controllerMap.put("welcome", fxmlLoader.getController());
        sceneMap.put("welcome", scene);
    }

    public static void codePage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("codepage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), win_width, win_height);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        controllerMap.put("codepage", fxmlLoader.getController());
        sceneMap.put("codepage", scene);
    }

    public static void linkTo(String pageName) {
        sceneStack.push(sceneMap.get(pageName));
        controllerMap.get(pageName).loadParams();
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
