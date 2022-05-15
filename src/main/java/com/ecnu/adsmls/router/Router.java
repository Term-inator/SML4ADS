package com.ecnu.adsmls.router;

import com.ecnu.adsmls.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * 路由，负责页面跳转
 * 即创建 Scene 和切换 Scene
 */
public class Router {
    private static final int win_width = 900;
    private static final int win_height = 600;

    /**
     * Scene 栈，栈顶为当前显示的页面
     */
    public static Deque<Scene> sceneStack = new ArrayDeque<>();

    /**
     * 页面名对 Scene 的映射表
     */
    public static Map<String, Scene> sceneMap = new HashMap<>();

    /**
     * 页面名对实现了 Route 的 Controller 的映射表
     */
    public static Map<String, Route> controllerMap = new HashMap<>();

    /**
     * 初始化页面信息
     */
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

    /**
     * 页面跳转
     *
     * @param pageName 页面名
     */
    public static void linkTo(String pageName) {
        sceneStack.push(sceneMap.get(pageName));
        controllerMap.get(pageName).loadParams();
        changeScene();
    }

    /**
     * 页面后退
     */
    public static void back() {
        sceneStack.pop();
        changeScene();
    }

    /**
     * 切换页面（Scene）
     */
    public static void changeScene() {
        assert sceneStack.peek() != null;
        App.getMainStage().setScene(sceneStack.peek());
        App.getMainStage().show();
    }
}
