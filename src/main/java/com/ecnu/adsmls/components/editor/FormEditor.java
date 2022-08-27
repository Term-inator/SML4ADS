package com.ecnu.adsmls.components.editor;

import javafx.geometry.Insets;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.io.File;

public abstract class FormEditor extends Editor {
    protected GridPane gridPane = new GridPane();

    public FormEditor(String projectPath, File file) {
        super(projectPath, file);
    }

    @Override
    protected void createNode() {
        this.gridPane.setPrefWidth(800);
        this.gridPane.setPrefWidth(800);
        this.gridPane.setPadding(new Insets(30, 40, 30, 40));
        this.gridPane.setVgap(8);
    }

    protected void bindKeyEvent() {
        this.gridPane.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            System.out.println(e);
            if (e.isControlDown() && e.getCode() == KeyCode.S) {
                this.save();
            }
        });
    }

    protected void bindMouseEvent() {
        this.gridPane.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.gridPane.requestFocus();
        });
    }
}
