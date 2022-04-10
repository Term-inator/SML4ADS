package com.ecnu.adsmls.components.editor.modeleditor;

import com.alibaba.fastjson.JSON;
import com.ecnu.adsmls.components.ChooseFileButton;
import com.ecnu.adsmls.components.editor.Editor;
import com.ecnu.adsmls.model.MCar;
import com.ecnu.adsmls.model.MModel;
import com.ecnu.adsmls.utils.EmptyParamException;
import com.ecnu.adsmls.utils.FileSystem;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ModelEditor extends Editor {
    private GridPane gridPane = new GridPane();

    // 模拟器类型
    private ComboBox<String> cbSimulatorType;
    // 地图文件
    private Node btMap;
    // 天气
    private ComboBox<String> cbWeather;
    
    private double timeStepMin = 0.1;
    private double timeStepMax = 10.0;
    private Spinner<Double> spTimeStep;
    // 总模拟时间 是 time step 的倍数
    private TextField tfSimulationTime;
    
    private Node btSource;

    private GridPane gridPaneCar = new GridPane();
    // 临时 id ，用于删除
    private int carId = 0;
    private Map<Integer, CarPane> carPanes = new LinkedHashMap<>();

    private GridPane gridPanePedestrian = new GridPane();
    //    private Map<Integer, PedestrianPane> pedestrianPanes = new LinkedHashMap<>();
    private GridPane gridPaneObstacle = new GridPane();
//    private Map<Integer, ObstaclePane> obstaclePanes = new LinkedHashMap<>();


    public ModelEditor(String projectPath, File file) {
        super(projectPath, file);
        this.createNode();
    }

    private void updateGridPane(GridPane gridPane, List<Node[]> page) {
        gridPane.getChildren().clear();
        for (int r = 0; r < page.size(); ++r) {
            gridPane.addRow(r, page.get(r));
        }
    }

    @Override
    public boolean check() {
        if(this.tfSimulationTime.getText().isEmpty()) {
            return false;
        }
        for(Map.Entry<Integer, CarPane> entry: this.carPanes.entrySet()) {
            entry.getValue().check();
        }
        return true;
    }

    // 由于关闭自动保存，其他在打开 Editor 后修改的内容会在关闭时被覆盖，所以 save 前要先 load
    @Override
    public void save() {
        String model = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(this.projectPath, this.relativePath)), StandardCharsets.UTF_8));
            model = br.readLine();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MModel mModel = JSON.parseObject(model, MModel.class);
        if (mModel == null) {
            mModel = new MModel();
        }
        System.out.println(model);

        mModel.setSimulatorType(this.cbSimulatorType.getValue());

        File map = ((ChooseFileButton) this.btMap.getUserData()).getFile();
        if (map == null) {
            mModel.setMap("");
        } else {
            // 转换成相对路径
            String path = map.getAbsolutePath();
            String relativePath = FileSystem.getRelativePath(this.projectPath, path);
            mModel.setMap(relativePath);
        }

        mModel.setWeather(this.cbWeather.getValue());
        mModel.setTimeStep(this.spTimeStep.getValue());

        // TODO simulation time 是 time step 的倍数
        mModel.setSimulationTime(Double.parseDouble(this.tfSimulationTime.getText()));


        File source = ((ChooseFileButton) this.btSource.getUserData()).getFile();
        if (source == null) {
            mModel.setSource("");
        } else {
            // 转换成相对路径
            String path = source.getAbsolutePath();
            String relativePath = FileSystem.getRelativePath(this.projectPath, path);
            mModel.setSource(relativePath);
        }

        List<MCar> cars = new ArrayList<>();
        for (Map.Entry<Integer, CarPane> entry : this.carPanes.entrySet()) {
            CarPane carPane = entry.getValue();
            cars.add(carPane.save());
        }
        mModel.setCars(cars);
        model = JSON.toJSONString(mModel);
        System.out.println(model);
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(this.projectPath, this.relativePath), false), StandardCharsets.UTF_8));
            bw.write(model);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        String model = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(this.projectPath, this.relativePath)), StandardCharsets.UTF_8));
            model = br.readLine();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MModel mModel = JSON.parseObject(model, MModel.class);
        if (mModel == null) {
            return;
        }
        System.out.println(model);

        this.cbSimulatorType.getSelectionModel().select(mModel.getSimulatorType());
        if (!Objects.equals(mModel.getMap(), "")) {
            // 恢复绝对路径
            ((ChooseFileButton) this.btMap.getUserData()).setFile(new File(this.projectPath, mModel.getMap()));
        }
        this.cbWeather.getSelectionModel().select(mModel.getWeather());
        this.spTimeStep.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(this.timeStepMin, this.timeStepMax, mModel.getTimeStep(), 0.1));
        this.tfSimulationTime.setText(Double.toString(mModel.getSimulationTime()));
        if (!Objects.equals(mModel.getSource(), "")) {
            // 恢复绝对路径
            ((ChooseFileButton) this.btSource.getUserData()).setFile(new File(this.projectPath, mModel.getSource()));
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
        this.gridPane.setPrefWidth(800);
        this.gridPane.setPrefWidth(800);
        this.gridPane.setPadding(new Insets(30, 40, 30, 40));
        this.gridPane.setVgap(8);

        this.gridPane.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            System.out.println(e);
            if (e.isControlDown() && e.getCode() == KeyCode.S) {
                this.save();
            }
        });
        this.gridPane.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.gridPane.requestFocus();
        });

        this.gridPaneCar.setPadding(new Insets(0, 0, 8, 20));
        this.gridPaneCar.setHgap(8);
        this.gridPaneCar.setVgap(8);

        Label lbSimulatorType = new Label("Simulator Type");
        String[] simulators = {"Carla", "lgsvl"};
        this.cbSimulatorType = new ComboBox<>(FXCollections.observableArrayList(simulators));
        this.cbSimulatorType.getSelectionModel().select(0);

        Label lbMap = new Label("Map");
        // 限定选择 *.xodr 文件
        Map<String, String> mapFilter = new HashMap<>();
        mapFilter.put(FileSystem.getRegSuffix(FileSystem.Suffix.MAP), FileSystem.Suffix.MAP.toString());
        this.btMap =  new ChooseFileButton(this.gridPane, this.projectPath, mapFilter).getNode();

        Label lbWeather = new Label("Weather");
        // TODO 提供不同仿真器支持的天气选项
        String[] weathers = {"clear", "rainy", "foggy"};
        this.cbWeather = new ComboBox<>(FXCollections.observableArrayList(weathers));
        this.cbWeather.getSelectionModel().select(0);

        Label lbSource = new Label("Actor Source");
        // 限定选择 *.model 文件
        Map<String, String> actorFilter = new HashMap<>();
        actorFilter.put(FileSystem.getRegSuffix(FileSystem.Suffix.MODEL), FileSystem.Suffix.MODEL.toString());
        this.btSource = new ChooseFileButton(this.gridPane, this.projectPath, actorFilter).getNode();

        Label lbTimeStep = new Label("Time Step");
        this.spTimeStep = new Spinner<>(this.timeStepMin, this.timeStepMax, 0.1, 0.1);
        // 可直接输入
        this.spTimeStep.setEditable(true);
        this.spTimeStep.setPrefWidth(80);

        Label lbSimulationTime = new Label("Simulation Time");
        this.tfSimulationTime = new TextField();

        Label lbCars = new Label("Cars");

        Button btNewCar = new Button("New Car");
        btNewCar.setOnMouseClicked(e -> {
            this.newCar(new CarPane(this.projectPath));
        });

        Label lbPedestrians = new Label("Pedestrians");
        Button btNewPedestrian = new Button("New Pedestrian");

        Label lbObstacles = new Label("Obstacles");
        Button btNewObstacle = new Button("New Obstacle");

        this.gridPane.addRow(0, lbSimulatorType, this.cbSimulatorType);
        this.gridPane.addRow(1, lbMap, this.btMap);
        this.gridPane.addRow(2, lbWeather, this.cbWeather);
        this.gridPane.addRow(3, lbSource, this.btSource);
        this.gridPane.addRow(4, lbTimeStep, this.spTimeStep);
        this.gridPane.addRow(5, lbSimulationTime, this.tfSimulationTime);
        this.gridPane.addRow(6, lbCars);
        this.gridPane.add(this.gridPaneCar, 0, 7, 2, 1);
        this.gridPane.addRow(8, btNewCar);
        this.gridPane.addRow(9, lbPedestrians);
        this.gridPane.add(this.gridPanePedestrian, 0, 10, 2, 1);
        this.gridPane.addRow(11, btNewPedestrian);
        this.gridPane.addRow(12, lbObstacles);
        this.gridPane.add(this.gridPaneObstacle, 0, 13, 2, 1);
        this.gridPane.addRow(14, btNewObstacle);
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
        System.out.println("delete car" + index);
        this.carPanes.remove(index);
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

        this.updateGridPane(this.gridPaneCar, page);
    }

    @Override
    public Node getNode() {
        return this.gridPane;
    }
}
