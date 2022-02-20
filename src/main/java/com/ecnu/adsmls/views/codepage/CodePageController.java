package com.ecnu.adsmls.views.codepage;

import com.alibaba.fastjson.JSON;
import com.ecnu.adsmls.components.editor.modeleditor.ModelEditor;
import com.ecnu.adsmls.components.editor.treeeditor.TreeEditor;
import com.ecnu.adsmls.components.modal.NewModelModal;
import com.ecnu.adsmls.components.modal.NewTreeModal;
import com.ecnu.adsmls.components.mutileveldirectory.MultiLevelDirectory;
import com.ecnu.adsmls.model.MCar;
import com.ecnu.adsmls.model.MModel;
import com.ecnu.adsmls.model.MTree;
import com.ecnu.adsmls.router.Route;
import com.ecnu.adsmls.router.Router;
import com.ecnu.adsmls.router.params.CodePageParams;
import com.ecnu.adsmls.utils.FileSystem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class CodePageController implements Initializable, Route {
    @FXML
    private AnchorPane rootLayout;
    @FXML
    private MenuBar menuBar;
    @FXML
    private StackPane directoryWrapper;
    // 左侧目录
    private MultiLevelDirectory multiLevelDirectory;
    // 目录的上下文菜单
    private List<MenuItem> multiLevelDirectoryMenu = new ArrayList<>();

    @FXML
    private TabPane tabPane;
    // 正在被访问的文件
    private Set<File> filesOpened = new HashSet<>();

    // 项目路径
    private String directory;
    // 项目名
    private String projectName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("init");
        this.initMenu();
    }

    @Override
    public void loadParams() {
        this.directory = CodePageParams.directory;
        this.projectName = CodePageParams.projectName;
        // 更新页面
        this.updateProject();
    }

    private void initMenu() {
        this.initMenuBar();
        this.initMultiLevelDirectoryMenu();
    }

    private void initMenuBar() {
        Menu fileMenu = new Menu("File");

        Menu newMenu = new Menu("New");
        MenuItem newDirectory = new MenuItem("Directory");
        MenuItem newModel = new MenuItem("Model");
        newModel.setOnAction(this::onNewModelClick);
        MenuItem newTree = new MenuItem("Tree");
        newTree.setOnAction(this::onNewTreeClick);
        newMenu.getItems().addAll(newDirectory, newModel, newTree);

        MenuItem closeProject = new MenuItem("Close Project");
        closeProject.setOnAction(e -> {
            // TODO 重置界面
            this.tabPane.getTabs().clear();
            this.filesOpened.clear();
            Router.back();
        });

        fileMenu.getItems().addAll(newMenu, closeProject);

        Menu editMenu = new Menu("Edit");
        MenuItem delete = new MenuItem("Delete");
        editMenu.getItems().add(delete);

        Menu helpMenu = new Menu("Help");

        this.menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);
    }

    private void initMultiLevelDirectoryMenu() {
        Menu newMenu = new Menu("New");
        MenuItem newDirectory = new MenuItem("Directory");
        MenuItem newModel = new MenuItem("Model");
        newModel.setOnAction(this::onNewModelClick);
        MenuItem newTree = new MenuItem("Tree");
        newTree.setOnAction(this::onNewTreeClick);
        newMenu.getItems().addAll(newDirectory, newModel, newTree);

        MenuItem delete = new MenuItem("Delete");

        this.multiLevelDirectoryMenu.add(newMenu);
        this.multiLevelDirectoryMenu.add(delete);
    }

    private void updateProject() {
        System.out.println("Initialize Project");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        AnchorPane anchorPane = new AnchorPane();

        // 重置左侧目录
        this.multiLevelDirectory = new MultiLevelDirectory(new File(this.directory + '/' + this.projectName));
        this.multiLevelDirectory.setMenu(this.multiLevelDirectoryMenu);

        this.multiLevelDirectory.getTreeView().setOnMouseClicked(e -> {
            TreeItem<File> selectedItem = this.multiLevelDirectory.getTreeView().getSelectionModel().getSelectedItem();
            if(selectedItem == null || !selectedItem.getValue().isFile()) {
                return;
            }
            if(e.getClickCount() == 2) {
                String suffix = FileSystem.getSuffix(selectedItem.getValue());
                this.openFile(selectedItem.getValue(), suffix);
            }
        });

        anchorPane.getChildren().add(this.multiLevelDirectory.getNode());
        AnchorPane.setTopAnchor(this.multiLevelDirectory.getNode(), 0.0);
        AnchorPane.setRightAnchor(this.multiLevelDirectory.getNode(), 0.0);
        AnchorPane.setBottomAnchor(this.multiLevelDirectory.getNode(), 0.0);
        AnchorPane.setLeftAnchor(this.multiLevelDirectory.getNode(), 0.0);

        scrollPane.setContent(anchorPane);
        this.directoryWrapper.getChildren().add(scrollPane);
    }

    @FXML

    protected void onRun() {
        if(this.tabPane.getTabs().size() == 0) {
            System.out.println("Please open model files first");
            return;
        }
        // 当前显示的 tab
        File file = ((File) this.tabPane.getSelectionModel().getSelectedItem().getUserData());

        String model = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            model = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MModel mModel = JSON.parseObject(model, MModel.class);
        if(mModel == null) {
            return;
        }
        System.out.println(model);

        String projectPath = FileSystem.concatAbsolutePath(this.directory, this.projectName);
        for(MCar mCar : mModel.getCars()) {
            String treePath = mCar.getTreePath();
            String tree = null;
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(projectPath, treePath)), StandardCharsets.UTF_8));
                tree = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            MTree mTree = JSON.parseObject(tree, MTree.class);
            if(mTree == null) {
                return;
            }
            System.out.println(tree);
            mCar.setMTree(mTree);
        }

        model = JSON.toJSONString(mModel);
        System.out.println(model);
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(projectPath, "test.adsml"),false), StandardCharsets.UTF_8));
            bw.write(model);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openFile(File file, String suffix) {
        if(this.filesOpened.contains(file)) {
            System.out.println("This file has already been opened");
            return;
        }
        Tab tab = new Tab(file.getName());
        tab.setUserData(file);
        tab.setOnClosed(e -> {
            System.out.println(tab.getText() + " closed");
            // TODO unsaved ?
            this.filesOpened.remove(file);
        });

        Node node;
        if(Objects.equals(suffix, FileSystem.Suffix.MODEL.value)) {
            node = this.openModel(file);
        }
        else if(Objects.equals(suffix, FileSystem.Suffix.TREE.value)) {
            node = this.openTree(file);
        }
        else {
            System.out.println("Unsupported file");
            return;
        }
        tab.setContent(node);
        tabPane.getTabs().add(tab);
        this.filesOpened.add(file);
    }

    private Node openModel(File file) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        String projectPath = FileSystem.concatAbsolutePath(this.directory, this.projectName);
        ModelEditor modelEditor = new ModelEditor(projectPath, FileSystem.getRelativePath(projectPath, file.getAbsolutePath()));
        modelEditor.load();

        scrollPane.setContent(modelEditor.getNode());
        scrollPane.setUserData(modelEditor);

        return scrollPane;
    }

    private void onNewModelClick(ActionEvent event) {
        System.out.println("Model");

        NewModelModal nmm = new NewModelModal();
        nmm.setDirectory(this.multiLevelDirectory.getTreeView().getFocusModel().getFocusedItem().getValue());
        nmm.getWindow().showAndWait();
        if(!nmm.isConfirm()) {
            return;
        }
        if(!nmm.isSucceed()) {
            System.out.println("File already exists");
            return;
        }
        this.multiLevelDirectory.updateNode();
        this.openFile(new File(nmm.getDirectory(), nmm.getFilename() + FileSystem.Suffix.MODEL.value), FileSystem.Suffix.MODEL.value);
    }

    private Node openTree(File file) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        // TODO ScrollPane 应在 TreeEditor 内部

        AnchorPane anchorPane = new AnchorPane();

        String projectPath = FileSystem.concatAbsolutePath(this.directory, this.projectName);
        TreeEditor treeEditor = new TreeEditor(projectPath, FileSystem.getRelativePath(projectPath, file.getAbsolutePath()));
        treeEditor.load();
        anchorPane.getChildren().add(treeEditor.getNode());

        scrollPane.setContent(anchorPane);
        scrollPane.setUserData(treeEditor);

        return scrollPane;
    }

    private void onNewTreeClick(ActionEvent event) {
        System.out.println("Tree");

        NewTreeModal ntm = new NewTreeModal();
        ntm.setDirectory(this.multiLevelDirectory.getTreeView().getFocusModel().getFocusedItem().getValue());
        ntm.getWindow().showAndWait();
        if(!ntm.isConfirm()) {
            return;
        }
        if(!ntm.isSucceed()) {
            System.out.println("File already exists");
            return;
        }
        this.multiLevelDirectory.updateNode();
        this.openFile(new File(ntm.getDirectory(), ntm.getFilename() + FileSystem.Suffix.TREE.value), FileSystem.Suffix.TREE.value);
    }
}