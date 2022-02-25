package com.ecnu.adsmls.components.mutileveldirectory;

import com.ecnu.adsmls.utils.FileSystem;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 树形多级目录
 */
public class MultiLevelDirectory {
    // 根文件
    private File directory;

    private TreeView<File> treeView = new TreeView<>();
    // 根项
    private TreeItem<File> rootItem;

    // 上下文菜单
    private ContextMenu menu = new ContextMenu();

    public MultiLevelDirectory(File directory) {
        this.directory = directory;
        this.initialize();
    }

    private void initialize() {
        this.rootItem = new TreeItem<>(this.directory);
        this.rootItem.setExpanded(true);
        this.rootItem.getChildren().addAll(this.createNode(this.directory));

        // 监听展开事件
        this.rootItem.addEventHandler(TreeItem.<File>branchExpandedEvent(), e -> {
            System.out.println("expand");
            e.getTreeItem().getChildren().clear();
            // 动态加载，节省资源
            e.getTreeItem().getChildren().addAll(this.createNode(e.getTreeItem().getValue()));
        });

        // 监听关闭事件
        this.rootItem.addEventHandler(TreeItem.<File>branchCollapsedEvent(), e -> {
            System.out.println("collapse");
            // 减少内存消耗
            e.getTreeItem().getChildren().clear();
            e.getTreeItem().getChildren().add(new TreeItem<>());
        });

        // 设置根项
        this.treeView.setRoot(this.rootItem);
        // 将 File 对象的 name 显示在界面上
        this.treeView.setCellFactory(TextFieldTreeCell.forTreeView(new StringConverter<File>() {
            @Override
            public String toString(File object) {
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

        // 弹出菜单
        this.treeView.setOnContextMenuRequested(event -> {
            this.menu.hide();
            this.menu.show(this.treeView, event.getScreenX(), event.getScreenY());
        });
    }

    /**
     * 为该组件设置菜单内容，外部调用
     * @param menuItems 菜单项
     */
    public void setMenu(List<MenuItem> menuItems) {
        for(MenuItem menuItem :menuItems) {
            this.menu.getItems().add(menuItem);
        }
        System.out.println(this.menu.getItems());
    }

    public TreeView<File> getTreeView() {
        return treeView;
    }

    private List<TreeItem<File>> createNode(File root) {
        File[] fileList = root.listFiles();
        // fileList 默认字典序，用 Buffer 修改成文件夹优先
        List<File> fileBuffer = new ArrayList<>();
        List<TreeItem<File>> treeItems = new ArrayList<>();
        assert fileList != null;
        for(File file : fileList) {
            if(file.isFile()) {
                fileBuffer.add(file);
            }
            else {
                TreeItem<File> treeItem = new TreeItem<>(file);
                treeItems.add(treeItem);
            }
        }
        for(File file : fileBuffer) {
            TreeItem<File> treeItem = new TreeItem<>(file);
            treeItems.add(treeItem);
        }

        return treeItems;
    }

    /**
     * 新建文件时调用，更新显示的内容
     */
    public void updateNode() {
        // 文件新建在哪取决于当前哪个节点是 active 的
        TreeItem<File> focusedItem = this.treeView.getFocusModel().getFocusedItem();
        File directory = this.treeView.getFocusModel().getFocusedItem().getValue();
        // 如果 active 的不是文件夹，则新建在和该文件同级的位置
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
