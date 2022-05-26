package com.ecnu.adsmls.components.modal;

import com.ecnu.adsmls.model.MConfig;
import com.ecnu.adsmls.router.params.Global;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

// TODO 暂时使用 Modal
public class SettingsModal extends Modal {
    private MConfig mConfig;
    private String simulationPort;

    private TextField tfSimulationPort;

    public SettingsModal(MConfig mConfig) {
        super();
        this.mConfig = mConfig;
        this.loadData();
    }

    private void loadData() {
        this.simulationPort = String.valueOf(this.mConfig.getSimulationPort());
    }

    protected void createWindow() {
        super.createWindow();
        this.window.setTitle("Settings");

        Label lbSimulationPort = new Label("simulation port");
        this.tfSimulationPort = new TextField(this.simulationPort);

        this.slot.addRow(0, lbSimulationPort, this.tfSimulationPort);
    }

    @Override
    protected void update() {
        try {
            this.simulationPort = this.tfSimulationPort.getText();
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void check() {
        try {
            Integer.parseInt(this.simulationPort);
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void then() {
        Global.simulationPort = Integer.valueOf(this.simulationPort);
    }
}
