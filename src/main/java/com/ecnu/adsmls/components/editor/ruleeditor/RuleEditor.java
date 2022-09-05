package com.ecnu.adsmls.components.editor.ruleeditor;

import com.ecnu.adsmls.components.editor.FormEditor;
import com.ecnu.adsmls.utils.register.exception.DataTypeException;
import com.ecnu.adsmls.utils.register.exception.EmptyParamException;
import com.ecnu.adsmls.utils.register.exception.RequirementException;
import javafx.scene.Node;

import java.io.File;
import java.util.LinkedHashMap;

public class RuleEditor extends FormEditor {
    public RuleEditor(String projectPath, File file) {
        super(projectPath, file);
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
    public Node getNode() {
        return this.gridPane;
    }
}
