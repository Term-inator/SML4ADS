package com.ecnu.adsmls.views.codepage;

import com.ecnu.adsmls.components.ChooseFileButton;
import com.ecnu.adsmls.components.editor.TreeEditor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.*;


public class CodePageController implements Initializable {
    @FXML
    private AnchorPane rootLayout;
    @FXML
    private MenuBar menuBar;
    @FXML
    private TabPane tabPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("init");
        this.initMenu();
    }

    private void initMenu() {
        ObservableList<Menu> menus = menuBar.getMenus();
        Queue<Menu> queue = new LinkedList(menus);
        while(!queue.isEmpty()) {
            Menu menu = queue.poll();
            ObservableList<MenuItem> menuItems = menu.getItems();
            for(MenuItem menuItem : menuItems) {
                if(menuItem instanceof Menu) {
                    queue.offer((Menu) menuItem);
                }
                else {
                    String menuItemName = menuItem.getText();
                    if(Objects.equals(menuItemName, "Model")) {
                        menuItem.setOnAction(this::onNewModelClick);
                    }
                    else if(Objects.equals(menuItemName, "Tree")) {
                        menuItem.setOnAction(this::onNewTreeClick);
                    }
                }
            }
        }
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

    private void onNewTreeClick(ActionEvent event) {
        System.out.println("Tree");

        NewTreeModal ntm = new NewTreeModal();
        ntm.getWindow().showAndWait();
        if(!ntm.isConfirm()) {
            return;
        }

        Tab tab = new Tab(ntm.getFilename() + ".tree");
        tab.setOnClosed(e -> {
            System.out.println(tab.getText() + " closed");
        });

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        AnchorPane anchorPane = new AnchorPane();
        TreeEditor editor = new TreeEditor();
        editor.setDirectory(ntm.getDirectory());
        editor.setFilename(ntm.getFilename());
        anchorPane.getChildren().add(editor.getNode());

        scrollPane.setContent(anchorPane);

        tab.setContent(scrollPane);
        tabPane.getTabs().add(tab);
    }
}