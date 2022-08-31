package com.ecnu.adsmls.components.editor.requirementeditor;

import com.ecnu.adsmls.components.editor.FormEditor;
import com.ecnu.adsmls.router.params.Global;
import com.ecnu.adsmls.utils.register.Function;
import com.ecnu.adsmls.utils.register.FunctionParam;
import com.ecnu.adsmls.utils.register.exception.DataTypeException;
import com.ecnu.adsmls.utils.register.exception.EmptyParamException;
import com.ecnu.adsmls.utils.register.exception.RequirementException;
import com.ecnu.adsmls.utils.register.impl.WeatherRegister;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RequirementEditor extends FormEditor {
    private Label lbRequirements;
    private ListView<String> listView = new ListView<>();
    private List<String> requirements = new ArrayList<>();

    private VBox btWrapper;
    private Button btAdd;
    private Button btDelete;

    public RequirementEditor(String projectPath, File file) {
        super(projectPath, file);
        this.createNode();
    }

    @Override
    public void check() throws EmptyParamException, RequirementException, DataTypeException {

    }

    @Override
    public void save() {

    }

    @Override
    public void load() {

    }

    @Override
    protected void createNode() {
        super.createNode();
        this.bindKeyEvent();
        this.bindMouseEvent();

        this.lbRequirements = new Label("requirements");
        this.listView.setCellFactory(TextFieldListCell.forListView());
        this.listView.setEditable(true);

        this.btWrapper = new VBox();
        this.btWrapper.setAlignment(Pos.CENTER);
        this.btWrapper.setSpacing(5.0);
        this.btAdd = new Button("Add");
        this.btAdd.setOnAction(e -> {
            this.listView.getItems().add("");
        });

        this.btDelete = new Button("Delete");
        this.btDelete.setOnAction(e -> {
            String selected = this.listView.getSelectionModel().getSelectedItem();
            this.listView.getItems().remove(selected);
        });

        this.btWrapper.getChildren().addAll(this.btAdd, this.btDelete);

        this.gridPane.addRow(0, this.lbRequirements);
        this.gridPane.addRow(1, this.listView, this.btWrapper);
    }

    @Override
    public Node getNode() {
        return this.gridPane;
    }
}
