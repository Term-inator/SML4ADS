package com.ecnu.adsmls.components.mutileveldirectory;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


public class MultiLevelDirectory {
    private File directory;

    private TreeView<File> treeView = new TreeView<>();
    private TreeItem<File> rootItem;

    public MultiLevelDirectory(File directory) {
        this.directory = directory;
        this.initialize();
    }

    private void initialize() {
        this.rootItem = new TreeItem<>(this.directory);
        this.rootItem.setExpanded(true);
        this.rootItem.getChildren().addAll(this.createNode(this.directory));

        this.rootItem.addEventHandler(TreeItem.<File>branchExpandedEvent(), e -> {
            System.out.println("expand");
            e.getTreeItem().getChildren().clear();
            e.getTreeItem().getChildren().addAll(this.createNode(e.getTreeItem().getValue()));
        });

        this.rootItem.addEventHandler(TreeItem.<File>branchCollapsedEvent(), e -> {
            System.out.println("collapse");
            e.getTreeItem().getChildren().clear();
            e.getTreeItem().getChildren().add(new TreeItem<>());
        });

        this.treeView.setRoot(this.rootItem);
        this.treeView.setCellFactory(TextFieldTreeCell.forTreeView(new StringConverter<File>() {
            @Override
            public String toString(File object) {
                if(object == null) {
                    return "";
                }
                return object.getName();
            }

            @Override
            public File fromString(String string) {
                return null;
            }
        }));
    }

    private List<TreeItem<File>> createNode(File root) {
        File[] fileList = root.listFiles();
        List<File> fileBuffer = new ArrayList<>();
        List<TreeItem<File>> treeItems = new ArrayList<>();
        assert fileList != null;
        for(File file : fileList) {
            if(file.isFile()) {
                fileBuffer.add(file);
            }
            else {
                TreeItem<File> treeItem = new TreeItem<>(file);
                if(file.listFiles() != null && file.listFiles().length != 0) {
                    treeItem.getChildren().add(new TreeItem<>());
                }
                treeItems.add(treeItem);
            }
        }
        for(File file : fileBuffer) {
            TreeItem<File> treeItem = new TreeItem<>(file);
            treeItems.add(treeItem);
        }

//        PopupMenu menu = new PopupMenu(this.titledPane);
//
//        Menu newMenu = new Menu("new");
//        MenuItem newModel = new MenuItem("model");
//        MenuItem newTree = new MenuItem("tree");
//        newMenu.getItems().addAll(newModel, newTree);
//
//        menu.getItems().addAll(newMenu);

        return treeItems;
    }

    public class PopupMenu extends ContextMenu {
        public PopupMenu(Node node) {
            node.setOnContextMenuRequested(event -> {
                System.out.println(event.getTarget());
                hide();
                show(node, event.getScreenX(), event.getScreenY());
            });
        }
    }

    public Node getNode() {
        return this.treeView;
    }
}
