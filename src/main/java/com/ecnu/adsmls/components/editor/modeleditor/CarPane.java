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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;


public class CarPane {
    private GridPane gridPane = new GridPane();
    private TextField tfName;
    private ComboBox<String> cbModel;
    private TextField tfMaxSpeed;
    private TextField tfInitSpeed;
    private TextField tfRoadId;
    private TextField tfLaneSectionId;
    private TextField tfLaneId;
    private TextArea taFilter;
    private TextField tfOffset;
    private ComboBox<String> cbHeading;
    private TextField tfRoadDeviation;
    private Node btDynamic;

    public CarPane() {
        this.createNode();
    }

    public MCar getModel() {
        MCar car = new MCar();
        car.setName(this.tfName.getText());
        car.setModel(this.cbModel.getValue());
        car.setMaxSpeed(Double.parseDouble(this.tfMaxSpeed.getText()));
        car.setInitSpeed(Double.parseDouble(this.tfInitSpeed.getText()));
        car.setRoadId(Integer.parseInt(this.tfRoadId.getText()));
        car.setLaneSecId(Integer.parseInt(this.tfLaneSectionId.getText()));
        car.setLaneId(Integer.parseInt(this.tfLaneId.getText()));
        car.setFilter(this.taFilter.getText());
        car.setHeading(Objects.equals("same", this.cbHeading.getValue()));
        car.setRoadDeviation(Double.parseDouble(this.tfRoadDeviation.getText()));
        String path = ((ChooseFileButton) this.btDynamic.getUserData()).getFile().getAbsolutePath();
        car.setTreePath(path);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
            String tree = br.readLine();
            car.setMTree(tree);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return car;
    }

    public void load(MCar mCar) {
        this.tfName.setText(mCar.getName());
        this.cbModel.getSelectionModel().select(mCar.getModel());
        this.tfMaxSpeed.setText(String.valueOf(mCar.getMaxSpeed()));
        this.tfInitSpeed.setText(String.valueOf(mCar.getInitSpeed()));
        this.tfRoadId.setText(String.valueOf(mCar.getRoadId()));
        this.tfLaneSectionId.setText(String.valueOf(mCar.getLaneSecId()));
        this.tfLaneId.setText(String.valueOf(mCar.getLaneId()));
        this.taFilter.setText(mCar.getFilter());
        this.tfOffset.setText(String.valueOf(mCar.getOffset()));
        this.cbHeading.getSelectionModel().select(mCar.getHeading() ? "same" : "opposite");
        this.tfRoadDeviation.setText(String.valueOf(mCar.getRoadDeviation()));
        ((ChooseFileButton) this.btDynamic.getUserData()).setFile(new File(mCar.getTreePath()));
    }

    private void createNode() {
        this.gridPane.setVgap(8);
        this.gridPane.setHgap(8);

        Label lbName = new Label("name: ");
        this.tfName = new TextField();

        Label lbModel = new Label("model: ");
        String[] models = {"random", "vehicle.audi.a2"};
        this.cbModel = new ComboBox<>(FXCollections.observableArrayList(models));
        this.cbModel.getSelectionModel().select(0);

        Label lbMaxSpeed = new Label("max speed: ");
        this.tfMaxSpeed = new TextField();

        Label lbInitSpeed = new Label("initial speed: ");
        this.tfInitSpeed = new TextField();

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
        this.tfRoadId = new TextField();
        Label lbLaneSectionId = new Label("lane section: ");
        this.tfLaneSectionId = new TextField();
        Label lbLaneId = new Label("lane: ");
        this.tfLaneId = new TextField();
        Label lbFilter = new Label("filter: ");
        this.taFilter = new TextArea();
        this.taFilter.setPrefRowCount(10);
        this.taFilter.setMinHeight(100);
        this.taFilter.setPrefColumnCount(15);
        Label lbOffset = new Label("offset: ");
        this.tfOffset = new TextField();
        gridPaneLocation.addRow(0, lbRoadId, this.tfRoadId);
        gridPaneLocation.addRow(1, lbLaneId, this.tfLaneId);
        gridPaneLocation.addRow(2, lbLaneSectionId, this.tfLaneSectionId);
        gridPaneLocation.addRow(3, lbFilter, this.taFilter);
        gridPaneLocation.addRow(4, lbOffset, this.tfOffset);

        Label lbHeading = new Label("heading: ");
        this.cbHeading = new ComboBox<>(FXCollections.observableArrayList("same", "opposite"));
        this.cbHeading.getSelectionModel().select(0);

        Label lbRoadDeviation = new Label("road deviation: ");
        this.tfRoadDeviation = new TextField();

        Label lbDynamic = new Label("Dynamic: ");
        this.btDynamic = new ChooseFileButton(this.gridPane).getNode();

        this.gridPane.addRow(0, lbName, this.tfName);
        this.gridPane.addRow(1, lbModel, this.cbModel);
        this.gridPane.addRow(2, lbMaxSpeed, this.tfMaxSpeed);
        this.gridPane.addRow(3, lbInitSpeed, this.tfInitSpeed);
        this.gridPane.addRow(4, lbLocation);
        this.gridPane.add(gridPaneLocation, 0, 5, 2, 1);
        this.gridPane.addRow(6, lbHeading, this.cbHeading);
        this.gridPane.addRow(7, lbRoadDeviation, this.tfRoadDeviation);
        this.gridPane.addRow(8, lbDynamic, this.btDynamic);
    }

    public Node getNode() {
        return this.gridPane;
    }
}
