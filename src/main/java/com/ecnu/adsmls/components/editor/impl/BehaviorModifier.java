package com.ecnu.adsmls.components.editor.impl;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class BehaviorModifier {
    private Stage window;

    public BehaviorModifier() {
        window = new Stage(StageStyle.TRANSPARENT);
        window.initModality(Modality.WINDOW_MODAL);
        window.setOpacity(0.87);
        TextField tf = new TextField();
        //initial value of text field should be user-defined
        tf.setMinHeight(30);
        Button okBtn = new Button("OK");
        //action for okBtn should be user-defined
        okBtn.setMinWidth(70);
        okBtn.setMinHeight(30);
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                window.close();
            }
        });
        cancelBtn.setMinWidth(70);
        cancelBtn.setMinHeight(30);
        HBox hBox = new HBox(25, okBtn, cancelBtn);
        hBox.setPadding(new Insets(0, 20, 0, 20));
        VBox vBox = new VBox(15, tf, hBox);
        vBox.setPadding(new Insets(40, 10, 20, 10));
        Scene scene = new Scene(vBox);
        scene.setFill(Color.TRANSPARENT);
        //draw special window
        vBox.setBackground(new Background(
                new BackgroundFill(
                        new LinearGradient(1, 1, 1, 0, true, CycleMethod.REFLECT,
                                new Stop(0.0, Color.LIGHTBLUE), new Stop(1.0, Color.WHITE)),
                        new CornerRadii(15),
                        Insets.EMPTY)));
        window.setScene(scene);
    }

    public Stage getWindow() {
        return window;
    }
}
