package com.ecnu.adsmls.utils.register;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;


public class Input {
    private Function.DataType dataType;

    private Node inputNode;

    public Input(Function.DataType dataType) {
        this.dataType = dataType;
        this.createNode();
    }

    private void createNode() {
        switch (this.dataType) {
            case INT:
            case DOUBLE:
            case STRING:
                this.inputNode = new TextField();
                break;
            case BOOL:
                this.inputNode = new ComboBox<String>(FXCollections.observableArrayList("true", "false"));
                break;
        }
        this.inputNode.setUserData(this);
    }

    public String getValue() {
        if (this.inputNode instanceof TextInputControl) {
            return ((TextInputControl) this.inputNode).getText();
        }
        else if (this.inputNode instanceof ComboBox) {
            return ((ComboBox<String>) this.inputNode).getValue();
        }
        else {
            return null;
        }
    }

    public Node getNode() {
        return this.inputNode;
    }
}
