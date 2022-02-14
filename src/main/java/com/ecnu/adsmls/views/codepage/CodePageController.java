package com.ecnu.adsmls.views.codepage;

import com.ecnu.adsmls.components.ChooseFileButton;
import com.ecnu.adsmls.components.treeeditor.TreeEditor;
import com.ecnu.adsmls.components.modal.NewTreeModal;
import com.ecnu.adsmls.components.mutileveldirectory.MultiLevelDirectory;
import com.ecnu.adsmls.router.Route;
import com.ecnu.adsmls.router.Router;
import com.ecnu.adsmls.router.params.CodePageParams;
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
        MenuItem newTree = new MenuItem("Tree");
        newTree.setOnAction(this::onNewTreeClick);
        newMenu.getItems().addAll(newDirectory, newModel, newTree);

        MenuItem closeProject = new MenuItem("Close Project");
        closeProject.setOnAction(e -> {
            // TODO 重置界面
            this.tabPane.getTabs().clear();
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
                this.onOpenTree(selectedItem.getValue());
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

    private void updateGridPane(GridPane gridPane, ArrayList<Node[]> page) {
        gridPane.getChildren().clear();
        for(int r = 0; r < page.size(); ++r) {
            gridPane.addRow(r, page.get(r));
        }
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

        GridPane gridPane = new GridPane();
        gridPane.setPrefWidth(800);
        gridPane.setPrefWidth(800);
        gridPane.setPadding(new Insets(30, 40, 30, 40));
        gridPane.setVgap(8);

        ArrayList<Node[]> page = new ArrayList<>();

        GridPane props = new GridPane();
        props.setVgap(8);

        Label lbMap = new Label("Map: ");
        Node btMap = new ChooseFileButton(rootLayout).getNode();

        Label lbWeather = new Label("Weather: ");
        String weathers[] = {"clear", "rainy", "foggy"};
        ComboBox cbWeather = new ComboBox(FXCollections.observableArrayList(weathers));
        cbWeather.getSelectionModel().select(0);

        Label lbSource = new Label("Actor Source: ");
        Node btSource = new ChooseFileButton(rootLayout).getNode();

        props.addRow(0, lbMap, btMap);
        props.addRow(1, lbWeather, cbWeather);
        props.addRow(2, lbSource, btSource);

        Label lbCars = new Label("Cars: ");
        Button btNewCar = new Button("New Car");
        btNewCar.setOnMouseClicked(e -> {
            Node source = (Node) e.getSource();
            int row = GridPane.getRowIndex(source);
            GridPane gridPaneCar = new GridPane();
            gridPaneCar.setPadding(new Insets(8, 0, 8, 20));
            gridPaneCar.setVgap(8);

            Label lbName = new Label("name: ");
            TextField tfName = new TextField();

            Label lbModel = new Label("model: ");
            String[] models = {"random", "vehicle.audi.a2"};
            ComboBox cbModel = new ComboBox(FXCollections.observableArrayList(models));
            cbModel.getSelectionModel().select(0);

            Label lbMaxSpeed = new Label("max speed: ");
            TextField tfMaxSpeed = new TextField();

            Label lbInitSpeed = new Label("initial speed: ");
            TextField tfInitSpeed = new TextField();

            // TODO macAcc?
            /** TODO location
             *  1. 确定lane (filter函数 / 3 个 id)
             *  2. 数值 距离 lane 起始位置的偏移量
             */
            // TODO time step
            Label lbLocation = new Label("location: ");

            Label lbHeading = new Label("heading: ");
            ComboBox cbHeading = new ComboBox(FXCollections.observableArrayList("same", "opposite"));
            cbHeading.getSelectionModel().select(0);

            Label lbRoadDeviation = new Label("road deviation: ");
            TextField tfRoadDeviation = new TextField();

            Label lbDynamic = new Label("Dynamic: ");
            String[] trees = {"test.tree"};
            Node btDynamic = new ChooseFileButton(rootLayout).getNode();

            gridPaneCar.addRow(0, lbName, tfName);
            gridPaneCar.addRow(1, lbModel, cbModel);
            gridPaneCar.addRow(2, lbMaxSpeed, tfMaxSpeed);
            gridPaneCar.addRow(3, lbInitSpeed, tfInitSpeed);
            gridPaneCar.addRow(5, lbLocation);
            gridPaneCar.addRow(6, lbHeading, cbHeading);
            gridPaneCar.addRow(7, lbRoadDeviation, tfRoadDeviation);
            gridPaneCar.addRow(8, lbDynamic, btDynamic);

            page.add(row, new Node[] {gridPaneCar});
            this.updateGridPane(gridPane, page);
        });

        Label lbPedestrians = new Label("Pedestrians: ");
        Button btNewPedestrian = new Button("New Pedestrian");

        Label lbObstacles = new Label("Obstacles: ");
        Button btNewObstacle = new Button("New Obstacle");

        page.add(new Node[] {props});
        page.add(new Node[] {lbCars});
        page.add(new Node[] {btNewCar});
        page.add(new Node[] {lbPedestrians});
        page.add(new Node[] {btNewPedestrian});
        page.add(new Node[] {lbObstacles});
        page.add(new Node[] {btNewObstacle});

        this.updateGridPane(gridPane, page);

        scrollPane.setContent(gridPane);
        tab.setContent(scrollPane);
        tabPane.getTabs().add(tab);
    }

    private void onOpenTree(File file) {
        Tab tab = new Tab(file.getName());
        tab.setOnClosed(e -> {
            System.out.println(tab.getText() + " closed");
        });

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        AnchorPane anchorPane = new AnchorPane();
        TreeEditor editor = new TreeEditor();
        editor.setDirectory(file.getParentFile().getAbsolutePath());
        editor.setFilename(file.getName());
        editor.loadTree();
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