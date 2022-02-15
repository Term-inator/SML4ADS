package com.ecnu.adsmls.views.codepage;

import com.ecnu.adsmls.components.ChooseFileButton;
import com.ecnu.adsmls.components.editor.modeleditor.ModelEditor;
import com.ecnu.adsmls.components.editor.treeeditor.TreeEditor;
import com.ecnu.adsmls.components.modal.NewTreeModal;
import com.ecnu.adsmls.components.mutileveldirectory.MultiLevelDirectory;
import com.ecnu.adsmls.router.Route;
import com.ecnu.adsmls.router.Router;
import com.ecnu.adsmls.router.params.CodePageParams;
import com.ecnu.adsmls.utils.FileSystem;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.File;
import java.net.URL;
import java.util.*;


public class CodePageController implements Initializable, Route {
    @FXML
    private AnchorPane rootLayout;
    @FXML
    private MenuBar menuBar;
    @FXML
    private StackPane directoryWrapper;

    private MultiLevelDirectory multiLevelDirectory;
    private List<MenuItem> multiLevelDirectoryMenu = new ArrayList<>();

    @FXML
    private TabPane tabPane;
    // 正在被访问的文件
    private Set<File> filesOpened = new HashSet<>();

    private String directory;
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

        this.multiLevelDirectory = new MultiLevelDirectory(new File(this.directory + '/' + this.projectName));
        this.multiLevelDirectory.setMenu(this.multiLevelDirectoryMenu);

        this.multiLevelDirectory.getTreeView().setOnMouseClicked(e -> {
            TreeItem<File> selectedItem = this.multiLevelDirectory.getTreeView().getSelectionModel().getSelectedItem();
            if(!selectedItem.getValue().isFile()) {
                return;
            }
            if(e.getClickCount() == 2) {
                if(FileSystem.getSuffix(selectedItem.getValue()).equals(FileSystem.Suffix.TREE.value)) {
                    this.openTree(selectedItem.getValue());
                }
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
        System.out.println("Run");
    }

    private void onNewModelClick(ActionEvent event) {
        System.out.println("Model");
        Tab tab = new Tab("untitled.model");
        tab.setOnClosed(e -> {
            System.out.println(tab.getText() + " closed");
        });

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        ModelEditor modelEditor = new ModelEditor();

        scrollPane.setContent(modelEditor.getNode());
        tab.setContent(scrollPane);
        tabPane.getTabs().add(tab);
    }

    private void openTree(File file) {
        if(this.filesOpened.contains(file)) {
            System.out.println("This file has already been opened");
            return;
        }
        this.filesOpened.add(file);

        Tab tab = new Tab(file.getName());
        tab.setOnClosed(e -> {
            System.out.println(tab.getText() + " closed");
            // TODO unsaved ?
            this.filesOpened.remove(file);
        });

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        AnchorPane anchorPane = new AnchorPane();
        TreeEditor editor = new TreeEditor();
        editor.setDirectory(file.getParentFile().getAbsolutePath());
        editor.setFilename(file.getName());
        editor.load();
        anchorPane.getChildren().add(editor.getNode());

        scrollPane.setContent(anchorPane);

        tab.setContent(scrollPane);
        tab.setUserData(editor);
        tabPane.getTabs().add(tab);
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
    }
}