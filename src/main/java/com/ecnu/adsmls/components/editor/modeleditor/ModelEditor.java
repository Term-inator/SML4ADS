package com.ecnu.adsmls.components.editor.modeleditor;

import com.alibaba.fastjson.JSON;
import com.ecnu.adsmls.components.ChooseFileButton;
import com.ecnu.adsmls.components.editor.Editor;
import com.ecnu.adsmls.components.editor.FormEditor;
import com.ecnu.adsmls.model.MCar;
import com.ecnu.adsmls.model.MModel;
import com.ecnu.adsmls.utils.FileSystem;
import com.ecnu.adsmls.utils.GridPaneUtils;
import com.ecnu.adsmls.utils.SimulatorConstant;
import com.ecnu.adsmls.utils.SimulatorTypeObserver;
import com.ecnu.adsmls.utils.register.exception.DataTypeException;
import com.ecnu.adsmls.utils.register.exception.EmptyParamException;
import com.ecnu.adsmls.utils.register.exception.RequirementException;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.util.*;

public class ModelEditor extends FormEditor implements SimulatorTypeObserver {
    /**
     * 地图文件
     */
    private GridPane mapPane = new GridPane();
    private ComboBox<String> cbMapType;
    private Node btMap;
    private String[] defaultMaps = {};
    private ComboBox<String> cbDefaultMap;

    // 天气
    private ComboBox<String> cbWeatherType;
    private GridPane weatherPane = new GridPane();
    private Node btWeather;
    private String[] defaultWeathers = {};
    private ComboBox<String> cbDefaultWeather;


    private double timeStepMin = 0.1;
    private double timeStepMax = 10.0;
    private Spinner<Double> spTimeStep;
    // 总模拟时间 是 time step 的倍数
    private TextField tfSimulationTime;

    // 模拟结束扳机
    private TextArea taScenarioEndTrigger;

    // 验证规则
    private Node btRequirements;

    private GridPane gridPaneCar = new GridPane();
    // 临时 id ，用于删除
    private int carId = 0;
    private Map<Integer, CarPane> carPanes = new LinkedHashMap<>();

//    private GridPane gridPanePedestrian = new GridPane()
//    private Map<Integer, PedestrianPane> pedestrianPanes = new LinkedHashMap<>();
//    private GridPane gridPaneObstacle = new GridPane();
//    private Map<Integer, ObstaclePane> obstaclePanes = new LinkedHashMap<>();

    public ModelEditor(String projectPath, File file) {
        super(projectPath, file);
        this.createNode();
    }

    @Override
    public void check() throws EmptyParamException, DataTypeException, RequirementException {
        if (Objects.equals(this.cbMapType.getValue(), "custom")) {
            if (((ChooseFileButton) this.btMap.getUserData()).getFile() == null) {
                throw new EmptyParamException("Map is required.");
            }
        }
        if (Objects.equals(this.cbWeatherType.getValue(), "custom")) {
            if (((ChooseFileButton) this.btWeather.getUserData()).getFile() == null) {
                throw new EmptyParamException("Weather is required.");
            }
        }
        if (this.tfSimulationTime.getText().isEmpty()) {
            throw new EmptyParamException("Simulation Time is required.");
        }
        // simulation time 是 time step 的倍数
        if (Double.parseDouble(this.tfSimulationTime.getText()) / this.spTimeStep.getValue() !=
                Math.floor(Double.parseDouble(this.tfSimulationTime.getText()) / this.spTimeStep.getValue())) {
            throw new RequirementException("Simulation Time should be a multiple of Time Step");
        }
        for (Map.Entry<Integer, CarPane> entry : this.carPanes.entrySet()) {
            entry.getValue().check();
        }
    }

    // 由于关闭自动保存，其他在打开 Editor 后修改的内容会在关闭时被覆盖，所以 save 前要先 load
    @Override
    public void save() {
        String model = FileSystem.JSONReader(new File(this.projectPath, this.relativePath));
        MModel mModel = JSON.parseObject(model, MModel.class);
        if (mModel == null) {
            mModel = new MModel();
        }
        System.out.println(model);

        mModel.setMapType(this.cbMapType.getValue());
        if (Objects.equals(this.cbMapType.getValue(), "custom")) {
            File map = ((ChooseFileButton) this.btMap.getUserData()).getFile();
            if (map == null) {
                mModel.setMap("");
            } else {
                // 转换成相对路径
                String path = map.getAbsolutePath();
                String relativePath = FileSystem.getRelativePath(this.projectPath, path);
                mModel.setMap(relativePath);
            }
        } else if (Objects.equals(this.cbMapType.getValue(), "default")) {
            mModel.setMap(this.cbDefaultMap.getValue() + FileSystem.Suffix.MAP.value);
        }

        mModel.setWeatherType(this.cbWeatherType.getValue());
        if (Objects.equals(this.cbWeatherType.getValue(), "custom")) {
            File weather = ((ChooseFileButton) this.btWeather.getUserData()).getFile();
            if (weather == null) {
                mModel.setWeather("");
            } else {
                // 转换成相对路径
                String path = weather.getAbsolutePath();
                String relativePath = FileSystem.getRelativePath(this.projectPath, path);
                mModel.setWeather(relativePath);
            }
        } else if (Objects.equals(this.cbWeatherType.getValue(), "default")) {
            mModel.setWeather(this.cbDefaultWeather.getValue() + FileSystem.Suffix.WEATHER.value);
        }

        mModel.setTimeStep(this.spTimeStep.getValue());

        try {
            mModel.setSimulationTime(Double.parseDouble(this.tfSimulationTime.getText()));
        } catch (Exception ignored) {
            mModel.setSimulationTime(null);
        }

        mModel.setScenarioEndTrigger(this.taScenarioEndTrigger.getText());

        File requirements = ((ChooseFileButton) this.btRequirements.getUserData()).getFile();
        if (requirements == null) {
            mModel.setRequirementsPath("");
        } else {
            // 转换成相对路径
            String path = requirements.getAbsolutePath();
            String relativePath = FileSystem.getRelativePath(this.projectPath, path);
            mModel.setRequirementsPath(relativePath);
        }

        List<MCar> cars = new ArrayList<>();
        for (Map.Entry<Integer, CarPane> entry : this.carPanes.entrySet()) {
            CarPane carPane = entry.getValue();
            cars.add(carPane.save());
        }
        mModel.setCars(cars);
        model = JSON.toJSONString(mModel);
        System.out.println(model);
        FileSystem.JSONWriter(new File(this.projectPath, this.relativePath), model);
    }

    @Override
    public void load() {
        String model = FileSystem.JSONReader(new File(this.projectPath, this.relativePath));
        MModel mModel = JSON.parseObject(model, MModel.class);
        if (mModel == null) {
            return;
        }
        System.out.println(model);

        this.cbMapType.getSelectionModel().select(mModel.getMapType());
        if (Objects.equals(mModel.getMapType(), "custom")) {
            if (!Objects.equals(mModel.getMap(), "")) {
                // 恢复绝对路径
                ((ChooseFileButton) this.btMap.getUserData()).setFile(new File(this.projectPath, mModel.getMap()));
            }
        } else if (Objects.equals(mModel.getMapType(), "default")) {
            this.cbDefaultMap.getSelectionModel().select(FileSystem.removeSuffix(mModel.getMap()));
        }

        this.cbWeatherType.getSelectionModel().select(mModel.getWeatherType());
        if (Objects.equals(mModel.getWeatherType(), "custom")) {
            if (!Objects.equals(mModel.getWeather(), "")) {
                // 恢复绝对路径
                ((ChooseFileButton) this.btWeather.getUserData()).setFile(new File(this.projectPath, mModel.getWeather()));
            }
        } else if (Objects.equals(mModel.getWeatherType(), "default")) {
            this.cbDefaultWeather.getSelectionModel().select(FileSystem.removeSuffix(mModel.getWeather()));
        }

        this.spTimeStep.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(this.timeStepMin, this.timeStepMax, mModel.getTimeStep(), 0.1));

        try {
            this.tfSimulationTime.setText(Double.toString(mModel.getSimulationTime()));
        } catch (Exception ignored) {
        }

        this.taScenarioEndTrigger.setText(mModel.getScenarioEndTrigger());

        if (!Objects.equals(mModel.getRequirementsPath(), "")) {
            // 恢复绝对路径
            ((ChooseFileButton) this.btRequirements.getUserData()).setFile(new File(this.projectPath, mModel.getRequirementsPath()));
        }

        for (MCar mCar : mModel.getCars()) {
            CarPane carPane = new CarPane(this.projectPath);
            // 设置 carPane 数据
            carPane.load(mCar);
            this.newCar(carPane);
        }
    }

    @Override
    protected void createNode() {
        super.createNode();
        this.bindKeyEvent();
        this.bindMouseEvent();

        this.gridPaneCar.setPadding(new Insets(0, 0, 8, 20));
        this.gridPaneCar.setHgap(8);
        this.gridPaneCar.setVgap(8);

        // 地图模块
        Label lbMap = new Label("map");
        this.cbMapType = new ComboBox<>(FXCollections.observableArrayList("custom", "default"));
        this.cbMapType.getSelectionModel().select(0);
        Map<String, String> mapFilter = new HashMap<>();
        mapFilter.put(FileSystem.getRegSuffix(FileSystem.Suffix.MAP), FileSystem.Suffix.MAP.toString());
        this.btMap = new ChooseFileButton(this.gridPane, this.projectPath, mapFilter).getNode();
        this.defaultMaps = SimulatorConstant.getMap();
        this.cbDefaultMap = new ComboBox<>(FXCollections.observableArrayList(this.defaultMaps));
        this.cbDefaultMap.getSelectionModel().select(0);
        this.mapPane.setHgap(8);
        this.mapPane.addRow(0, this.cbMapType, this.btMap);
        this.cbMapType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.equals(newValue, "custom")) {
                this.mapPane.getChildren().remove(this.cbDefaultMap);
                this.mapPane.add(this.btMap, 1, 0);
            } else if (Objects.equals(newValue, "default")) {
                this.mapPane.getChildren().remove(this.btMap);
                this.mapPane.add(this.cbDefaultMap, 1, 0);
            }
        });

        // 天气模块
        Label lbWeather = new Label("weather");
        this.cbWeatherType = new ComboBox<>(FXCollections.observableArrayList("custom", "default"));
        this.cbWeatherType.getSelectionModel().select(0);
        Map<String, String> weatherFilter = new HashMap<>();
        weatherFilter.put(FileSystem.getRegSuffix(FileSystem.Suffix.WEATHER), FileSystem.Suffix.WEATHER.toString());
        this.btWeather = new ChooseFileButton(this.gridPane, this.projectPath, weatherFilter).getNode();
        this.defaultWeathers = SimulatorConstant.getWeather();
        this.cbDefaultWeather = new ComboBox<>(FXCollections.observableArrayList(this.defaultWeathers));
        this.cbDefaultWeather.getSelectionModel().select(0);
        this.weatherPane.setHgap(8);
        this.weatherPane.addRow(0, this.cbWeatherType, this.btWeather);
        this.cbWeatherType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.equals(newValue, "custom")) {
                this.weatherPane.getChildren().remove(this.cbDefaultWeather);
                this.weatherPane.add(this.btWeather, 1, 0);
            } else if (Objects.equals(newValue, "default")) {
                this.weatherPane.getChildren().remove(this.btWeather);
                this.weatherPane.add(this.cbDefaultWeather, 1, 0);
            }
        });

        Label lbTimeStep = new Label("timeStep");
        this.spTimeStep = new Spinner<>(this.timeStepMin, this.timeStepMax, 0.1, 0.1);
        // 可直接输入
        this.spTimeStep.setEditable(true);
        this.spTimeStep.setPrefWidth(80);

        Label lbSimulationTime = new Label("simulationTime");
        this.tfSimulationTime = new TextField();

        Label lbScenarioTrigger = new Label("scenarioEndTrigger");
        this.taScenarioEndTrigger = new TextArea();
        this.taScenarioEndTrigger.setPrefRowCount(1);
        //自动换行
        this.taScenarioEndTrigger.setWrapText(true);

        Label lbRequirements = new Label("requirements");
        Map<String, String> requirementFilter = new HashMap<>();
        weatherFilter.put(FileSystem.getRegSuffix(FileSystem.Suffix.REQUIREMENT), FileSystem.Suffix.REQUIREMENT.toString());
        this.btRequirements = new ChooseFileButton(this.gridPane, this.projectPath, requirementFilter).getNode();

        Label lbCars = new Label("Vehicles");

        Button btNewCar = new Button("New Vehicle");
        btNewCar.setOnMouseClicked(e -> {
            this.newCar(new CarPane(this.projectPath));
        });

//        Label lbPedestrians = new Label("pedestrians");
//        Button btNewPedestrian = new Button("New Pedestrian");
//
//        Label lbObstacles = new Label("obstacles");
//        Button btNewObstacle = new Button("New Obstacle");

        this.gridPane.add(lbMap, 0, 0);
        this.gridPane.add(this.mapPane, 1, 0, 2, 1);
        this.gridPane.add(lbWeather, 0, 1);
        this.gridPane.add(this.weatherPane, 1, 1, 2, 1);
        this.gridPane.addRow(2, lbTimeStep, this.spTimeStep);
        this.gridPane.addRow(3, lbSimulationTime, this.tfSimulationTime);
        this.gridPane.addRow(4, lbScenarioTrigger, this.taScenarioEndTrigger);
        this.gridPane.addRow(5, lbCars);
        this.gridPane.addRow(6, lbRequirements, this.btRequirements);
        this.gridPane.add(this.gridPaneCar, 0, 7, 2, 1);
        this.gridPane.add(btNewCar, 0, 8, 2, 1);
//        this.gridPane.addRow(8, lbPedestrians);
//        this.gridPane.add(this.gridPanePedestrian, 0, 9, 2, 1);
//        this.gridPane.addRow(10, btNewPedestrian);
//        this.gridPane.addRow(11, lbObstacles);
//        this.gridPane.add(this.gridPaneObstacle, 0, 12, 2, 1);
//        this.gridPane.addRow(13, btNewObstacle);
    }

    /**
     * 显示一个 carPane
     *
     * @param carPane
     */
    private void newCar(CarPane carPane) {
        this.carPanes.put(this.carId++, carPane);
        this.updateCars();
    }

    private void deleteCar(int index) {
        System.out.println("delete vehicle " + index);
        CarPane carPane = this.carPanes.remove(index);
        this.updateCars();
    }

    private void updateCars() {
        List<Node[]> page = new ArrayList<>();

        int i = 0;
        for (Map.Entry<Integer, CarPane> entry : this.carPanes.entrySet()) {
            CarPane car = entry.getValue();
            if (i != 0) {
                Separator separator1 = new Separator();
                Separator separator2 = new Separator();
                page.add(new Node[]{separator1, separator2});
            }
            AnchorPane buttonWrapper = new AnchorPane();
            Button btDelete = new Button("Delete");
            btDelete.setOnAction(e -> {
                this.deleteCar(entry.getKey());
            });
            buttonWrapper.getChildren().add(btDelete);
            AnchorPane.setTopAnchor(btDelete, 0.0);
            page.add(new Node[]{car.getNode(), buttonWrapper});
            ++i;
        }

        GridPaneUtils.updateGridPane(this.gridPaneCar, page);
    }

    @Override
    public void updateSimulatorType() {
        this.defaultMaps = SimulatorConstant.getMap();
        this.cbDefaultMap.setItems(FXCollections.observableArrayList(this.defaultMaps));

        // notify CarPane
        for (Map.Entry<Integer, CarPane> entry : this.carPanes.entrySet()) {
            entry.getValue().notifyModel();;
        }
        // 更新天气选项
        this.cbDefaultWeather.setItems(FXCollections.observableArrayList(SimulatorConstant.getWeather()));
    }

    @Override
    public Node getNode() {
        return this.gridPane;
    }
}
