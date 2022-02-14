package com.ecnu.adsmls.components.editor.modeleditor;

import com.ecnu.adsmls.components.ChooseFileButton;
import com.ecnu.adsmls.components.editor.Editor;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

public class ModelEditor extends Editor {
    private GridPane gridPane = new GridPane();

    public ModelEditor() {
        this.createNode();
    }

    private void updateGridPane(List<Node[]> page) {
        this.gridPane.getChildren().clear();
        for(int r = 0; r < page.size(); ++r) {
            this.gridPane.addRow(r, page.get(r));
        }
    }

    @Override
    public void save() {

    }

    @Override
    public void load() {

    }

    @Override
    public void createNode() {
        gridPane.setPrefWidth(800);
        gridPane.setPrefWidth(800);
        gridPane.setPadding(new Insets(30, 40, 30, 40));
        gridPane.setVgap(8);

        ArrayList<Node[]> page = new ArrayList<>();

        GridPane props = new GridPane();
        props.setVgap(8);

        Label lbMap = new Label("Map: ");
        Node btMap = new ChooseFileButton(gridPane).getNode();

        Label lbWeather = new Label("Weather: ");
        String weathers[] = {"clear", "rainy", "foggy"};
        ComboBox cbWeather = new ComboBox(FXCollections.observableArrayList(weathers));
        cbWeather.getSelectionModel().select(0);

        Label lbSource = new Label("Actor Source: ");
        Node btSource = new ChooseFileButton(gridPane).getNode();

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
            Node btDynamic = new ChooseFileButton(gridPane).getNode();

            gridPaneCar.addRow(0, lbName, tfName);
            gridPaneCar.addRow(1, lbModel, cbModel);
            gridPaneCar.addRow(2, lbMaxSpeed, tfMaxSpeed);
            gridPaneCar.addRow(3, lbInitSpeed, tfInitSpeed);
            gridPaneCar.addRow(5, lbLocation);
            gridPaneCar.addRow(6, lbHeading, cbHeading);
            gridPaneCar.addRow(7, lbRoadDeviation, tfRoadDeviation);
            gridPaneCar.addRow(8, lbDynamic, btDynamic);

            page.add(row, new Node[] {gridPaneCar});
            this.updateGridPane(page);
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

        this.updateGridPane(page);
    }

    @Override
    public Node getNode() {
        return this.gridPane;
    }
}
