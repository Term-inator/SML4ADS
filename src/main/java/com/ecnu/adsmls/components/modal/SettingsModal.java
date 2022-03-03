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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

// TODO 暂时使用 Modal
public class SettingsModal extends Modal {
    private String pythonInterpreter;
    private String simulatorType;
    private String simulatorPath;

    private Node btPythonInterpreter;
    private ComboBox<String> cbSimulatorType;
    private Node btSimulatorPath;

    protected void createWindow() {
        super.createWindow();

        Label lbPythonInterpreter = new Label("Python Interpreter");
        // 限定选择 *.exe 文件
        Map<String, String> exeFilter = new HashMap<>();
        exeFilter.put("*" + FileSystem.Suffix.EXE.value, "EXE");
        this.btPythonInterpreter = new ChooseFileButton(this.gridPane, exeFilter).getNode();

        Label lbSimulatorType = new Label("Simulator Type");
        String[] simulators = {"Carla"};
        this.cbSimulatorType = new ComboBox<>(FXCollections.observableArrayList(simulators));

        Label lbSimulatorPath = new Label("Simulator Path");
        // 限定选择 *.exe 文件
        this.btSimulatorPath = new ChooseFileButton(this.gridPane, exeFilter).getNode();

        this.slot.addRow(0, lbPythonInterpreter, this.btPythonInterpreter);
        this.slot.addRow(1, lbSimulatorType, this.cbSimulatorType);
        this.slot.addRow(2, lbSimulatorPath, this.btSimulatorPath);
    }

    @Override
    protected void update() {
        try {
            this.pythonInterpreter = ((ChooseFileButton) this.btPythonInterpreter.getUserData()).getFile().getAbsolutePath();
        }
        catch (Exception ignored) {}
        this.simulatorType = this.cbSimulatorType.getValue();
        try {
            this.simulatorPath = ((ChooseFileButton) this.btSimulatorPath.getUserData()).getFile().getAbsolutePath();
        }
        catch (Exception ignored) {}
    }

    @Override
    protected void check() {
        if(this.pythonInterpreter == null || this.simulatorType == null || this.simulatorPath == null) {
            this.valid = false;
        }
        // TODO 如何判断选择的 exe 是 python 和 carla?
    }

    @Override
    protected void then() {
        Global.pythonEnv = this.pythonInterpreter;
        Global.simulatorType = this.simulatorType;
        Global.simulatorPath = this.simulatorPath;
    }
}
