package com.ecnu.adsmls.components;

import com.ecnu.adsmls.components.editor.impl.Behavior;
import com.ecnu.adsmls.components.editor.impl.BehaviorRegister;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.*;

public abstract class Modal {
    protected Stage window;

    protected GridPane gridPane;
    // 静态部分
    protected ArrayList<Node[]> staticPage = new ArrayList<>();

    protected Button btConfirm = new Button("Confirm");;
    protected Button btCancel = new Button("Cancel");;

    private StageStyle stageStyle = StageStyle.DECORATED;
    private double opacity = 1f;
    private Background background = null;

    // 填写是否合法
    protected boolean valid = true;
    // 是否点击了确认
    protected boolean confirm = true;

    public Modal() {

    }

    public boolean isConfirm() {
        return this.confirm;
    }

    public void setStyle(String key, Object obj) {
        if(Objects.equals(key, "stageStyle")) {
            this.stageStyle = (StageStyle) obj;
        }
        else if(Objects.equals(key, "opacity")) {
            this.opacity = (Double) obj;
        }
        else if(Objects.equals(key, "background")) {
            this.background = (Background) obj;
        }
    }

    protected void createWindow() {
        window = new Stage(this.stageStyle);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setOpacity(this.opacity);

        gridPane = new GridPane();
        gridPane.setPadding(new Insets(15, 20, 15, 20));
        gridPane.setVgap(8);
        gridPane.setHgap(5);

        gridPane.setBackground(this.background);

        staticPage.add(new Node[] {btConfirm, btCancel});

        this.bindConfirmCancel();

        Scene scene = new Scene(gridPane);
        scene.setFill(Color.TRANSPARENT);

        window.setScene(scene);
    }

    protected void bindConfirmCancel() {
        btConfirm.setOnAction(e -> {
            this.update();
            this.check();
            if(valid) {
                this.then();
                this.window.close();
            }
            else {
                this.valid = true;
            }
        });

        btCancel.setOnAction(e -> {
            this.confirm = false;
            this.window.close();
        });
    }

    /**
     * 更新数据和界面，在 confirm 中调用
     */
    protected abstract void update();

    /**
     * 检查数据是否合法，在 confirm 中调用
     */
    protected abstract void check();

    /**
     * confirm 且数据 valid 后执行的操作
     */
    protected abstract void then();

    protected void updateGridPane() {
        this.gridPane.getChildren().clear();
        for(int r = 0; r < staticPage.size(); ++r) {
            gridPane.addRow(r, staticPage.get(r));
        }

        window.sizeToScene();
    }

    public Stage getWindow() {
        this.createWindow();
        this.updateGridPane();
        return window;
    }
}
