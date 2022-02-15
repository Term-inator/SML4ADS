package com.ecnu.adsmls.components.editor.modeleditor;

import com.ecnu.adsmls.components.ChooseFileButton;
import com.ecnu.adsmls.components.editor.Editor;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

public class ModelEditor extends Editor {
    private GridPane gridPane = new GridPane();
    private List<Node[]> staticPage = new ArrayList<>();
    private GridPane gridPaneCar = new GridPane();
    private List<CarPane> carPanes = new ArrayList<>();

    private GridPane gridPanePedestrian = new GridPane();
    private List<Node[]> newPedestrianPage = new ArrayList<>();
    private GridPane gridPaneObstacle = new GridPane();
    private List<Node[]> newObstaclePage = new ArrayList<>();

    public ModelEditor() {
        this.createNode();
    }

    private void updateGridPane(GridPane gridPane, List<Node[]> page) {
        gridPane.getChildren().clear();
        for(int r = 0; r < page.size(); ++r) {
            gridPane.addRow(r, page.get(r));
        }
    }

    @Override
    public void save() {

    }

    @Override
    public void load() {

    }

    @Override
    protected void createNode() {
        gridPane.setPrefWidth(800);
        gridPane.setPrefWidth(800);
        gridPane.setPadding(new Insets(30, 40, 30, 40));
        gridPane.setVgap(8);

        gridPaneCar.setPadding(new Insets(0, 0, 8, 20));
        gridPaneCar.setHgap(8);
        gridPaneCar.setVgap(8);

        Label lbMap = new Label("Map: ");
        Node btMap = new ChooseFileButton(gridPane).getNode();

        Label lbWeather = new Label("Weather: ");
        String[] weathers = {"clear", "rainy", "foggy"};
        ComboBox<String> cbWeather = new ComboBox<>(FXCollections.observableArrayList(weathers));
        cbWeather.getSelectionModel().select(0);

        Label lbTimeStep = new Label("Time Step: ");
        Spinner<Integer> spTimeStep = new Spinner<>(1, 10, 1);
        spTimeStep.setPrefWidth(80);

        Label lbSource = new Label("Actor Source: ");
        Node btSource = new ChooseFileButton(gridPane).getNode();

        Label lbCars = new Label("Cars: ");

        Button btNewCar = new Button("New Car");
        btNewCar.setOnMouseClicked(e -> {
            this.newCar();
        });

        Label lbPedestrians = new Label("Pedestrians: ");
        Button btNewPedestrian = new Button("New Pedestrian");

        Label lbObstacles = new Label("Obstacles: ");
        Button btNewObstacle = new Button("New Obstacle");

        this.gridPane.addRow(0, lbMap, btMap);
        this.gridPane.addRow(1, lbWeather, cbWeather);
        this.gridPane.addRow(2, lbSource, btSource);
        this.gridPane.addRow(3, lbTimeStep, spTimeStep);
        this.gridPane.addRow(4, lbCars);
        this.gridPane.add(this.gridPaneCar, 0, 5, 2, 1);
        this.gridPane.addRow(6, btNewCar);
        this.gridPane.addRow(7, lbPedestrians);
        this.gridPane.add(this.gridPanePedestrian, 0, 8, 2, 1);
        this.gridPane.addRow(9, btNewPedestrian);
        this.gridPane.addRow(10, lbObstacles);
        this.gridPane.add(this.gridPaneObstacle, 0, 11, 2, 1);
        this.gridPane.addRow(12, btNewObstacle);
    }

    public void newCar() {
        CarPane carPane = new CarPane();
        this.carPanes.add(carPane);

        List<Node[]> page = new ArrayList<>();

        for(int i = 0; i < this.carPanes.size(); ++i) {
            CarPane car = this.carPanes.get(i);
            if(i != 0) {
                Separator separator1 = new Separator();
                Separator separator2 = new Separator();
                page.add(new Node[] {separator1, separator2});
            }
            AnchorPane buttonWrapper = new AnchorPane();
            Button btDelete = new Button("Delete");
            btDelete.setUserData(i);
            btDelete.setOnAction(e -> {
                this.deleteCar((Integer) btDelete.getUserData());
            });
            buttonWrapper.getChildren().add(btDelete);
            AnchorPane.setTopAnchor(btDelete, 0.0);
            page.add(new Node[] {car.getNode(), buttonWrapper});
        }

        this.updateGridPane(this.gridPaneCar, page);
    }

    public void deleteCar(int index) {
        System.out.println("delete car" + index);
    }

    @Override
    public Node getNode() {
        return this.gridPane;
    }
}
