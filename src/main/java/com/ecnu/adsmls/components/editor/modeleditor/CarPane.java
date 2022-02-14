package com.ecnu.adsmls.components.editor.modeleditor;

import com.ecnu.adsmls.components.ChooseFileButton;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class CarPane {
    GridPane gridPane = new GridPane();
    private List<Node[]> staticPage = new ArrayList<>();

    public CarPane() {
        this.createNode();
    }

    public List<Node[]> getStaticPage() {
        return staticPage;
    }

    private void updateGridPane(GridPane gridPane, List<Node[]> page) {
        gridPane.getChildren().clear();
        for(int r = 0; r < page.size(); ++r) {
            gridPane.addRow(r, page.get(r));
        }
    }

    private void createNode() {
        this.gridPane.setVgap(8);
        this.gridPane.setHgap(8);

        Label lbName = new Label("name: ");
        TextField tfName = new TextField();
        Button btDelete = new Button("Delete");

        Label lbModel = new Label("model: ");
        String[] models = {"random", "vehicle.audi.a2"};
        ComboBox cbModel = new ComboBox(FXCollections.observableArrayList(models));
        cbModel.getSelectionModel().select(0);

        Label lbMaxSpeed = new Label("max speed: ");
        TextField tfMaxSpeed = new TextField();

        Label lbInitSpeed = new Label("initial speed: ");
        TextField tfInitSpeed = new TextField();

        // TODO macAcc?
        /** TODO location
         *  1. 确定lane (filter函数 / 3 个 id)
         *  2. 数值 距离 lane 起始位置的偏移量
         */
        Label lbLocation = new Label("location: ");

        Label lbHeading = new Label("heading: ");
        ComboBox cbHeading = new ComboBox(FXCollections.observableArrayList("same", "opposite"));
        cbHeading.getSelectionModel().select(0);

        Label lbRoadDeviation = new Label("road deviation: ");
        TextField tfRoadDeviation = new TextField();

        Label lbDynamic = new Label("Dynamic: ");
        String[] trees = {"test.tree"};
        Node btDynamic = new ChooseFileButton(this.gridPane).getNode();

        this.staticPage.add(new Node[] {lbName, tfName, btDelete});
        this.staticPage.add(new Node[] {lbModel, cbModel});
        this.staticPage.add(new Node[] {lbMaxSpeed, tfMaxSpeed});
        this.staticPage.add(new Node[] {lbInitSpeed, tfInitSpeed});
        this.staticPage.add(new Node[] {lbLocation});
        this.staticPage.add(new Node[] {lbHeading, cbHeading});
        this.staticPage.add(new Node[] {lbRoadDeviation, tfRoadDeviation});
        this.staticPage.add(new Node[] {lbDynamic, btDynamic});

        this.updateGridPane(this.gridPane, this.staticPage);
    }

    public Node getNode() {
        return this.gridPane;
    }
}
