package com.ecnu.adsmls.components.editor.modeleditor;

import com.ecnu.adsmls.components.ChooseFileButton;
import com.ecnu.adsmls.components.editor.Editor;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

public class ModelEditor extends Editor {
    private GridPane gridPane = new GridPane();
    private List<Node[]> staticPage = new ArrayList<>();
    private GridPane gridPaneCar = new GridPane();
    private List<CarPane> carPanes = new ArrayList<>();
//    private GridPane gridPanePedestrian = new GridPane();
//    private List<Node[]> newPedestrianPage = new ArrayList<>();
//    private GridPane gridPaneObstacle = new GridPane();
//    private List<Node[]> newObstaclePage = new ArrayList<>();

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
        gridPaneCar.setVgap(8);

        GridPane props = new GridPane();
        props.setVgap(8);

        Label lbMap = new Label("Map: ");
        Node btMap = new ChooseFileButton(gridPane).getNode();

        Label lbWeather = new Label("Weather: ");
        String weathers[] = {"clear", "rainy", "foggy"};
        ComboBox cbWeather = new ComboBox(FXCollections.observableArrayList(weathers));
        cbWeather.getSelectionModel().select(0);

        Label lbTimeStep = new Label("Time Step");
        Spinner<Integer> spTimeStep = new Spinner<>(1, 10, 1);

        Label lbSource = new Label("Actor Source: ");
        Node btSource = new ChooseFileButton(gridPane).getNode();

        props.addRow(0, lbMap, btMap);
        props.addRow(1, lbWeather, cbWeather);
        props.addRow(2, lbSource, btSource);
        props.addRow(3, lbTimeStep, spTimeStep);

        Label lbCars = new Label("Cars: ");

        Button btNewCar = new Button("New Car");
        btNewCar.setOnMouseClicked(e -> {
            this.newCar();
        });

        Label lbPedestrians = new Label("Pedestrians: ");
        Button btNewPedestrian = new Button("New Pedestrian");

        Label lbObstacles = new Label("Obstacles: ");
        Button btNewObstacle = new Button("New Obstacle");

        staticPage.add(new Node[] {props});
        staticPage.add(new Node[] {lbCars});
        staticPage.add(new Node[] {this.gridPaneCar});
        staticPage.add(new Node[] {btNewCar});
        staticPage.add(new Node[] {lbPedestrians});
//        staticPage.add(new Node[] {this.gridPanePedestrian});
        staticPage.add(new Node[] {btNewPedestrian});
        staticPage.add(new Node[] {lbObstacles});
//        staticPage.add(new Node[] {this.gridPaneObstacle});
        staticPage.add(new Node[] {btNewObstacle});

        this.updateGridPane(this.gridPane, staticPage);
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
            page.add(new Node[] {car.getNode()});
        }

        this.updateGridPane(this.gridPaneCar, page);
    }

    public void deleteCar() {

    }

    @Override
    public Node getNode() {
        return this.gridPane;
    }
}
