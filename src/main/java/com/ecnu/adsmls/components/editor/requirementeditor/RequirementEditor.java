package com.ecnu.adsmls.components.editor.requirementeditor;

import com.alibaba.fastjson.JSON;
import com.ecnu.adsmls.components.editor.FormEditor;
import com.ecnu.adsmls.model.MRequirement;
import com.ecnu.adsmls.model.MRequirements;
import com.ecnu.adsmls.router.params.Global;
import com.ecnu.adsmls.utils.FileSystem;
import com.ecnu.adsmls.utils.GridPaneUtils;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RequirementEditor extends FormEditor {
    private Label lbRequirements;

    private GridPane gridPaneRequirement = new GridPane();
    private Map<Integer, RequirementPane> requirementPanes = new LinkedHashMap();
    private int requirementId = 0;

    public RequirementEditor(String projectPath, File file) {
        super(projectPath, file);
        this.createNode();
    }

    @Override
    public void check() throws EmptyParamException, RequirementException, DataTypeException {

    }

    @Override
    public void save() {
        String requirements = FileSystem.JSONReader(new File(this.projectPath, this.relativePath));
        MRequirements mRequirements = JSON.parseObject(requirements, MRequirements.class);
        if(mRequirements == null) {
            mRequirements = new MRequirements();
        }
        System.out.println(mRequirements);

        List<MRequirement> requirementList = new ArrayList<>();
        for(Map.Entry<Integer, RequirementPane> entry: this.requirementPanes.entrySet()) {
            RequirementPane requirementPane = entry.getValue();
            requirementList.add(requirementPane.save());
        }
        mRequirements.setRequirements(requirementList);
        requirements = JSON.toJSONString(mRequirements);
        System.out.println(requirements);
        FileSystem.JSONWriter(new File(this.projectPath, this.relativePath), requirements);
    }

    @Override
    public void load() {
        String requirements = FileSystem.JSONReader(new File(this.projectPath, this.relativePath));
        MRequirements mRequirements = JSON.parseObject(requirements, MRequirements.class);
        if (mRequirements == null) {
            return;
        }
        System.out.println(requirements);

        for (MRequirement mRequirement: mRequirements.getRequirements()) {
            RequirementPane requirementPane = new RequirementPane();
            // 设置 requirementPane 数据
            requirementPane.load(mRequirement);
            this.newRequirement(requirementPane);
        }
    }

    @Override
    protected void createNode() {
        super.createNode();
        this.bindKeyEvent();
        this.bindMouseEvent();

        this.lbRequirements = new Label("requirements");

        Button btAddRequirement = new Button("Add");
        btAddRequirement.setOnMouseClicked(e -> {
            this.newRequirement(new RequirementPane());
        });

        this.gridPane.addRow(0, this.lbRequirements);
        this.gridPane.addRow(1, this.gridPaneRequirement);
        this.gridPane.addRow(2, btAddRequirement);
    }

    private void newRequirement(RequirementPane requirementPane) {
        this.requirementPanes.put(this.requirementId++, requirementPane);
        this.updateRequirements();
    }

    private void deleteRequirement(int index) {
        System.out.println("delete requirement " + index);
        RequirementPane requirementPane = this.requirementPanes.remove(index);
        this.updateRequirements();
    }

    private void updateRequirements() {
        List<Node[]> page = new ArrayList<>();

        int i = 0;
        for(Map.Entry<Integer, RequirementPane> entry: this.requirementPanes.entrySet()) {
            RequirementPane requirement = entry.getValue();
            AnchorPane buttonWrapper = new AnchorPane();
            Button btDelete = new Button("Delete");
            btDelete.setOnAction(e -> {
                this.deleteRequirement(entry.getKey());
            });
            buttonWrapper.getChildren().add(btDelete);
            AnchorPane.setTopAnchor(btDelete, 4.0);
            AnchorPane.setLeftAnchor(btDelete, 8.0);
            page.add(new Node[] {requirement.getNode(), buttonWrapper});
            ++i;
        }

        GridPaneUtils.updateGridPane(this.gridPaneRequirement, page);
    }

    @Override
    public Node getNode() {
        return this.gridPane;
    }
}
