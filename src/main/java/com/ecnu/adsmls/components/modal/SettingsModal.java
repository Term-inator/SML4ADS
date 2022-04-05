package com.ecnu.adsmls.components.modal;

import com.alibaba.fastjson.JSON;
import com.ecnu.adsmls.components.ChooseFileButton;
import com.ecnu.adsmls.model.MConfig;
import com.ecnu.adsmls.router.params.Global;
import com.ecnu.adsmls.utils.FileSystem;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// TODO 暂时使用 Modal
public class SettingsModal extends Modal {
    private MConfig mConfig;
    private String pythonInterpreter;

    private Node btPythonInterpreter;

    public SettingsModal(MConfig mConfig) {
        super();
        this.mConfig = mConfig;
        this.loadData();
    }

    private void loadData() {
        this.pythonInterpreter = this.mConfig.getPythonEnv();
    }

    protected void createWindow() {
        super.createWindow();
        this.window.setTitle("Settings");

        Label lbPythonInterpreter = new Label("Python Interpreter");
        // 限定选择 *.exe 文件
        Map<String, String> exeFilter = new HashMap<>();
        exeFilter.put(FileSystem.getRegSuffix(FileSystem.Suffix.EXE), FileSystem.Suffix.EXE.toString());
        this.btPythonInterpreter = new ChooseFileButton(this.gridPane, exeFilter).getNode();
        if(this.pythonInterpreter != null) {
            ((ChooseFileButton) this.btPythonInterpreter.getUserData()).setFile(new File(this.pythonInterpreter));
        }

        this.slot.addRow(0, lbPythonInterpreter, this.btPythonInterpreter);
    }

    @Override
    protected void update() {
        try {
            this.pythonInterpreter = ((ChooseFileButton) this.btPythonInterpreter.getUserData()).getFile().getAbsolutePath();
        }
        catch (Exception ignored) {}
    }

    @Override
    protected void check() {
        if(this.pythonInterpreter == null) {
            this.valid = false;
        }
        // TODO 如何判断选择的 exe 是 python?
    }

    @Override
    protected void then() {
        Global.pythonEnv = this.pythonInterpreter;
    }
}
