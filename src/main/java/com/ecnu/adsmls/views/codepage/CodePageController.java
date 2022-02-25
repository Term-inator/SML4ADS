package com.ecnu.adsmls.views.codepage;

import com.alibaba.fastjson.JSON;
import com.ecnu.adsmls.components.editor.Editor;
import com.ecnu.adsmls.components.editor.modeleditor.ModelEditor;
import com.ecnu.adsmls.components.editor.treeeditor.TreeEditor;
import com.ecnu.adsmls.components.editor.treeeditor.impl.BehaviorRegister;
import com.ecnu.adsmls.components.modal.NewDirectoryModal;
import com.ecnu.adsmls.components.modal.NewFileModal;
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
        this.initBehavior();
        this.initMenu();
    }

    @Override
    public void loadParams() {
        this.directory = CodePageParams.directory;
        this.projectName = CodePageParams.projectName;
        // 更新页面
        this.updateProject();
    }

    /**
     * 初始化内置 behavior 及其参数
     */
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

    private void initMenu() {
        this.initMenuBar();
        this.initMultiLevelDirectoryMenu();
    }

    private void initMenuBar() {
        Menu fileMenu = new Menu("File");

        Menu newMenu = new Menu("New");
        MenuItem newDirectory = new MenuItem("Directory");
        newDirectory.setOnAction(e -> {
            this.newFile(FileSystem.Suffix.DIR.value);
        });
        MenuItem newModel = new MenuItem("Model");
        newModel.setOnAction(e -> {
            this.newFile(FileSystem.Suffix.MODEL.value);
        });
        MenuItem newTree = new MenuItem("Tree");
        newTree.setOnAction(e -> {
            this.newFile(FileSystem.Suffix.TREE.value);
        });
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
        newDirectory.setOnAction(e -> {
            this.newFile(FileSystem.Suffix.DIR.value);
        });
        MenuItem newModel = new MenuItem("Model");
        newModel.setOnAction(e -> {
            this.newFile(FileSystem.Suffix.MODEL.value);
        });
        MenuItem newTree = new MenuItem("Tree");
        newTree.setOnAction(e -> {
            this.newFile(FileSystem.Suffix.TREE.value);
        });
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
        File file = ((Editor) this.tabPane.getSelectionModel().getSelectedItem().getUserData()).getFile();

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

        // 仿真
        try {
            this.simulate();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openFile(File file, String suffix) {
        if(this.filesOpened.contains(file)) {
            System.out.println("This file has already been opened");
            // 选择对应的 tab
            for(Tab tab : this.tabPane.getTabs()) {
                if(Objects.equals(file, ((Editor) tab.getUserData()).getFile())) {
                    this.tabPane.getSelectionModel().select(tab);
                    break;
                }
            }
            return;
        }
        
        Tab tab = new Tab(file.getName());
        tab.setOnClosed(e -> {
            System.out.println(tab.getText() + " closed");
            // 关闭 tab 自动保存
            ((Editor) tab.getUserData()).save();
            this.filesOpened.remove(file);
        });

        if(Objects.equals(suffix, FileSystem.Suffix.MODEL.value)) {
            this.openModel(tab, file);
        }
        else if(Objects.equals(suffix, FileSystem.Suffix.TREE.value)) {
            this.openTree(tab, file);
        }
        else {
            System.out.println("Unsupported file");
            return;
        }

        this.tabPane.getTabs().add(tab);
        // 选择该 tab
        this.tabPane.getSelectionModel().select(tab);
        this.filesOpened.add(file);
    }

    private void newFile(String suffix) {
        NewFileModal nfm;
        if(Objects.equals(suffix, FileSystem.Suffix.MODEL.value)) {
            nfm = new NewModelModal();
        }
        else if(Objects.equals(suffix, FileSystem.Suffix.TREE.value)) {
            nfm = new NewTreeModal();
        }
        else if(Objects.equals(suffix, FileSystem.Suffix.DIR.value)) {
            nfm = new NewDirectoryModal();
        }
        else {
            System.out.println("Unsupported file");
            return;
        }

        nfm.setDirectory(this.multiLevelDirectory.getTreeView().getFocusModel().getFocusedItem().getValue());
        nfm.getWindow().showAndWait();
        if(!nfm.isConfirm()) {
            return;
        }
        if(!nfm.isSucceed()) {
            System.out.println("File or directory already exists");
            return;
        }
        this.multiLevelDirectory.updateNode();

        if(!Objects.equals(suffix, FileSystem.Suffix.DIR.value)) {
            this.openFile(new File(nfm.getDirectory(), nfm.getFilename() + suffix), suffix);
        }
    }

    private void openModel(Tab tab, File file) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        String projectPath = FileSystem.concatAbsolutePath(this.directory, this.projectName);
        ModelEditor modelEditor = new ModelEditor(projectPath, file);
        modelEditor.load();

        scrollPane.setContent(modelEditor.getNode());
        scrollPane.setUserData(modelEditor);

        tab.setContent(scrollPane);
        tab.setUserData(modelEditor);
    }

    private void openTree(Tab tab, File file) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        // TODO ScrollPane 应在 TreeEditor 内部

        AnchorPane anchorPane = new AnchorPane();

        String projectPath = FileSystem.concatAbsolutePath(this.directory, this.projectName);
        TreeEditor treeEditor = new TreeEditor(projectPath, file);
        treeEditor.load();
        anchorPane.getChildren().add(treeEditor.getNode());

        scrollPane.setContent(anchorPane);
        scrollPane.setUserData(treeEditor);

        tab.setContent(scrollPane);
        tab.setUserData(treeEditor);
    }

    private void simulate() throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec("python ./src/main/java/com/ecnu/adsmls/simulator/run.py --file ./a.adsml");
//        proc.waitFor();
        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while((line = in.readLine()) != null) {
            System.out.println(line);
        }
    }
}