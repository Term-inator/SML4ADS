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

    // 填写是否合法
    protected boolean valid = true;
    // 是否点击了确认
    protected boolean confirm = true;

    public Modal() {

    }

    public boolean isConfirm() {
        return this.confirm;
    }

    protected void createWindow() {
        window = new Stage(StageStyle.TRANSPARENT);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setOpacity(0.87);

        gridPane = new GridPane();
        gridPane.setPadding(new Insets(15, 20, 15, 20));
        gridPane.setVgap(8);
        gridPane.setHgap(5);
        //draw special window
        gridPane.setBackground(new Background(
                new BackgroundFill(
                        new LinearGradient(1, 1, 1, 0, true, CycleMethod.REFLECT,
                                new Stop(0.0, Color.LIGHTBLUE), new Stop(1.0, Color.WHITE)),
                        new CornerRadii(15),
                        Insets.EMPTY)));

        staticPage.add(new Node[] {btConfirm, btCancel});

        this.bindConfirmCancel();

        Scene scene = new Scene(gridPane);
        scene.setFill(Color.TRANSPARENT);

        window.setScene(scene);
    }

    protected void bindConfirmCancel() {
        btConfirm.setOnAction(e -> {
            this.confirm(e);
            if(valid) {
                this.window.close();
            }
        });

        btCancel.setOnAction(e -> {
            this.cancel(e);
            this.window.close();
        });
    }

    protected abstract void confirm(ActionEvent e);

    protected void cancel(ActionEvent e) {
        this.confirm = false;
    }

    protected abstract void check();

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
