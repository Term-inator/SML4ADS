package com.ecnu.adsmls.components.editor.modeleditor;

import com.alibaba.fastjson.JSON;
import com.ecnu.adsmls.components.ChooseFileButton;
import com.ecnu.adsmls.components.editor.Editor;
import com.ecnu.adsmls.components.editor.treeeditor.TreeComponent;
import com.ecnu.adsmls.model.MCar;
import com.ecnu.adsmls.model.MModel;
import com.ecnu.adsmls.model.MTree;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModelEditor extends Editor {
    private GridPane gridPane = new GridPane();
    private List<Node[]> staticPage = new ArrayList<>();

    private Node btMap;
    private ComboBox<String> cbWeather;
    private int timeStepMin = 1;
    private int timeStepMax = 10;
    private Spinner<Integer> spTimeStep;
    private Node btSource;

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
        MModel mModel = new MModel();
        File map = ((ChooseFileButton) this.btMap.getUserData()).getFile();
        if(map == null) {
            mModel.setMap("");
        }
        else {
            mModel.setMap(map.getAbsolutePath());
        }

        mModel.setWeather(this.cbWeather.getValue());
        mModel.setTimeStep(this.spTimeStep.getValue());

        File source = ((ChooseFileButton) this.btSource.getUserData()).getFile();
        if(source == null) {
            mModel.setSource("");
        }
        else {
            mModel.setMap(source.getAbsolutePath());
        }

        List<MCar> cars = new ArrayList<>();
        for(CarPane carPane : this.carPanes) {
            cars.add(carPane.getModel());
        }
        mModel.setCars(cars);
        String model = JSON.toJSONString(mModel);
        String path = this.directory + "/" + this.filename;
        System.out.println(model);
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path,false), StandardCharsets.UTF_8));
            bw.write(model);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        String model = null;
        String path = this.directory + "/" + this.filename;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
            model = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MModel mModel = JSON.parseObject(model, MModel.class);
        if(mModel == null) {
            return;
        }
        System.out.println(model);

        if(!Objects.equals(mModel.getMap(), "")) {
            ((ChooseFileButton) this.btMap.getUserData()).setFile(new File(mModel.getMap()));
        }
        this.cbWeather.getSelectionModel().select(mModel.getWeather());
        this.spTimeStep.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(this.timeStepMin, this.timeStepMax, mModel.getTimeStep()));
        if(!Objects.equals(mModel.getSource(), "")) {
            ((ChooseFileButton) this.btSource.getUserData()).setFile(new File(mModel.getSource()));
        }
        for(MCar mCar : mModel.getCars()) {
            CarPane carPane = new CarPane();
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
            if(e.isControlDown() && e.getCode() == KeyCode.S) {
                this.save();
            }
        });
        this.gridPane.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            this.gridPane.requestFocus();
        });

        this.gridPaneCar.setPadding(new Insets(0, 0, 8, 20));
        this.gridPaneCar.setHgap(8);
        this.gridPaneCar.setVgap(8);

        Label lbMap = new Label("Map: ");
        this.btMap = new ChooseFileButton(gridPane).getNode();

        Label lbWeather = new Label("Weather: ");
        String[] weathers = {"clear", "rainy", "foggy"};
        this.cbWeather = new ComboBox<>(FXCollections.observableArrayList(weathers));
        this.cbWeather.getSelectionModel().select(0);

        Label lbTimeStep = new Label("Time Step: ");
        this.spTimeStep = new Spinner<>(this.timeStepMin, this.timeStepMax, 1);
        this.spTimeStep.setPrefWidth(80);

        Label lbSource = new Label("Actor Source: ");
        this.btSource = new ChooseFileButton(gridPane).getNode();

        Label lbCars = new Label("Cars: ");

        Button btNewCar = new Button("New Car");
        btNewCar.setOnMouseClicked(e -> {
            this.newCar(new CarPane());
        });

        Label lbPedestrians = new Label("Pedestrians: ");
        Button btNewPedestrian = new Button("New Pedestrian");

        Label lbObstacles = new Label("Obstacles: ");
        Button btNewObstacle = new Button("New Obstacle");

        this.gridPane.addRow(0, lbMap, this.btMap);
        this.gridPane.addRow(1, lbWeather, this.cbWeather);
        this.gridPane.addRow(2, lbSource, this.btSource);
        this.gridPane.addRow(3, lbTimeStep, this.spTimeStep);
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

    public void newCar(CarPane carPane) {
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
