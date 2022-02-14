package com.ecnu.adsmls.components.treeeditor;

import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;


public abstract class Component {
    protected Group graphicNode;
    protected Shape shape;

    // 是否被选中
    private boolean selected = false;

    public Component() {
        this.graphicNode = new Group();

        // 提示用户该结点可点击
        this.graphicNode.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            this.graphicNode.setCursor(Cursor.HAND);
            this.active();
        });
        this.graphicNode.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            this.graphicNode.setCursor(Cursor.DEFAULT);
            if(this.selected) {
                System.out.println("selected");
                return;
            }
            this.inactive();
        });
    }

    public boolean isSelected() {
        return selected;
    }

    public void select() {
        this.selected = true;
    }

    public void unselect() {
        this.selected = false;
    }

    protected void addNode(Node node) {
        if(!this.graphicNode.getChildren().contains(node)) {
            this.graphicNode.getChildren().add(node);
        }
    }

    protected void addNodes(Node... nodes) {
        for(Node node : nodes) {
            this.addNode(node);
        }
    }

    public Group getNode() {
        return graphicNode;
    }

    public void active() {
        this.shape.setStroke(Color.ORANGE);
    };

    public void inactive() {
        this.shape.setStroke(Color.ROYALBLUE);
    };

    public abstract void createNode();

    public abstract void updateNode();
}
