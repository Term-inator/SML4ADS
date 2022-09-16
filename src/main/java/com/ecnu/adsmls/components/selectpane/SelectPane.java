package com.ecnu.adsmls.components.selectpane;


import com.ecnu.adsmls.utils.register.Function;
import com.ecnu.adsmls.utils.register.FunctionRegister;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;

import java.util.*;

/**
 * 多模式组件
 * ComboBox 选择不同选项显示不同的内容
 * 支持从设置的 panes 里选择，或从某一类 function 中选择
 */
public class SelectPane {
    public enum Row {
        ONE, TWO
    }

    private GridPane gridPane = new GridPane();

    private ComboBox<String> cbModes;
    // 页面上显示的内容
    private GridPane displayPane = new GridPane();

    protected String defaultName = "default";

    // 默认一行
    protected Row row = Row.ONE;

    protected Map<String, GridPane> panes;

    protected FunctionRegister.FunctionCategory functionCategory;

    // 当前 pane 的数值
    protected Map<String, String> values = new HashMap<>();


    public SelectPane() {
        this.createNode();
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    public void setRow(Row row) {
        this.row = row;
    }

    public void setPanes(Map<String, GridPane> panes) {
        this.panes = panes;
        this.setChoices();
    }

    public void setFunctionCategory(FunctionRegister.FunctionCategory functionCategory) {
        this.functionCategory = functionCategory;
        this.setChoices();
    }

    private void setChoices() {
        List<String> choices = new ArrayList<>(this.panes.keySet());
        choices.addAll(FunctionRegister.getFunctionNames(this.functionCategory));
        this.cbModes.setItems(FXCollections.observableArrayList(choices));
    }

    private void createNode() {
        this.gridPane.setHgap(5);

        this.cbModes = new ComboBox<>();
        this.cbModes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // 可以在 panes 里找到
            GridPane tmp = this.panes.get(newValue);
            if (tmp != null) {
                this.displayPane = tmp;
            }
            else {
                this.displayPane.getChildren().clear();
                // 可以在 functions 里找到
                Function function = FunctionRegister.getFunction(this.functionCategory, newValue);
                if (function != null) {
                    function.render(this.displayPane);
                }
            }
        });

        switch (this.row) {
            case ONE: {
                this.gridPane.addRow(1, this.cbModes, this.displayPane);
            }
            case TWO: {
                this.gridPane.addRow(1, this.cbModes);
                this.gridPane.addRow(2, this.displayPane);
            }
        }
    }

    public String getMode() {
        return this.cbModes.getValue();
    }

    public Node getNode() {
        return this.gridPane;
    }
}
