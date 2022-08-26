package com.ecnu.adsmls.components.modal;

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

public class VerifyRequirementsModal extends Modal {
    private MModel mModel;

    private ListView<String> listView = new ListView<>();
    private List<String> requirements = new ArrayList<>();

    private TextField tfOutputPath;
    private String outputPath;

    public VerifyRequirementsModal(MModel mModel) {
        this.mModel = mModel;
        this.loadData();
    }

    public List<String> getRequirements() {
        return requirements;
    }

    public String getOutputPath() {
        return outputPath;
    }

    private void loadData() {
        this.requirements = this.mModel.getRequirements();
        this.listView.setItems(FXCollections.observableList(this.requirements));
    }

    @Override
    protected void createWindow() {
        super.createWindow();
        this.setTitle("Verify");

        Label lbRequirements = new Label("requirements");

        this.listView.setCellFactory(TextFieldListCell.forListView());
        this.listView.setEditable(true);

        VBox btWrapper = new VBox();
        btWrapper.setAlignment(Pos.CENTER);
        btWrapper.setSpacing(5.0);
        Button btAdd = new Button("Add");
        btAdd.setOnAction(e -> {
            this.listView.getItems().add("");
        });

        Button btDelete = new Button("Delete");
        btDelete.setOnAction(e -> {
            String selected = this.listView.getSelectionModel().getSelectedItem();
            this.listView.getItems().remove(selected);
        });

        btWrapper.getChildren().addAll(btAdd, btDelete);

        Label lbOutputPath = new Label("output file path");
        this.tfOutputPath = new TextField();
        this.tfOutputPath.setPromptText("default: ${model.dir}/${model.name}.xml");

        this.slot.addRow(0, lbRequirements);
        this.slot.addRow(1, this.listView, btWrapper);
        this.slot.addRow(2, lbOutputPath);
        this.slot.addRow(3, this.tfOutputPath);
    }

    @Override
    protected void update() {
        this.requirements = this.listView.getItems();
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
