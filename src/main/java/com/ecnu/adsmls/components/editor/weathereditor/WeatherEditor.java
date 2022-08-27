package com.ecnu.adsmls.components.editor.weathereditor;

import com.alibaba.fastjson.JSON;
import com.ecnu.adsmls.components.ChooseFileButton;
import com.ecnu.adsmls.components.editor.Editor;
import com.ecnu.adsmls.components.editor.modeleditor.CarPane;
import com.ecnu.adsmls.model.MCar;
import com.ecnu.adsmls.model.MModel;
import com.ecnu.adsmls.model.MWeather;
import com.ecnu.adsmls.router.params.Global;
import com.ecnu.adsmls.utils.FileSystem;
import com.ecnu.adsmls.utils.SimulatorTypeObserver;
import com.ecnu.adsmls.utils.register.Function;
import com.ecnu.adsmls.utils.register.FunctionParam;
import com.ecnu.adsmls.utils.register.exception.DataTypeException;
import com.ecnu.adsmls.utils.register.exception.EmptyParamException;
import com.ecnu.adsmls.utils.register.exception.RequirementException;
import com.ecnu.adsmls.utils.register.impl.WeatherRegister;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.util.*;

// TODO 和 ModelEditor 提取父类
public class WeatherEditor extends Editor implements SimulatorTypeObserver {
    private GridPane gridPane = new GridPane();

    LinkedHashMap<String, String> weatherParams = new LinkedHashMap<>();


    public WeatherEditor(String projectPath, File file) {
        super(projectPath, file);
        this.createNode();
    }

    @Override
    public void check() throws EmptyParamException, RequirementException, DataTypeException {
        this.weatherParams.clear();

        Function weatherFunction = WeatherRegister.getFunction(Global.simulatorType.value);
        String locationParamName = "";
        String locationParamValue = "";
        for (Node node : this.gridPane.getChildren()) {
            if (node instanceof Label) {
                locationParamName = ((Label) node).getText();
            } else if (node instanceof TextField) {
                locationParamValue = ((TextField) node).getText();
                this.weatherParams.put(locationParamName, locationParamValue);
                weatherFunction.updateContext(locationParamName, locationParamValue);
            }
        }
        weatherFunction.check();
    }

    @Override
    public void save() {
        String weather = FileSystem.JSONReader(new File(this.projectPath, this.relativePath));
        MWeather mWeather = JSON.parseObject(weather, MWeather.class);
        if (mWeather == null) {
            mWeather = new MWeather();
        }
        System.out.println(weather);

        String weatherParamName = "";
        String weatherParamValue = "";
        for (Node node : this.gridPane.getChildren()) {
            if (node instanceof Label) {
                weatherParamName = ((Label) node).getText();
            } else if (node instanceof TextField) {
                weatherParamValue = ((TextField) node).getText();
                this.weatherParams.put(weatherParamName, weatherParamValue);
            }
        }
        mWeather.setWeatherParams(this.weatherParams);

        weather = JSON.toJSONString(mWeather);
        System.out.println(weather);
        FileSystem.JSONWriter(new File(this.projectPath, this.relativePath), weather);
    }

    @Override
    public void load() {
        String weather = FileSystem.JSONReader(new File(this.projectPath, this.relativePath));
        MWeather mWeather = JSON.parseObject(weather, MWeather.class);
        if (mWeather == null) {
            return;
        }
        System.out.println(weather);

        String weatherParamName = "";
        String weatherParamValue = "";
        for (Node node : this.gridPane.getChildren()) {
            if (node instanceof Label) {
                weatherParamName = ((Label) node).getText();
            } else if (node instanceof TextField) {
                weatherParamValue = mWeather.getWeatherParams().get(weatherParamName);
                ((TextField) node).setText(weatherParamValue);
            }
        }
    }

    @Override
    protected void createNode() {
        this.gridPane.setPrefWidth(800);
        this.gridPane.setPrefWidth(800);
        this.gridPane.setPadding(new Insets(30, 40, 30, 40));
        this.gridPane.setHgap(8);
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

        System.out.println(Global.simulatorType.value);
        Function weatherFunction = WeatherRegister.getFunction(Global.simulatorType.value);
        // 生成界面
        int row = 0;
        for (FunctionParam param : weatherFunction.getParams()) {
            Label lbParamName = new Label(param.getParamName());
            TextField tfParamValue = new TextField();
            this.gridPane.addRow(row++, lbParamName, tfParamValue);
        }
    }

    @Override
    public void updateSimulatorType() {
        this.gridPane.getChildren().clear();
        this.createNode();
        this.load();
    }

    @Override
    public Node getNode() {
        return this.gridPane;
    }
}
