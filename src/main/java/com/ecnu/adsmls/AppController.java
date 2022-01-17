package com.ecnu.adsmls;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

import java.net.URL;
import java.util.*;

public class AppController implements Initializable {
    @FXML
    private MenuBar menuBar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<Menu> menus = menuBar.getMenus();
        Queue<Menu> queue = new LinkedList(menus);
        System.out.println("Run");
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

    }

    private void onNewModelClick(ActionEvent event) {
        System.out.println("Model");
    }

    private void onNewTreeClick(ActionEvent event) {
        System.out.println("Tree");
    }
}