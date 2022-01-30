package com.ecnu.adsmls.components.editor;

import com.ecnu.adsmls.utils.Position;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class TreeEditor {
    private GridPane palette;
    final ToggleGroup group = new ToggleGroup();
    private String componentSelected;
    private Pane canvas;

    private long componentId = 0;
    private Group componentChose;

    public TreeEditor() {
        palette = new GridPane();
        palette.setPadding(new Insets(8, 0, 0, 0));
        palette.setVgap(8);
        palette.setAlignment(Pos.TOP_CENTER);
        palette.setPrefWidth(100);
        canvas = new Pane();
        canvas.setPrefWidth(1200);
        canvas.setPrefHeight(800);
        initPalette();
        initCanvas();
    }

    private void initPalette() {
        ToggleButton tb0 = new ToggleButton("Behavior");
        tb0.setUserData("Behavior");
        ToggleButton tb1 = new ToggleButton("BranchPoint");
        tb1.setUserData("BranchPoint");
        ToggleButton tb2 = new ToggleButton("Transition");
        tb2.setUserData("Transition");

        palette.addRow(0, tb0);
        palette.addRow(1, tb1);
        palette.addRow(2, tb2);
        for(Node n : palette.getChildren()) {
            if(n instanceof ToggleButton) {
                ((ToggleButton) n).setToggleGroup(group);
            }
        }

        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if(newValue == null) {
                    componentSelected = null;
                }
                else {
                    componentSelected = (String) newValue.getUserData();
                }
            }
        });
    }

    public void initCanvas() {
        AtomicReference<Transition> transition = new AtomicReference<>(new Transition(this.componentId++));
        AtomicReference<Boolean> linkFinish = new AtomicReference<>(false);
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(event.getTarget() instanceof Pane) {
                this.componentChose = null;
            }
            if(componentSelected == null) {
                return;
            }
            else {
                var lambdaContext = new Object() {
                    Node node = null;
                };
                if(Objects.equals(componentSelected, "Behavior")) {
                    Position position = new Position(event.getX(), event.getY());
                    Behavior behavior = new Behavior(this.componentId++, position);
                    lambdaContext.node = behavior.getNode();
                    lambdaContext.node.setUserData(behavior);
                    lambdaContext.node.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                        System.out.println("choose");
                        this.componentChose = (Group) lambdaContext.node;
                    });
                }
                else if(Objects.equals(componentSelected, "Transition")) {
                    System.out.println(this.componentChose);
                    if(this.componentChose != null) {
                        if(transition.get().getSource() == null) {
                            Area source = (Area) this.componentChose.getUserData();
                            transition.get().setSource(source);
                            transition.get().getPositions().add(source.getCenterPoint());
                        }
                        else {
                            Area target = (Area) this.componentChose.getUserData();
                            transition.get().setTarget(target);
                            transition.get().getPositions().add(target.getCenterPoint());
                            linkFinish.set(true);
                        }
                    }
                    else {
                        if(transition.get().getSource() == null) {
                            return;
                        }
                        System.out.println("linking");
                        transition.get().getPositions().add(new Position(event.getX(), event.getY()));
                    }
                    System.out.println(transition.get().getId());
                    lambdaContext.node = transition.get().getNode();
                    lambdaContext.node.setUserData(transition);
                }
                // TODO
                if(lambdaContext.node != null) {
                    try {
                        canvas.getChildren().remove(lambdaContext.node);
                    }
                    catch (Exception e) {

                    }
                    canvas.getChildren().add(lambdaContext.node);
                }
                if(linkFinish.get()) {
                    transition.set(new Transition(this.componentId++));
                    linkFinish.set(true);
                }
            }
        });
    }

    public void setComponentChose(Group componentChose) {
        this.componentChose = componentChose;
    }

    public Node getNode() {
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(palette, canvas);
        splitPane.setDividerPositions(.1f);
//        splitPane.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 255), null, null)));
        return splitPane;
    }
}
