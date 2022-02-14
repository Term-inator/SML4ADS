package com.ecnu.adsmls.components.mutileveldirectory;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.util.StringConverter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MultiLevelDirectory {
    private File directory;

    private TreeView<File> treeView = new TreeView<>();
    private TreeItem<File> rootItem;

    private ContextMenu menu = new ContextMenu();

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

        this.treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(newValue);
        });

        this.treeView.setOnContextMenuRequested(event -> {
            this.menu.hide();
            this.menu.show(this.treeView, event.getScreenX(), event.getScreenY());
        });
    }

    public void setMenu(List<MenuItem> menuItems) {
        for(MenuItem menuItem :menuItems) {
            this.menu.getItems().add(menuItem);
        }
        System.out.println(this.menu.getItems());
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

        return treeItems;
    }

    public void updateNode() {
        TreeItem<File> focusedItem = this.treeView.getFocusModel().getFocusedItem();
        File directory = this.treeView.getFocusModel().getFocusedItem().getValue();
        if(directory.isFile()) {
            directory = directory.getParentFile();
            focusedItem = focusedItem.getParent();
        }
        focusedItem.getChildren().clear();
        focusedItem.getChildren().addAll(this.createNode(directory));
    }

    public Node getNode() {
        return this.treeView;
    }
}
