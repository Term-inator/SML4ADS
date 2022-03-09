package com.ecnu.adsmls.components.modal;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class VerifyRequirementsModal extends Modal {
    private ListView<String> listView;
    private List<String> requirements = new ArrayList<>();

    public List<String> getRequirements() {
        return requirements;
    }

    @Override
    protected void createWindow() {
        super.createWindow();
        this.window.setTitle("Requirements");

        this.listView = new ListView<>();
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

        this.slot.addRow(0, this.listView, btWrapper);
    }

    @Override
    protected void update() {
        for(String requirement : this.listView.getItems()) {
            this.requirements.add(requirement);
        }
    }

    @Override
    protected void check() {
        // TODO
    }

    @Override
    protected void then() {

    }
}
