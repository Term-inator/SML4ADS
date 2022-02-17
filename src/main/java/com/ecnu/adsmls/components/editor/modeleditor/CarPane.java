package com.ecnu.adsmls.components.editor.modeleditor;

import com.alibaba.fastjson.JSON;
import com.ecnu.adsmls.components.ChooseFileButton;
import com.ecnu.adsmls.model.MCar;
import com.ecnu.adsmls.model.MTree;
import com.ecnu.adsmls.utils.FileSystem;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 点击 New Car 显示的内容
 */
public class CarPane {
    private String projectPath;

    private GridPane gridPane = new GridPane();
    // 名称
    private TextField tfName;
    // 蓝图类型
    private ComboBox<String> cbModel;
    // 最大速度
    private TextField tfMaxSpeed;
    // 初速度
    private TextField tfInitSpeed;

    private TextField tfRoadId;

    private TextField tfLaneSectionId;

    private TextField tfLaneId;
    // 道路过滤器
    private TextArea taFilter;
    // 偏移量 [0, 1]，表示 Car 所在位置离 Road 起点的距离 / Road 总长
    private TextField tfOffset;
    // 朝向，和路同向或反向
    private ComboBox<String> cbHeading;
    // 偏移程度
    // TODO 这干啥的？
    private TextField tfRoadDeviation;
    // 动态信息，一棵树
    private Node btDynamic;

    public CarPane(String projectPath) {
        this.projectPath = projectPath;
        this.createNode();
    }

    public MCar save() {
        MCar car = new MCar();
        car.setName(this.tfName.getText());
        car.setModel(this.cbModel.getValue());
        car.setMaxSpeed(Double.parseDouble(this.tfMaxSpeed.getText()));
        car.setInitSpeed(Double.parseDouble(this.tfInitSpeed.getText()));
        car.setRoadId(Integer.parseInt(this.tfRoadId.getText()));
        car.setLaneSecId(Integer.parseInt(this.tfLaneSectionId.getText()));
        car.setLaneId(Integer.parseInt(this.tfLaneId.getText()));
        car.setFilter(this.taFilter.getText());
        car.setOffset(Double.parseDouble(this.tfOffset.getText()));
        car.setHeading(Objects.equals("same", this.cbHeading.getValue()));
        car.setRoadDeviation(Double.parseDouble(this.tfRoadDeviation.getText()));
        // 转换成相对路径
        String path = ((ChooseFileButton) this.btDynamic.getUserData()).getFile().getAbsolutePath();
        String relativePath = FileSystem.getRelativePath(this.projectPath, path);
        car.setTreePath(relativePath);
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
        // 恢复绝对路径
        ((ChooseFileButton) this.btDynamic.getUserData()).setFile(new File(this.projectPath, mCar.getTreePath()));
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
