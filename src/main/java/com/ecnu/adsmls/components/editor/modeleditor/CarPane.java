package com.ecnu.adsmls.components.editor.modeleditor;

import com.alibaba.fastjson.JSON;
import com.ecnu.adsmls.components.ChooseFileButton;
import com.ecnu.adsmls.model.MCar;
import com.ecnu.adsmls.model.MTree;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class CarPane {
    GridPane gridPane = new GridPane();
    Map<String, Node> values = new HashMap<>();

    public CarPane() {
        this.createNode();
    }

    private void createNode() {
        this.gridPane.setVgap(8);
        this.gridPane.setHgap(8);

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
        GridPane gridPaneLocation = new GridPane();
        gridPaneLocation.setPrefWidth(280);
        gridPaneLocation.setPadding(new Insets(0, 0, 0 , 20));
        gridPaneLocation.setHgap(8);
        gridPaneLocation.setVgap(8);
        Label lbRoadId = new Label("road: ");
        TextField tfRoadId = new TextField();
        Label lbLaneSectionId = new Label("lane section: ");
        TextField tfLaneSectionId = new TextField();
        Label lbLaneId = new Label("lane: ");
        TextField tfLaneId = new TextField();
        Label lbFilter = new Label("filter: ");
        TextArea taFilter = new TextArea();
        taFilter.setPrefRowCount(10);
        taFilter.setMinHeight(100);
        taFilter.setPrefColumnCount(15);
        Label lbOffset = new Label("offset: ");
        TextField tfOffset = new TextField();
        gridPaneLocation.addRow(0, lbRoadId, tfRoadId);
        gridPaneLocation.addRow(1, lbLaneId, tfLaneId);
        gridPaneLocation.addRow(2, lbLaneSectionId, tfLaneSectionId);
        gridPaneLocation.addRow(3, lbFilter, taFilter);
        gridPaneLocation.addRow(4, lbOffset, tfOffset);

        Label lbHeading = new Label("heading: ");
        ComboBox cbHeading = new ComboBox(FXCollections.observableArrayList("same", "opposite"));
        cbHeading.getSelectionModel().select(0);

        Label lbRoadDeviation = new Label("road deviation: ");
        TextField tfRoadDeviation = new TextField();

        Label lbDynamic = new Label("Dynamic: ");
        String[] trees = {"test.tree"};
        Node btDynamic = new ChooseFileButton(this.gridPane).getNode();

        this.values.put("name", tfName);
        this.values.put("model", cbModel);
        this.values.put("maxSpeed", tfMaxSpeed);
        this.values.put("initSpeed", tfInitSpeed);
        this.values.put("location.roadId", tfRoadId);
        this.values.put("location.laneSecId", tfLaneSectionId);
        this.values.put("location.laneId", tfLaneId);
        this.values.put("heading", cbHeading);
        this.values.put("roadDeviation", tfRoadDeviation);
        this.values.put("dynamic", btDynamic);

        this.gridPane.addRow(0, lbName, tfName);
        this.gridPane.addRow(1, lbModel, cbModel);
        this.gridPane.addRow(2, lbMaxSpeed, tfMaxSpeed);
        this.gridPane.addRow(3, lbInitSpeed, tfInitSpeed);
        this.gridPane.addRow(4, lbLocation);
        this.gridPane.add(gridPaneLocation, 0, 5, 2, 1);
        this.gridPane.addRow(6, lbHeading, cbHeading);
        this.gridPane.addRow(7, lbRoadDeviation, tfRoadDeviation);
        this.gridPane.addRow(8, lbDynamic, btDynamic);
    }

    public MCar getModel() {
        MCar car = new MCar();
        car.setName(((TextField) this.values.get("name")).getText());
        car.setModel(((ComboBox<String>) this.values.get("model")).getValue());
        car.setMaxSpeed(Double.parseDouble(((TextField) this.values.get("maxSpeed")).getText()));
        car.setInitSpeed(Double.parseDouble(((TextField) this.values.get("initSpeed")).getText()));
        car.setRoadId(Integer.parseInt(((TextField) this.values.get("location.roadId")).getText()));
        car.setLaneSecId(Integer.parseInt(((TextField) this.values.get("location.laneSecId")).getText()));
        car.setLaneId(Integer.parseInt(((TextField) this.values.get("location.laneId")).getText()));
        car.setHeading(Objects.equals("same", ((ComboBox<String>) this.values.get("heading")).getValue()));
        car.setRoadDeviation(Double.parseDouble(((TextField) this.values.get("roadDeviation")).getText()));
        String path = ((ChooseFileButton) this.values.get("dynamic").getUserData()).getFile().getAbsolutePath();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
            String tree = br.readLine();
            car.setMTree(tree);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return car;
    }

    public Node getNode() {
        return this.gridPane;
    }
}
