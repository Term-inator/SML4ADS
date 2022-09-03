package com.ecnu.adsmls.components.editor.modeleditor;

import com.ecnu.adsmls.components.choosebutton.impl.ChooseFileButton;
import com.ecnu.adsmls.model.MCar;
import com.ecnu.adsmls.utils.FileSystem;
import com.ecnu.adsmls.utils.SimulatorConstant;
import com.ecnu.adsmls.utils.register.Function;
import com.ecnu.adsmls.utils.register.FunctionParam;
import com.ecnu.adsmls.utils.register.exception.DataTypeException;
import com.ecnu.adsmls.utils.register.exception.EmptyParamException;
import com.ecnu.adsmls.utils.register.exception.RequirementException;
import com.ecnu.adsmls.utils.register.impl.LocationRegister;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.util.*;


/**
 * 点击 New Vehicle 显示的内容
 */
public class CarPane {
    // Project 路径
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
    // 位置
    private ComboBox<String> cbLocation;
    // 位置参数
    private GridPane gridPaneLocationParams;
    LinkedHashMap<String, String> locationParams = new LinkedHashMap<>();
    // 朝向，和路同向或反向
    private ComboBox<String> cbHeading;
    // 偏移程度
    private TextField tfRoadDeviation;
    // 动态信息，一棵树
    private ChooseFileButton btDynamic;

    public CarPane(String projectPath) {
        this.projectPath = projectPath;
        this.createNode();
    }

    public void check() throws EmptyParamException, DataTypeException, RequirementException {
        if (this.tfName.getText().isEmpty()) {
            throw new EmptyParamException("car.name is required");
        }
        if (this.tfMaxSpeed.getText().isEmpty()) {
            throw new EmptyParamException("car.maxSpeed is required.");
        }
        if (this.tfInitSpeed.getText().isEmpty()) {
            throw new EmptyParamException("car.initSpeed is required.");
        }
        if (Double.parseDouble(this.tfInitSpeed.getText()) > Double.parseDouble(this.tfMaxSpeed.getText())) {
            throw new RequirementException("car.initSpeed should not be larger than car.maxSpeed.");
        }
        this.locationParams.clear();
        Function locationFunction = LocationRegister.getLocationFunction(this.cbLocation.getValue());
        String locationParamName = "";
        String locationParamValue = "";
        for (Node node : this.gridPaneLocationParams.getChildren()) {
            if (node instanceof Label) {
                locationParamName = ((Label) node).getText();
            } else if (node instanceof TextField) {
                locationParamValue = ((TextField) node).getText();
                this.locationParams.put(locationParamName, locationParamValue);
                locationFunction.updateContext(locationParamName, locationParamValue);
            }
        }
        locationFunction.check();
        if (this.tfRoadDeviation.getText().isEmpty()) {
            throw new EmptyParamException("car.roadDeviation is required");
        }
    }

    public MCar save() {
        MCar car = new MCar();
        car.setName(this.tfName.getText());
        car.setModel(this.cbModel.getValue());
        // TODO refactor 类型检查放入 check()
        try {
            car.setMaxSpeed(Double.parseDouble(this.tfMaxSpeed.getText()));
        } catch (Exception ignored) {
            car.setMaxSpeed(null);
        }
        try {
            car.setInitSpeed(Double.parseDouble(this.tfInitSpeed.getText()));
        } catch (Exception ignored) {
            car.setInitSpeed(null);
        }
        car.setLocationType(this.cbLocation.getValue());

        String locationParamName = "";
        String locationParamValue = "";
        for (Node node : this.gridPaneLocationParams.getChildren()) {
            if (node instanceof Label) {
                locationParamName = ((Label) node).getText();
            } else if (node instanceof TextField) {
                locationParamValue = ((TextField) node).getText();
                this.locationParams.put(locationParamName, locationParamValue);
            }
        }
        car.setLocationParams(this.locationParams);

        car.setHeading(Objects.equals("same", this.cbHeading.getValue()));
        try {
            car.setRoadDeviation(Double.parseDouble(this.tfRoadDeviation.getText()));
        } catch (Exception ignored) {
            car.setRoadDeviation(null);
        }

        File tree = this.btDynamic.getFile();
        if (tree == null) {
            car.setTreePath("");
        } else {
            // 转换成相对路径
            String path = this.btDynamic.getFile().getAbsolutePath();
            String relativePath = FileSystem.getRelativePath(this.projectPath, path);
            car.setTreePath(relativePath);
        }
        return car;
    }

    public void load(MCar mCar) {
        this.tfName.setText(mCar.getName());
        this.cbModel.getSelectionModel().select(mCar.getModel());
        try {
            this.tfMaxSpeed.setText(Double.toString(mCar.getMaxSpeed()));
        } catch (Exception ignored) {
        }
        try {
            this.tfInitSpeed.setText(Double.toString(mCar.getInitSpeed()));
        } catch (Exception ignored) {
        }
        this.cbLocation.getSelectionModel().select(mCar.getLocationType());

        String locationParamName = "";
        String locationParamValue = "";
        for (Node node : this.gridPaneLocationParams.getChildren()) {
            if (node instanceof Label) {
                locationParamName = ((Label) node).getText();
            } else if (node instanceof TextField) {
                locationParamValue = mCar.getLocationParams().get(locationParamName);
                ((TextField) node).setText(locationParamValue);
            }
        }

        this.cbHeading.getSelectionModel().select(mCar.getHeading() ? "same" : "opposite");
        try {
            this.tfRoadDeviation.setText(Double.toString(mCar.getRoadDeviation()));
        } catch (Exception ignored) {
        }
        if (!Objects.equals(mCar.getTreePath(), "")) {
            // 恢复绝对路径
            this.btDynamic.setFile(new File(this.projectPath, mCar.getTreePath()));
        }
    }

    private void createNode() {
        this.gridPane.setPadding(new Insets(0, 0, 12, 0));
        this.gridPane.setVgap(8);
        this.gridPane.setHgap(8);

        Label lbName = new Label("name");
        this.tfName = new TextField();

        Label lbModel = new Label("model");

        this.cbModel = new ComboBox<>();
        this.cbModel.setItems(FXCollections.observableArrayList(SimulatorConstant.getModel()));
        this.cbModel.getSelectionModel().select(0);

        Label lbMaxSpeed = new Label("maxSpeed");
        this.tfMaxSpeed = new TextField();

        Label lbInitSpeed = new Label("initialSpeed");
        this.tfInitSpeed = new TextField();

        Label lbLocation = new Label("location");
        List<String> locationTypes = LocationRegister.getLocationTypes();
        this.cbLocation = new ComboBox<>(FXCollections.observableArrayList(locationTypes));
        this.gridPaneLocationParams = new GridPane();
        gridPaneLocationParams.setPadding(new Insets(0, 0, 0, 20));
        gridPaneLocationParams.setHgap(8);
        gridPaneLocationParams.setVgap(8);
        cbLocation.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            gridPaneLocationParams.getChildren().clear();

            Function locationFunction = LocationRegister.getLocationFunction(newValue);
            // 生成界面
            int row = 0;
            for (FunctionParam param : locationFunction.getParams()) {
                Label lbParamName = new Label(param.getParamName());
                TextField tfParamValue = new TextField();
                gridPaneLocationParams.addRow(row++, lbParamName, tfParamValue);
            }
        });
        this.cbLocation.getSelectionModel().select(0);

        Label lbHeading = new Label("heading");
        this.cbHeading = new ComboBox<>(FXCollections.observableArrayList("same", "opposite"));
        this.cbHeading.getSelectionModel().select(0);

        Label lbRoadDeviation = new Label("roadDeviation");
        this.tfRoadDeviation = new TextField();

        Label lbDynamic = new Label("dynamic");
        // 限定选择 *.tree 文件
        Map<String, String> treeFilter = new HashMap<>();
        treeFilter.put(FileSystem.getRegSuffix(FileSystem.Suffix.TREE), FileSystem.Suffix.TREE.toString());
        this.btDynamic = new ChooseFileButton(this.gridPane, this.projectPath);
        this.btDynamic.setClearable(true);
        this.btDynamic.setFileFilter(treeFilter);

        this.gridPane.addRow(0, lbName, this.tfName);
        this.gridPane.addRow(1, lbModel, this.cbModel);
        this.gridPane.addRow(2, lbMaxSpeed, this.tfMaxSpeed);
        this.gridPane.addRow(3, lbInitSpeed, this.tfInitSpeed);
        this.gridPane.addRow(4, lbLocation, cbLocation);
        this.gridPane.add(gridPaneLocationParams, 0, 5, 2, 1);
        this.gridPane.addRow(6, lbHeading, this.cbHeading);
        this.gridPane.addRow(7, lbRoadDeviation, this.tfRoadDeviation);
        this.gridPane.addRow(8, lbDynamic, this.btDynamic.getNode());
    }

    // SimulatorType 改变时被调用
    public void notifyModel() {
        this.cbModel.setItems(FXCollections.observableArrayList(SimulatorConstant.getModel()));
    }

    public Node getNode() {
        return this.gridPane;
    }
}
