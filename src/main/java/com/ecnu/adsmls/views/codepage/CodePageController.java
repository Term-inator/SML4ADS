package com.ecnu.adsmls.views.codepage;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.*;

public class CodePageController implements Initializable {
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
        tab.setOnClosed(event1 -> {
            System.out.println(tab.getText() + " closed");
        });

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        GridPane gridPane = new GridPane();
        gridPane.setPrefWidth(800);
        gridPane.setPrefWidth(800);
        gridPane.setBackground(new Background(new BackgroundFill(Color.rgb(255, 0, 0), null, null)));

        scrollPane.setContent(gridPane);

        tab.setContent(scrollPane);
        tabPane.getTabs().add(tab);
    }

    private void onNewTreeClick(ActionEvent event) {
        System.out.println("Tree");
        Tab tab = new Tab("untitled.tree");
        tab.setOnClosed(event1 -> {
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