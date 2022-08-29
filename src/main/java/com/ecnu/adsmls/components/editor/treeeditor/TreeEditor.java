package com.ecnu.adsmls.components.editor.treeeditor;

import com.alibaba.fastjson.JSON;
import com.ecnu.adsmls.components.editor.Editor;
import com.ecnu.adsmls.components.editor.treeeditor.impl.*;
import com.ecnu.adsmls.model.*;
import com.ecnu.adsmls.utils.Converter;
import com.ecnu.adsmls.utils.FileSystem;
import com.ecnu.adsmls.utils.Position;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class TreeEditor extends Editor {
    private SplitPane splitPane = new SplitPane();

    private AnchorPane paletteWrapper;
    private AnchorPane canvasWrapper;
    private ScrollPane scrollPane;
    private double defaultCanvasWidth = 600;
    private double defaultCanvasHeight = 400;

    // 组件栏
    private GridPane palette = new GridPane();
    private ComboBox<String> cbTreeType;
    final ToggleGroup group = new ToggleGroup();
    private String componentSelected;
    private Pane canvas = new Pane();

    private long componentId = 0;
    // 被选中的组件
    private Group componentChose;

    // 错误信息
    private StringBuilder errorMsg = new StringBuilder();

    public TreeEditor(String projectPath, File file) {
        super(projectPath, file);
        this.createNode();
    }

    /**
     * 选中组件
     *
     * @param component 选中的那个 component
     */
    private void chooseComponent(Group component) {
        if (componentChose != null) {
            ((TreeComponent) this.componentChose.getUserData()).unselect();
            ((TreeComponent) this.componentChose.getUserData()).inactive();
        }
        this.componentChose = component;
        ((TreeComponent) this.componentChose.getUserData()).select();
        ((TreeComponent) this.componentChose.getUserData()).active();
        this.raise(this.componentChose);
    }

    /**
     * 使 component 浮于顶层
     *
     * @param component 需要浮于顶层的那个 component
     */
    private void raise(Group component) {
        this.canvas.getChildren().remove(component);
        this.canvas.getChildren().add(component);
    }

    private void initPalette() {
        this.paletteWrapper = new AnchorPane(this.palette);

        this.palette.setPadding(new Insets(8, 8, 0, 8));
        this.palette.setVgap(8);
        this.palette.setAlignment(Pos.TOP_CENTER);

        // TODO 什么选择 BehaviorModal 对应显示什么行为
        // TODO 切换选择提示将会清空画布
        // TODO preprocess 检查 tree 类型
        String[] treeTypes = {"Vehicle", "Pedestrian", "Obstacle"};
        this.cbTreeType = new ComboBox<>(FXCollections.observableArrayList(treeTypes));
        this.cbTreeType.getSelectionModel().select(0);

        ToggleButton tb0 = new ToggleButton("Behavior");
        tb0.setUserData("Behavior");
        ToggleButton tb1 = new ToggleButton("BranchPoint");
        tb1.setUserData("BranchPoint");
        ToggleButton tb2 = new ToggleButton("Transition");
        tb2.setUserData("Transition");

        palette.addRow(0, this.cbTreeType);
        palette.addRow(1, tb0);
        palette.addRow(2, tb1);
        palette.addRow(3, tb2);
        for (Node n : palette.getChildren()) {
            if (n instanceof ToggleButton) {
                ((ToggleButton) n).setToggleGroup(group);
            }
        }

        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                componentSelected = null;
            } else {
                componentSelected = (String) newValue.getUserData();
            }
        });
    }

    public void initCanvas() {
        this.canvasWrapper = new AnchorPane(this.canvas);

        this.canvas.setMinSize(this.defaultCanvasWidth, this.defaultCanvasHeight);
        this.canvas.setPrefWidth(Control.USE_COMPUTED_SIZE);
        this.canvas.setPrefHeight(Control.USE_COMPUTED_SIZE);
        this.canvas.setBackground(new Background(new BackgroundFill(new Color(1, 1, 1, 1), null, null)));

        AnchorPane.setTopAnchor(this.canvas, 0d);
        AnchorPane.setRightAnchor(this.canvas, 0d);
        AnchorPane.setBottomAnchor(this.canvas, 0d);
        AnchorPane.setLeftAnchor(this.canvas, 0d);

        canvasWrapper.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            System.out.println(e);
            // Delete 删除事件
            if (e.getCode() == KeyCode.DELETE) {
                if (this.componentChose == null) {
                    return;
                }
                System.out.println("delete");
                // 递归删除
                List<Node> nodes = ((TreeComponent) this.componentChose.getUserData()).remove();
                this.canvas.getChildren().removeAll(nodes);
                this.componentChose = null;
            }
            // Ctrl + S 保存
            else if (e.isControlDown() && e.getCode() == KeyCode.S) {
                this.save();
            }
        });

        // 调整画布大小
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            this.canvas.setPrefWidth(Control.USE_COMPUTED_SIZE);
            this.canvas.setPrefHeight(Control.USE_COMPUTED_SIZE);
        });

        AtomicReference<Transition> transition = new AtomicReference<>();

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            System.out.println("click: " + event.getTarget().toString());
            // 点击 canvas 就激活键盘事件
            canvasWrapper.requestFocus();
            // 点击空白区域
            if (event.getTarget() instanceof Pane) {
                System.out.println("click nothing");
                if (this.componentChose != null) {
                    System.out.println("inactive");
                    ((TreeComponent) this.componentChose.getUserData()).unselect();
                    ((TreeComponent) this.componentChose.getUserData()).inactive();
                }
                this.componentChose = null;
            }
            // 未选择画板上的组件
            if (componentSelected == null) {
                return;
            } else {
                var lambdaContext = new Object() {
                    Node node = null;
                };
                if (Objects.equals(componentSelected, "Transition")) {
                    System.out.println(this.componentChose);
                    if (this.componentChose != null) {
                        // 未实例化 || 未选起点
                        if (transition.get() == null || transition.get().getSource() == null) {
                            // 必须实现 Linkable
                            TreeArea source;
                            try {
                                source = (TreeArea) this.componentChose.getUserData();
                            } catch (ClassCastException e) {
                                System.out.println("not linkable");
                                return;
                            }
                            if (source instanceof Behavior) {
                                transition.set(new CommonTransition(this.componentId++));
                            } else if (source instanceof BranchPoint) {
                                transition.set(new ProbabilityTransition(this.componentId++));
                            }
                            transition.get().setSource(source);
                            // 创建时压入 Pane 中即可
                            transition.get().updateNode();
                            lambdaContext.node = transition.get().getNode();
                            lambdaContext.node.setUserData(transition.get());

                            this.transitionBindClick(lambdaContext.node);
                        } else {
                            TreeArea target;
                            try {
                                target = (TreeArea) this.componentChose.getUserData();
                            } catch (ClassCastException e) {
                                System.out.println("not linkable");
                                return;
                            }
                            boolean success = transition.get().setTarget(target);
                            if (success) {
                                transition.get().finish();
                            }
                        }
                    } else {
                        // 未实例化 或 未选起点
                        if (transition.get() == null || transition.get().getSource() == null) {
                            return;
                        }
                        System.out.println("linking");
                        transition.get().getLinkPoints().add(new TreeLinkPoint(new Position(event.getX(), event.getY()), transition.get()));
                    }
                    transition.get().updateNode();
                } else {
                    // 已经创建了 Transition
                    if (transition.get() != null) {
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

                if (lambdaContext.node != null) {
                    canvas.getChildren().add(lambdaContext.node);
                    // BranchPoint 不创建 TreeText
                    if (!(lambdaContext.node.getUserData() instanceof BranchPoint)) {
                        ((TreeComponent) lambdaContext.node.getUserData()).initTreeText();
                        canvas.getChildren().add(((TreeComponent) lambdaContext.node.getUserData()).getTreeText().getNode());
                    }
                    this.chooseComponent((Group) lambdaContext.node);
                } else {
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
            if (clickCount == 2) {
                System.out.println("set behavior");

                BehaviorModal bm = new BehaviorModal((Behavior) behaviorNode.getUserData());
                bm.getWindow().showAndWait();

                if (!bm.isConfirm()) {
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
            if (clickCount == 2) {
                // 保证 Transition 已经完成
                if (!((TreeLink) transitionNode.getUserData()).isFinish()) {
                    return;
                }
                if (transitionNode.getUserData() instanceof CommonTransition) {
                    System.out.println("set common transition");

                    CommonTransitionModal ctm = new CommonTransitionModal((CommonTransition) transitionNode.getUserData());
                    ctm.getWindow().showAndWait();

                    if (!ctm.isConfirm()) {
                        return;
                    }
                    ((CommonTransition) transitionNode.getUserData()).setGuard(ctm.getGuard());
                } else if (transitionNode.getUserData() instanceof ProbabilityTransition) {
                    System.out.println("set probability transition");

                    ProbabilityTransitionModal ptm = new ProbabilityTransitionModal((ProbabilityTransition) transitionNode.getUserData());
                    ptm.getWindow().showAndWait();

                    if (!ptm.isConfirm()) {
                        return;
                    }
                    ((ProbabilityTransition) transitionNode.getUserData()).setWeight(ptm.getWeight());
                }
            }
        });
    }

    /**
     * 判断图是否有环
     *
     * @param root 树根
     * @return
     */
    private boolean noRing(TreeArea root) {
        Stack<TreeArea> s = new Stack<>();
        s.push(root);
        Map<Long, Boolean> mark = new HashMap<>();
        for (Node node : this.canvas.getChildren()) {
            if (node.getUserData() instanceof TreeArea) {
                TreeArea treeArea = (TreeArea) node.getUserData();
                mark.put(treeArea.getId(), false);
            }
        }
        while (!s.isEmpty()) {
            TreeArea treeArea = s.pop();
            // 若已访问则有环
            if (mark.get(treeArea.getId()) == true) {
                this.errorMsg.append("The tree has at least one ring, in file ").append(this.file.getName()).append("\n");
                return false;
            }
            mark.put(treeArea.getId(), true);

            for (TreeLink treeLink : treeArea.getOutTransitions()) {
                s.push(treeLink.getTarget());
            }
        }
        return true;
    }

    /**
     * 获取树的根节点
     *
     * @return
     */
    private TreeArea getRoot() {
        Stack<TreeArea> s = new Stack<>();
        for (Node node : this.canvas.getChildren()) {
            if (node.getUserData() instanceof TreeArea) {
                TreeArea treeArea = (TreeArea) node.getUserData();
                // 入度为 0
                if (treeArea.getInTransitions().isEmpty()) {
                    s.push(treeArea);
                }
            }
        }
        System.out.println(s);
        if (s.isEmpty()) {
            this.errorMsg.append("The tree has at least one ring or does not have a root, in file ")
                    .append(this.file.getName()).append("\n");
            return null;
        }
        // 若图入度为 0 的节点多于一个，则一定不是树
        else if (s.size() != 1) {
            this.errorMsg.append("The tree has more than one root, in file ").append(this.file.getName()).append(".\n");
            return null;
        } else {
            TreeArea root = s.pop();
            if (this.noRing(root)) {
                return root;
            } else {
                return null;
            }
        }
    }

    @Override
    public void check() {
        for (Node node : this.canvas.getChildren()) {
            if (node.getUserData() instanceof BranchPoint) {
                BranchPoint branchPoint = (BranchPoint) node.getUserData();
                if (branchPoint.getInTransitions().isEmpty()) {
                    this.errorMsg.append("The BranchPoint in file ").append(this.file.getName()).append(" has no parent node.\n");
                } else if (branchPoint.getOutTransitions().isEmpty()) {
                    this.errorMsg.append("The BranchPoint in file ").append(this.file.getName()).append(" should have at least one child node.\n");
                }
            }
        }
    }

    @Override
    public void save() {
        this.errorMsg = new StringBuilder();
        this.check();

        MTree mTree = new MTree();
        TreeArea root = this.getRoot();
        if (root == null) {
            mTree.setRootId(-1);
        } else {
            mTree.setRootId(root.getId());
        }
        mTree.setErrMsg(this.errorMsg.toString());
        List<Node> nodes = this.canvas.getChildren();
        for (Node node : nodes) {
            Component component = (Component) node.getUserData();
            if (component instanceof Behavior) {
                Behavior behavior = (Behavior) component;
                mTree.getBehaviors().add(Converter.cast(behavior));
            } else if (component instanceof BranchPoint) {
                BranchPoint branchPoint = (BranchPoint) component;
                mTree.getBranchPoints().add(Converter.cast(branchPoint));
            } else if (component instanceof CommonTransition) {
                CommonTransition commonTransition = (CommonTransition) component;
                mTree.getCommonTransitions().add(Converter.cast(commonTransition));
            } else if (component instanceof ProbabilityTransition) {
                ProbabilityTransition probabilityTransition = (ProbabilityTransition) component;
                mTree.getProbabilityTransitions().add(Converter.cast(probabilityTransition));
            }
        }
        String tree = JSON.toJSONString(mTree);
        System.out.println(tree);
        FileSystem.JSONWriter(new File(this.projectPath, this.relativePath), tree);
    }

    @Override
    public void load() {
        // 算出当前最大 id
        long componentId = 0;
        String tree = FileSystem.JSONReader(new File(this.projectPath, this.relativePath));

        MTree mTree = JSON.parseObject(tree, MTree.class);
        if (mTree == null) {
            return;
        }
        System.out.println(tree);

        List<TreeArea> treeAreaList = new ArrayList<>();
        for (MBehavior mBehavior : mTree.getBehaviors()) {
            Behavior behavior = Converter.cast(mBehavior);

            componentId = Math.max(componentId, behavior.getId());

            treeAreaList.add(behavior);
            Node node = behavior.getNode();
            node.setUserData(behavior);
            this.behaviorBindClick(node);
            this.canvas.getChildren().addAll(node, behavior.getTreeText().getNode());
        }
        for (MBranchPoint mBranchPoint : mTree.getBranchPoints()) {
            BranchPoint branchPoint = Converter.cast(mBranchPoint);

            componentId = Math.max(componentId, branchPoint.getId());

            treeAreaList.add(branchPoint);
            Node node = branchPoint.getNode();
            node.setUserData(branchPoint);
            this.branchPointBindClick(node);
            this.canvas.getChildren().add(node);
        }
        for (MCommonTransition mCommonTransition : mTree.getCommonTransitions()) {
            CommonTransition commonTransition = Converter.cast(treeAreaList, mCommonTransition);

            componentId = Math.max(componentId, commonTransition.getId());

            Node node = commonTransition.getNode();
            node.setUserData(commonTransition);
            this.transitionBindClick(node);
            this.canvas.getChildren().addAll(node, commonTransition.getTreeText().getNode());
        }
        for (MProbabilityTransition mProbabilityTransition : mTree.getProbabilityTransitions()) {
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
        // TODO canvas 一旦变大，无法恢复，导致全屏后再缩小会出问题
        this.initPalette();
        this.initCanvas();

        this.scrollPane = new ScrollPane(this.canvasWrapper);
        AnchorPane scrollWrapper = new AnchorPane(this.scrollPane);
        AnchorPane.setTopAnchor(this.scrollPane, 0d);
        AnchorPane.setRightAnchor(this.scrollPane, 0d);
        AnchorPane.setBottomAnchor(this.scrollPane, 0d);
        AnchorPane.setLeftAnchor(this.scrollPane, 0d);

        this.canvasWrapper.setPrefWidth(this.scrollPane.getPrefWidth());
        this.canvasWrapper.setPrefHeight(this.scrollPane.getPrefHeight());

        this.splitPane.getItems().addAll(this.paletteWrapper, scrollWrapper);
        this.paletteWrapper.maxWidthProperty().bind(this.splitPane.maxWidthProperty().multiply(0.1d));
    }

    @Override
    public Node getNode() {
        return this.splitPane;
    }
}
