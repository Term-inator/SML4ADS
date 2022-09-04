package com.ecnu.adsmls.components.modal.impl;

import com.ecnu.adsmls.components.modal.Modal;
import com.ecnu.adsmls.model.MConfig;
import com.ecnu.adsmls.router.params.Global;
import com.ecnu.adsmls.utils.SimulatorConstant;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

// TODO 暂时使用 Modal
public class SettingsModal extends Modal {
    private MConfig mConfig;
    private SimulatorConstant.SimulatorType simulatorType;
    private String simulationPort;

    private ComboBox<String> cbSimulatorType;
    private TextField tfSimulationPort;

    public SettingsModal(MConfig mConfig) {
        super();
        this.mConfig = mConfig;
        this.loadData();
    }

    private void loadData() {
        this.simulationPort = String.valueOf(this.mConfig.getSimulationPort());
        this.simulatorType = SimulatorConstant.getSimulatorTypeByValue(this.mConfig.getSimulatorType());
    }

    protected void createWindow() {
        super.createWindow();
        this.setTitle("Settings");

        Label lbSimulatorType = new Label("simulator type");
        String[] simulators = SimulatorConstant.getSimulatorTypeList().toArray(new String[0]);
        this.cbSimulatorType = new ComboBox<>(FXCollections.observableArrayList(simulators));
        this.cbSimulatorType.getSelectionModel().select(this.simulatorType.value);

        Label lbSimulationPort = new Label("simulation port");
        this.tfSimulationPort = new TextField(this.simulationPort);

        this.slot.addRow(0, lbSimulatorType, this.cbSimulatorType);
        this.slot.addRow(1, lbSimulationPort, this.tfSimulationPort);
    }

    @Override
    protected void update() {
        try {
            this.simulatorType = SimulatorConstant.getSimulatorTypeByValue(this.cbSimulatorType.getValue());
            this.simulationPort = this.tfSimulationPort.getText();
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void check() {
        try {
            int port = Integer.parseInt(this.simulationPort);
            if (port < 1 || port > 65535) {
                this.valid = false;
                System.out.println("Port should in range[1, 65535]");
            }
        } catch (Exception e) {
            this.valid = false;
        }
    }

    @Override
    protected void then() {
        Global.simulatorType = this.simulatorType;
        Global.simulationPort = Integer.valueOf(this.simulationPort);
    }
}
