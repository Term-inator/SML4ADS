package com.ecnu.adsmls.utils;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;

import java.util.List;

public class GridPaneUtils {
    public static void updateGridPane(GridPane gridPane, List<Node[]> page) {
        gridPane.getChildren().clear();
        for (int r = 0; r < page.size(); ++r) {
            gridPane.addRow(r, page.get(r));
        }
    }
}
