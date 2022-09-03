package com.ecnu.adsmls.components.editor.requirementeditor;

import com.ecnu.adsmls.model.MRequirement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.util.Arrays;
import java.util.stream.Collectors;


public class RequirementPane {
    public enum RequirementType {
        ALWAYS("always"),
        EVENTUALLY("eventually");

        public String value;

        RequirementType(String value) {
            this.value = value;
        }
    }

    private GridPane gridPane = new GridPane();

    private ComboBox<String> cbRequirementType;

    private TextArea taRequirement;

    public RequirementPane() {
        this.createNode();
    }

    public void check() {
        // empty
    }

    public MRequirement save() {
        MRequirement mRequirement = new MRequirement();
        mRequirement.setRequirementType(this.cbRequirementType.getValue());
        mRequirement.setRequirement(this.taRequirement.getText());
        return mRequirement;
    }

    public void load(MRequirement mRequirement) {
        this.cbRequirementType.getSelectionModel().select(mRequirement.getRequirementType());
        this.taRequirement.setText(mRequirement.getRequirement());
    }

    public void createNode() {
        this.gridPane.setPadding(new Insets(4, 0, 4, 0));
        this.gridPane.setHgap(8);

        AnchorPane requirementTypeWrapper = new AnchorPane();
        this.cbRequirementType = new ComboBox<>();
        String[] requirementTypes =
                Arrays.stream(RequirementType.values())
                        .map(requirementType -> requirementType.value)
                        .collect(Collectors.toList()).toArray(String[]::new);
        this.cbRequirementType.setItems(FXCollections.observableArrayList(requirementTypes));
        this.cbRequirementType.getSelectionModel().select(RequirementType.ALWAYS.value);
        requirementTypeWrapper.getChildren().add(this.cbRequirementType);
        AnchorPane.setTopAnchor(this.cbRequirementType, 0.0);

        this.taRequirement = new TextArea();
        this.taRequirement.setPrefRowCount(4);
        this.taRequirement.setMinHeight(76);
        //自动换行
        this.taRequirement.setWrapText(true);

        this.gridPane.addRow(0, requirementTypeWrapper, this.taRequirement);
    }

    public Node getNode() {
        return this.gridPane;
    }
}
