package com.ecnu.adsmls.components.editor.weathereditor;

import com.alibaba.fastjson.JSON;
import com.ecnu.adsmls.components.editor.FormEditor;
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
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.File;
import java.util.*;


public class WeatherEditor extends FormEditor implements SimulatorTypeObserver {
    LinkedHashMap<String, String> weatherParams = new LinkedHashMap<>();

    // 错误信息
    private StringBuilder errorMsg = new StringBuilder();

    public WeatherEditor(String projectPath, File file) {
        super(projectPath, file);
        this.createNode();
    }

    @Override
    public void check() throws EmptyParamException, RequirementException, DataTypeException {
        this.weatherParams.clear();

        Function weatherFunction = WeatherRegister.getFunction(Global.simulatorType.value);
        String weatherParamName = "";
        String weatherParamValue = "";
        for (Node node : this.gridPane.getChildren()) {
            if (node instanceof Label) {
                weatherParamName = ((Label) node).getText();
            } else if (node instanceof TextField) {
                weatherParamValue = ((TextField) node).getText();
                this.weatherParams.put(weatherParamName, weatherParamValue);
                weatherFunction.updateContext(weatherParamName, weatherParamValue);
            }
        }
        weatherFunction.check();
    }

    @Override
    public void save() {
        this.errorMsg = new StringBuilder();
        try {
            this.check();
        } catch (Exception e) {
            this.errorMsg.append(e.getMessage()).append('\n');
        }

        String weather = FileSystem.JSONReader(new File(this.projectPath, this.relativePath));
        MWeather mWeather = JSON.parseObject(weather, MWeather.class);
        if (mWeather == null) {
            mWeather = new MWeather();
        }
        System.out.println(weather);

        mWeather.setErrMsg(this.errorMsg.toString());

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
        super.createNode();
        this.bindKeyEvent();
        this.bindMouseEvent();

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
