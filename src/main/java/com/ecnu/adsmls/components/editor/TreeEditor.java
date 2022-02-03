package com.ecnu.adsmls.components.editor;

import com.ecnu.adsmls.components.editor.impl.*;
import com.ecnu.adsmls.utils.Position;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class TreeEditor {
    private AnchorPane paletteWrapper;
    private AnchorPane canvasWrapper;

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

        paletteWrapper = new AnchorPane(palette);
        canvasWrapper = new AnchorPane(canvas);

        initPalette();
        initCanvas();
    }

    private void chooseComponent(Group component) {
        if(componentChose != null) {
            ((TreeComponent) this.componentChose.getUserData()).inactive();
        }
        this.componentChose = component;
        ((TreeComponent) this.componentChose.getUserData()).active();
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
        canvasWrapper.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            System.out.println(e);
            if(e.getCode() == KeyCode.DELETE) {
                if(this.componentChose == null) {
                    return;
                }
                System.out.println("delete");
                // 递归删除
                List<Node> nodes = ((TreeComponent) this.componentChose.getUserData()).remove();
                this.canvas.getChildren().removeAll(nodes);
                this.componentChose = null;
            }
        });

        AtomicReference<Transition> transition = new AtomicReference<>();

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            System.out.println("click: " + event.getTarget().toString());
            // 点击 canvas 就激活键盘事件
            canvasWrapper.requestFocus();
            // 点击空白区域
            if(event.getTarget() instanceof Pane) {
                System.out.println("click nothing");
                if(this.componentChose != null) {
                    System.out.println("inactive");
                    ((TreeComponent) this.componentChose.getUserData()).inactive();
                }
                this.componentChose = null;
            }
            // 未选择画板上的组件
            if(componentSelected == null) {
                return;
            }
            else {
                var lambdaContext = new Object() {
                    Node node = null;
                };
                if(Objects.equals(componentSelected, "Transition")) {
                    System.out.println(this.componentChose);
                    if(this.componentChose != null) {
                        // 未实例化 || 未选起点
                        if(transition.get() == null || transition.get().getSource() == null) {
                            // 必须实现 Linkable
                            TreeArea source;
                            try {
                                source = (TreeArea) this.componentChose.getUserData();
                            }
                            catch (ClassCastException e) {
                                System.out.println("not linkable");
                                return;
                            }
                            if(source instanceof Behavior) {
                                transition.set(new CommonTransition(this.componentId++));
                            }
                            else if(source instanceof BranchPoint) {
                                transition.set(new ProbabilityTransition(this.componentId++));
                            }
                            transition.get().setSource(source);
                            // 创建时压入 Pane 中即可
                            transition.get().updateNode();
                            lambdaContext.node = transition.get().getNode();
                            lambdaContext.node.setUserData(transition.get());
                            lambdaContext.node.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                                System.out.println("choose transition");
                                this.chooseComponent((Group) lambdaContext.node);
                            });
                        }
                        else {
                            TreeArea target;
                            try {
                                target = (TreeArea) this.componentChose.getUserData();
                            }
                            catch (ClassCastException e) {
                                System.out.println("not linkable");
                                return;
                            }
                            boolean success = transition.get().setTarget(target);
                            if(success) {
                                transition.get().finish();
                            }
                        }
                    }
                    else {
                        // 未实例化 或 未选起点
                        if(transition.get() == null || transition.get().getSource() == null) {
                            return;
                        }
                        System.out.println("linking");
                        transition.get().getLinkPoints().add(new TreeLinkPoint(new Position(event.getX(), event.getY()), transition.get()));
                    }
                    transition.get().updateNode();
                }
                else {
                    // 已经创建了 Transition
                    if(transition.get() != null) {
                        System.out.println("remove unfinished transition");
                        this.canvas.getChildren().remove(transition.get().getNode());
                        transition.get().rollback();
                    }
                    if (Objects.equals(componentSelected, "Behavior")) {
                        Position position = new Position(event.getX(), event.getY());
                        Behavior behavior = new Behavior(this.componentId++, position);
                        lambdaContext.node = behavior.getNode();
                        lambdaContext.node.setUserData(behavior);
                        lambdaContext.node.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                            System.out.println("choose behavior");
                            this.chooseComponent((Group) lambdaContext.node);
                        });
                    } else if (Objects.equals(componentSelected, "BranchPoint")) {
                        Position position = new Position(event.getX(), event.getY());
                        BranchPoint branchPoint = new BranchPoint(this.componentId++, position);
                        lambdaContext.node = branchPoint.getNode();
                        lambdaContext.node.setUserData(branchPoint);
                        lambdaContext.node.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                            System.out.println("choose branch point");
                            this.chooseComponent((Group) lambdaContext.node);
                        });
                    }
                }

                if(lambdaContext.node != null) {
                    canvas.getChildren().add(lambdaContext.node);
                    this.chooseComponent((Group) lambdaContext.node);
                }
                else {
                    if (transition.get().getFinish()) {
                        transition.set(null);
                        System.out.println("finish");
                    }
                }
            }
        });
    }

    public Node getNode() {
        SplitPane splitPane = new SplitPane();

        splitPane.getItems().addAll(paletteWrapper, canvasWrapper);
        splitPane.setDividerPositions(.1f);
//        splitPane.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 255), null, null)));
        return splitPane;
    }
}
