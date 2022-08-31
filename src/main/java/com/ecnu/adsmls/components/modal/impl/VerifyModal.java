package com.ecnu.adsmls.components.modal.impl;

import com.ecnu.adsmls.components.modal.Modal;
import com.ecnu.adsmls.model.MModel;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class VerifyModal extends Modal {
    private TextField tfOutputPath;
    private String outputPath;

    public VerifyModal() {
    }

    public String getOutputPath() {
        return outputPath;
    }

    @Override
    protected void createWindow() {
        super.createWindow();
        this.setTitle("Verify");

        Label lbOutputPath = new Label("output file path");
        this.tfOutputPath = new TextField();
        this.tfOutputPath.setPromptText("default: ${model.dir}/${model.name}.xml");

        this.slot.addRow(0, lbOutputPath, this.tfOutputPath);
    }

    @Override
    protected void update() {
        this.outputPath = this.tfOutputPath.getText();
    }

    @Override
    protected void check() {
        // empty
    }

    @Override
    protected void then() {

    }
}
