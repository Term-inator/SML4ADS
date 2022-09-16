package com.ecnu.adsmls.components.selectpane;

import com.ecnu.adsmls.components.choosebutton.impl.ChooseFileButton;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class CustomDefault extends SelectPane {
    public enum Mode {
        CUSTOM("custom"), DEFAULT("default");

        public String value;

        Mode(String value) {
            this.value = value;
        }
    }

    private GridPane defaultPane;
    private ComboBox<String> cbDefaultChoices;

    private GridPane customPane;
    private ChooseFileButton btCustomFile;

    public CustomDefault(List<String> defaultChoices, Pane rootLayout, String initDir, Map<String, String> fileFilter) {
        super();
        this.initDefaultPane(defaultChoices);
        this.initCustomPane(rootLayout, initDir, fileFilter);
        Map<String, GridPane> panes = new LinkedHashMap<>();
        panes.put(Mode.DEFAULT.value, this.defaultPane);
        panes.put(Mode.CUSTOM.value, this.customPane);
        this.setPanes(panes);
    }

    private void initDefaultPane(List<String> defaultChoices) {
        this.defaultPane = new GridPane();
        this.cbDefaultChoices = new ComboBox<>(FXCollections.observableArrayList(defaultChoices));
        this.defaultPane.addRow(0, this.cbDefaultChoices);
    }

    private void initCustomPane(Pane rootLayout, String initDir, Map<String, String> fileFilter) {
        this.customPane = new GridPane();
        this.btCustomFile = new ChooseFileButton(rootLayout, initDir);
        this.btCustomFile.setFileFilter(fileFilter);
        this.customPane.addRow(0, this.btCustomFile.getNode());
    }
}
