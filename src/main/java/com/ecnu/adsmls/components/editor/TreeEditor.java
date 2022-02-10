package com.ecnu.adsmls.components.editor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ecnu.adsmls.components.editor.impl.*;
import com.ecnu.adsmls.model.MBehavior;
import com.ecnu.adsmls.model.MPosition;
import com.ecnu.adsmls.model.MTree;
import com.ecnu.adsmls.utils.Converter;
import com.ecnu.adsmls.utils.Position;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class TreeEditor {
    private String directory;
    private String filename;

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

        initBehavior();

        initPalette();
        initCanvas();
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    private void initBehavior() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();

        params.put("duration", "int");
        BehaviorRegister.register("Keep", (LinkedHashMap<String, String>) params.clone());

        params.clear();
        params.put("acceleration", "double");
        params.put("target speed", "double");
        params.put("duration", "int");
        BehaviorRegister.register("Accelerate", (LinkedHashMap<String, String>) params.clone());

        params.clear();
        params.put("acceleration", "double");
        params.put("target speed", "double");
        BehaviorRegister.register("ChangeLeft", (LinkedHashMap<String, String>) params.clone());
        BehaviorRegister.register("ChangeRight", (LinkedHashMap<String, String>) params.clone());
        BehaviorRegister.register("TurnLeft", (LinkedHashMap<String, String>) params.clone());
        BehaviorRegister.register("TurnRight",(LinkedHashMap<String, String>) params.clone());
    }

    private void chooseComponent(Group component) {
        if(componentChose != null) {
            ((TreeComponent) this.componentChose.getUserData()).unselect();
            ((TreeComponent) this.componentChose.getUserData()).inactive();
        }
        this.componentChose = component;
        ((TreeComponent) this.componentChose.getUserData()).select();
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

        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null) {
                componentSelected = null;
            }
            else {
                componentSelected = (String) newValue.getUserData();
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
            else if(e.isControlDown() && e.getCode() == KeyCode.S) {
                this.saveTree();
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
                    ((TreeComponent) this.componentChose.getUserData()).unselect();
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
                                int clickCount = e.getClickCount();
                                System.out.println("choose transition");
                                this.chooseComponent((Group) lambdaContext.node);
                                if(clickCount == 2) {
                                    // 保证 Transition 已经完成
                                    if(!((TreeLink) lambdaContext.node.getUserData()).isFinish()) {
                                        return;
                                    }
                                    if(lambdaContext.node.getUserData() instanceof CommonTransition) {
                                        System.out.println("set common transition");

                                        CommonTransitionModal ctm = new CommonTransitionModal((CommonTransition) lambdaContext.node.getUserData());
                                        ctm.getWindow().showAndWait();

                                        if(!ctm.isConfirm()) {
                                            return;
                                        }
                                        ((CommonTransition) lambdaContext.node.getUserData()).setGuards(ctm.getGuards());
                                        ((CommonTransition) lambdaContext.node.getUserData()).getTreeText().setText(ctm.getCommonTransitionVO());
                                    }
                                    else if(lambdaContext.node.getUserData() instanceof ProbabilityTransition) {
                                        System.out.println("set probability transition");

                                        ProbabilityTransitionModal ptm = new ProbabilityTransitionModal((ProbabilityTransition) lambdaContext.node.getUserData());
                                        ptm.getWindow().showAndWait();

                                        if(!ptm.isConfirm()) {
                                            return;
                                        }
                                        ((ProbabilityTransition) lambdaContext.node.getUserData()).setWeight(ptm.getWeight());
                                        ((ProbabilityTransition) lambdaContext.node.getUserData()).getTreeText().setText(ptm.getProbabilityTransitionVO());
                                    }
                                }
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
                        behavior.updateNode();
                        lambdaContext.node = behavior.getNode();
                        lambdaContext.node.setUserData(behavior);
                        lambdaContext.node.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                            int clickCount = e.getClickCount();
                            System.out.println("choose behavior");
                            this.chooseComponent((Group) lambdaContext.node);
                            System.out.println(((Behavior) this.componentChose.getUserData()).position);
                            if(clickCount == 2) {
                                System.out.println("set behavior");

                                BehaviorModal bm = new BehaviorModal(behavior);
                                bm.getWindow().showAndWait();

                                if(!bm.isConfirm()) {
                                    return;
                                }
                                behavior.setName(bm.getBehaviorName());
                                behavior.setParams(bm.getParamsValue());
                                behavior.getTreeText().setText(bm.getBehaviorVO());
                            }
                        });
                    } else if (Objects.equals(componentSelected, "BranchPoint")) {
                        Position position = new Position(event.getX(), event.getY());
                        BranchPoint branchPoint = new BranchPoint(this.componentId++, position);
                        branchPoint.updateNode();
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
                    ((TreeComponent) lambdaContext.node.getUserData()).initTreeText();
                    canvas.getChildren().add(((TreeComponent) lambdaContext.node.getUserData()).getTreeText().getNode());
                    this.chooseComponent((Group) lambdaContext.node);
                }
                else {
                    if (transition.get().isFinish()) {
                        this.chooseComponent(transition.get().getNode());
                        transition.set(null);
                        System.out.println("finish");
                    }
                }
            }
        });
    }

    public void saveTree(){
        List<Node> nodes = this.canvas.getChildren();
        MTree mTree = new MTree();
        boolean setRoot = false;
        for(Node node : nodes) {
            Component component = (Component) node.getUserData();
            if(component instanceof Behavior) {
                Behavior behavior = (Behavior) component;
                if(!setRoot) {
                    mTree.setRootId(behavior.getId());
                    setRoot = true;
                }
                mTree.getBehaviors().add(Converter.cast(behavior));
            }
            else if(component instanceof BranchPoint) {
                BranchPoint branchPoint = (BranchPoint) component;
                mTree.getBranchPoints().add(Converter.cast(branchPoint));
            }
            else if(component instanceof CommonTransition) {
                CommonTransition commonTransition = (CommonTransition) component;
                mTree.getCommonTransitions().add(Converter.cast(commonTransition));
            }
            else if(component instanceof ProbabilityTransition) {
                ProbabilityTransition probabilityTransition = (ProbabilityTransition) component;
                mTree.getProbabilityTransitions().add(Converter.cast(probabilityTransition));
            }
        }
        String tree = JSON.toJSONString(mTree);
        String path = this.directory + this.filename + ".json";
        System.out.println(tree);
        System.out.println(path);
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path,false), StandardCharsets.UTF_8));
            bw.write(tree);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadTree() {

    }

    public Node getNode() {
        SplitPane splitPane = new SplitPane();

        splitPane.getItems().addAll(paletteWrapper, canvasWrapper);
        splitPane.setDividerPositions(.1f);
//        splitPane.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 255), null, null)));
        return splitPane;
    }
}
