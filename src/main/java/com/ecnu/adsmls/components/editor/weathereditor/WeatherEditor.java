package com.ecnu.adsmls.components.editor.weathereditor;

import com.ecnu.adsmls.components.editor.Editor;
import com.ecnu.adsmls.utils.register.Function;
import com.ecnu.adsmls.utils.register.FunctionParam;
import com.ecnu.adsmls.utils.register.exception.DataTypeException;
import com.ecnu.adsmls.utils.register.exception.EmptyParamException;
import com.ecnu.adsmls.utils.register.exception.RequirementException;
import com.ecnu.adsmls.utils.register.impl.LocationRegister;
import com.ecnu.adsmls.utils.register.impl.WeatherRegister;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.util.LinkedHashMap;

// TODO 和 ModelEditor 提取父类
public class WeatherEditor extends Editor {
    private GridPane gridPane = new GridPane();

    LinkedHashMap<String, String> weatherParams = new LinkedHashMap<>();


    public WeatherEditor(String projectPath, File file) {
        super(projectPath, file);
        this.createNode();
    }

    @Override
    public void check() throws EmptyParamException, RequirementException, DataTypeException {
        this.weatherParams.clear();
        // TODO refactor
        Function weatherFunction = WeatherRegister.getFunction("CARLA");
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

    }

    @Override
    public void load() {

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

        // TODO refactor
        Function weatherFunction = WeatherRegister.getFunction("CARLA");
        // 生成界面
        int row = 0;
        for (FunctionParam param : weatherFunction.getParams()) {
            Label lbParamName = new Label(param.getParamName());
            TextField tfParamValue = new TextField();
            this.gridPane.addRow(row++, lbParamName, tfParamValue);
        }
    }

    @Override
    public Node getNode() {
        return this.gridPane;
    }
}
