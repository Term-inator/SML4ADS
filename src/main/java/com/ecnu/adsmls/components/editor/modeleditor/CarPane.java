package com.ecnu.adsmls.components.editor.modeleditor;

import com.ecnu.adsmls.components.ChooseFileButton;
import com.ecnu.adsmls.model.MCar;
import com.ecnu.adsmls.utils.EmptyParamException;
import com.ecnu.adsmls.utils.FileSystem;
import com.ecnu.adsmls.utils.FunctionRegister;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.*;
import java.util.*;

/**
 * 点击 New Car 显示的内容
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
    private Node btDynamic;

    public CarPane(String projectPath) {
        this.projectPath = projectPath;
        this.createNode();
    }

    private boolean check() {
        if(this.tfName.getText().isEmpty() ||
                this.tfMaxSpeed.getText().isEmpty() ||
                this.tfInitSpeed.getText().isEmpty() ||
                this.cbLocation.getValue().isEmpty() ||
                this.tfRoadDeviation.getText().isEmpty()) {
            return false;
        }

        this.locationParams.clear();
        List<FunctionRegister.FunctionParam> paramsInfo = LocationRegister.getParams(this.cbLocation.getValue());
        String locationParamName = "";
        String locationParamValue = "";
        int i = 0;
        // TODO 进一步的参数检查
        for(Node node : this.gridPaneLocationParams.getChildren()) {
            if(node instanceof Label) {
                locationParamName = ((Label) node).getText();
            }
            else if(node instanceof TextField) {
                locationParamValue = ((TextField) node).getText();
                if(paramsInfo.get(i).check(locationParamValue)) {
                    this.locationParams.put(locationParamName, locationParamValue);
                }
                else {
                    return false;
                }
            }
            ++i;
        }
        return true;
    }

    public MCar save() throws EmptyParamException {
        if(!check()) {
            throw new EmptyParamException("Required param(s) is/are empty.");
        }

        MCar car = new MCar();
        car.setName(this.tfName.getText());
        car.setModel(this.cbModel.getValue());
        car.setMaxSpeed(Double.parseDouble(this.tfMaxSpeed.getText()));
        car.setInitSpeed(Double.parseDouble(this.tfInitSpeed.getText()));
        car.setLocationType(this.cbLocation.getValue());
        car.setLocationParams(this.locationParams);

        car.setHeading(Objects.equals("same", this.cbHeading.getValue()));
        car.setRoadDeviation(Double.parseDouble(this.tfRoadDeviation.getText()));

        File tree = ((ChooseFileButton) this.btDynamic.getUserData()).getFile();
        if (tree == null) {
            car.setTreePath("");
        } else {
            // 转换成相对路径
            String path = ((ChooseFileButton) this.btDynamic.getUserData()).getFile().getAbsolutePath();
            String relativePath = FileSystem.getRelativePath(this.projectPath, path);
            car.setTreePath(relativePath);
        }
        return car;
    }

    public void load(MCar mCar) {
        this.tfName.setText(mCar.getName());
        this.cbModel.getSelectionModel().select(mCar.getModel());
        this.tfMaxSpeed.setText(String.valueOf(mCar.getMaxSpeed()));
        this.tfInitSpeed.setText(String.valueOf(mCar.getInitSpeed()));
        this.cbLocation.getSelectionModel().select(mCar.getLocationType());

        String locationParamName = "";
        for(Node node : this.gridPaneLocationParams.getChildren()) {
            if(node instanceof Label) {
                locationParamName = ((Label) node).getText();
            }
            else if(node instanceof TextField) {
                ((TextField) node).setText(mCar.getLocationParams().get(locationParamName));
            }
        }

        this.cbHeading.getSelectionModel().select(mCar.getHeading() ? "same" : "opposite");
        this.tfRoadDeviation.setText(String.valueOf(mCar.getRoadDeviation()));
        if (!Objects.equals(mCar.getTreePath(), "")) {
            // 恢复绝对路径
            ((ChooseFileButton) this.btDynamic.getUserData()).setFile(new File(this.projectPath, mCar.getTreePath()));
        }
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

        Label lbLocation = new Label("location: ");
        List<String> behaviorNames = LocationRegister.getLocationTypes();
        this.cbLocation = new ComboBox<>(FXCollections.observableArrayList(behaviorNames));
        this.gridPaneLocationParams = new GridPane();
        gridPaneLocationParams.setPadding(new Insets(0, 0, 0, 20));
        gridPaneLocationParams.setHgap(8);
        gridPaneLocationParams.setVgap(8);
        cbLocation.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            gridPaneLocationParams.getChildren().clear();

            List<FunctionRegister.FunctionParam> paramsInfo = LocationRegister.getParams(newValue);
            // 生成界面
            int row = 0;
            for(FunctionRegister.FunctionParam param : paramsInfo) {
                Label lbParamName = new Label(param.getParamName());
                TextField tfParamValue = new TextField();
                gridPaneLocationParams.addRow(row++, lbParamName, tfParamValue);
            }
        });

        Label lbHeading = new Label("heading: ");
        this.cbHeading = new ComboBox<>(FXCollections.observableArrayList("same", "opposite"));
        this.cbHeading.getSelectionModel().select(0);

        Label lbRoadDeviation = new Label("road deviation: ");
        this.tfRoadDeviation = new TextField();

        Label lbDynamic = new Label("Dynamic: ");
        // 限定选择 *.tree 文件
        Map<String, String> treeFilter = new HashMap<>();
        treeFilter.put(FileSystem.getRegSuffix(FileSystem.Suffix.TREE), FileSystem.Suffix.TREE.toString());
        this.btDynamic = new ChooseFileButton(this.gridPane, this.projectPath, treeFilter).getNode();

        this.gridPane.addRow(0, lbName, this.tfName);
        this.gridPane.addRow(1, lbModel, this.cbModel);
        this.gridPane.addRow(2, lbMaxSpeed, this.tfMaxSpeed);
        this.gridPane.addRow(3, lbInitSpeed, this.tfInitSpeed);
        this.gridPane.addRow(4, lbLocation, cbLocation);
        this.gridPane.add(gridPaneLocationParams, 0, 5, 2, 1);
        this.gridPane.addRow(6, lbHeading, this.cbHeading);
        this.gridPane.addRow(7, lbRoadDeviation, this.tfRoadDeviation);
        this.gridPane.addRow(8, lbDynamic, this.btDynamic);
    }

    public Node getNode() {
        return this.gridPane;
    }
}
