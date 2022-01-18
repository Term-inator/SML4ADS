package com.ecnu.adsmls.views.codepage;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Flow;

public class CodePageController implements Initializable {
    @FXML
    private AnchorPane rootLayout;
    @FXML
    private MenuBar menuBar;
    @FXML
    private TabPane tabPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("init");
        this.initMenu();
    }

    private void initMenu() {
        ObservableList<Menu> menus = menuBar.getMenus();
        Queue<Menu> queue = new LinkedList(menus);
        while(!queue.isEmpty()) {
            Menu menu = queue.poll();
            ObservableList<MenuItem> menuItems = menu.getItems();
            for(MenuItem menuItem : menuItems) {
                if(menuItem instanceof Menu) {
                    queue.offer((Menu) menuItem);
                }
                else {
                    String menuItemName = menuItem.getText();
                    if(Objects.equals(menuItemName, "Model")) {
                        menuItem.setOnAction(this::onNewModelClick);
                    }
                    else if(Objects.equals(menuItemName, "Tree")) {
                        menuItem.setOnAction(this::onNewTreeClick);
                    }
                }
            }
        }
    }

    @FXML
    protected void onRun() {
        System.out.println("Run");
    }

    private void onNewModelClick(ActionEvent event) {
        System.out.println("Model");
        Tab tab = new Tab("untitled.model");
        tab.setOnClosed(e -> {
            System.out.println(tab.getText() + " closed");
        });

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        GridPane gridPane = new GridPane();
        gridPane.setPrefWidth(800);
        gridPane.setPrefWidth(800);
        gridPane.setPadding(new Insets(30, 0, 0, 40));
//        gridPane.setBackground(new Background(new BackgroundFill(Color.rgb(255, 0, 0), null, null)));

        Label lbMap = new Label("Map: ");
        FlowPane flowPane0 = new FlowPane();
        Button btMap = new Button("Choose File");
        Label lbFileName = new Label();
        flowPane0.getChildren().addAll(lbFileName, btMap);
        btMap.setOnMouseClicked(e -> {
            Stage stage = (Stage) rootLayout.getScene().getWindow();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            File file = fileChooser.showOpenDialog(stage);
            lbFileName.setText(file.getName());
        });

        Label lbWeather = new Label("Weather: ");
        TextField tfWeather = new TextField();

        gridPane.addRow(0, lbMap, flowPane0);
        gridPane.addRow(1, lbWeather, tfWeather);

        scrollPane.setContent(gridPane);
        tab.setContent(scrollPane);
        tabPane.getTabs().add(tab);
    }

    private void onNewTreeClick(ActionEvent event) {
        System.out.println("Tree");
        Tab tab = new Tab("untitled.tree");
        tab.setOnClosed(e -> {
            System.out.println(tab.getText() + " closed");
        });

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        SplitPane splitPane = new SplitPane();
        splitPane.setPrefWidth(800);
        splitPane.setPrefWidth(800);
        splitPane.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 255), null, null)));

        scrollPane.setContent(splitPane);

        tab.setContent(scrollPane);
        tabPane.getTabs().add(tab);
    }
}