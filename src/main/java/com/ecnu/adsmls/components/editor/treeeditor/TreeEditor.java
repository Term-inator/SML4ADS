package com.ecnu.adsmls.components.editor.treeeditor;

import com.alibaba.fastjson.JSON;
import com.ecnu.adsmls.components.editor.Editor;
import com.ecnu.adsmls.components.editor.treeeditor.impl.*;
import com.ecnu.adsmls.model.*;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class TreeEditor extends Editor {
    private SplitPane splitPane = new SplitPane();

    private AnchorPane paletteWrapper;
    private AnchorPane canvasWrapper;

    // 组件栏
    private GridPane palette = new GridPane();
    final ToggleGroup group = new ToggleGroup();
    private String componentSelected;
    private Pane canvas = new Pane();

    private long componentId = 0;
    // 被选中的组件
    private Group componentChose;

    public TreeEditor(String projectPath, File file) {
        super(projectPath, file);
        this.createNode();
    }

    // TODO 移到外面
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

    /**
     * 选中组件
     * @param component
     */
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
        this.paletteWrapper = new AnchorPane(this.palette);

        this.palette.setPadding(new Insets(8, 0, 0, 0));
        this.palette.setVgap(8);
        this.palette.setAlignment(Pos.TOP_CENTER);
        this.palette.setPrefWidth(100);

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

    // TODO 选中置顶
    public void initCanvas() {
        this.canvasWrapper = new AnchorPane(this.canvas);

        this.canvas.setPrefWidth(1200);
        this.canvas.setPrefHeight(800);

        canvasWrapper.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            System.out.println(e);
            // Delete 删除事件
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
            // Ctrl + S 保存
            else if(e.isControlDown() && e.getCode() == KeyCode.S) {
                this.save();
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

                            this.transitionBindClick(lambdaContext.node);
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

                        this.behaviorBindClick(lambdaContext.node);

                    } else if (Objects.equals(componentSelected, "BranchPoint")) {
                        Position position = new Position(event.getX(), event.getY());
                        BranchPoint branchPoint = new BranchPoint(this.componentId++, position);
                        branchPoint.updateNode();
                        lambdaContext.node = branchPoint.getNode();
                        lambdaContext.node.setUserData(branchPoint);

                        this.branchPointBindClick(lambdaContext.node);
                    }
                }

                if(lambdaContext.node != null) {
                    canvas.getChildren().add(lambdaContext.node);
                    // BranchPoint 不创建 TreeText
                    if(!(lambdaContext.node.getUserData() instanceof BranchPoint)) {
                        ((TreeComponent) lambdaContext.node.getUserData()).initTreeText();
                        canvas.getChildren().add(((TreeComponent) lambdaContext.node.getUserData()).getTreeText().getNode());
                    }
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

    private void behaviorBindClick(Node behaviorNode) {
        behaviorNode.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            int clickCount = e.getClickCount();
            System.out.println("choose behavior");
            this.chooseComponent((Group) behaviorNode);
            System.out.println(((Behavior) this.componentChose.getUserData()).getPosition());
            if(clickCount == 2) {
                System.out.println("set behavior");

                BehaviorModal bm = new BehaviorModal((Behavior) behaviorNode.getUserData());
                bm.getWindow().showAndWait();

                if(!bm.isConfirm()) {
                    return;
                }
                ((Behavior) behaviorNode.getUserData()).setName(bm.getBehaviorName());
                ((Behavior) behaviorNode.getUserData()).setParams(bm.getParamsValue());
            }
        });
    }

    private void branchPointBindClick(Node branchPointNode) {
        branchPointNode.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            System.out.println("choose branch point");
            this.chooseComponent((Group) branchPointNode);
        });
    }

    private void transitionBindClick(Node transitionNode) {
        transitionNode.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            int clickCount = e.getClickCount();
            System.out.println("choose transition");
            this.chooseComponent((Group) transitionNode);
            if(clickCount == 2) {
                // 保证 Transition 已经完成
                if(!((TreeLink) transitionNode.getUserData()).isFinish()) {
                    return;
                }
                if(transitionNode.getUserData() instanceof CommonTransition) {
                    System.out.println("set common transition");

                    CommonTransitionModal ctm = new CommonTransitionModal((CommonTransition) transitionNode.getUserData());
                    ctm.getWindow().showAndWait();

                    if(!ctm.isConfirm()) {
                        return;
                    }
                    ((CommonTransition) transitionNode.getUserData()).setGuards(ctm.getGuards());
                }
                else if(transitionNode.getUserData() instanceof ProbabilityTransition) {
                    System.out.println("set probability transition");

                    ProbabilityTransitionModal ptm = new ProbabilityTransitionModal((ProbabilityTransition) transitionNode.getUserData());
                    ptm.getWindow().showAndWait();

                    if(!ptm.isConfirm()) {
                        return;
                    }
                    ((ProbabilityTransition) transitionNode.getUserData()).setWeight(ptm.getWeight());
                }
            }
        });
    }

    @Override
    public void save(){
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
        System.out.println(tree);
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(this.projectPath, this.relativePath),false), StandardCharsets.UTF_8));
            bw.write(tree);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        // 算出当前最大 id
        long componentId = 0;
        String tree = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(this.projectPath, this.relativePath)), StandardCharsets.UTF_8));
            tree = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MTree mTree = JSON.parseObject(tree, MTree.class);
        if(mTree == null) {
            return;
        }
        System.out.println(tree);

        List<TreeArea> treeAreaList = new ArrayList<>();
        for(MBehavior mBehavior : mTree.getBehaviors()) {
            Behavior behavior = Converter.cast(mBehavior);

            componentId = Math.max(componentId, behavior.getId());

            treeAreaList.add(behavior);
            Node node = behavior.getNode();
            node.setUserData(behavior);
            this.behaviorBindClick(node);
            this.canvas.getChildren().addAll(node, behavior.getTreeText().getNode());
        }
        for(MBranchPoint mBranchPoint : mTree.getBranchPoints()) {
            BranchPoint branchPoint = Converter.cast(mBranchPoint);

            componentId = Math.max(componentId, branchPoint.getId());

            treeAreaList.add(branchPoint);
            Node node = branchPoint.getNode();
            node.setUserData(branchPoint);
            this.branchPointBindClick(node);
            this.canvas.getChildren().add(node);
        }
        for(MCommonTransition mCommonTransition : mTree.getCommonTransitions()) {
            CommonTransition commonTransition = Converter.cast(treeAreaList, mCommonTransition);

            componentId = Math.max(componentId, commonTransition.getId());

            Node node = commonTransition.getNode();
            node.setUserData(commonTransition);
            this.transitionBindClick(node);
            this.canvas.getChildren().addAll(node, commonTransition.getTreeText().getNode());
        }
        for(MProbabilityTransition mProbabilityTransition : mTree.getProbabilityTransitions()) {
            ProbabilityTransition probabilityTransition = Converter.cast(treeAreaList, mProbabilityTransition);

            componentId = Math.max(componentId, probabilityTransition.getId());

            Node node = probabilityTransition.getNode();
            node.setUserData(probabilityTransition);
            this.transitionBindClick(node);
            this.canvas.getChildren().addAll(node, probabilityTransition.getTreeText().getNode());
        }

        this.componentId = componentId + 1;
    }

    @Override
    protected void createNode() {
        this.initBehavior();
        this.initPalette();
        this.initCanvas();

        this.splitPane.getItems().addAll(this.paletteWrapper, this.canvasWrapper);
        this.splitPane.setDividerPositions(.1f);
    }

    public Node getNode() {
        return this.splitPane;
    }
}
