package com.ecnu.adsmls.components.mutileveldirectory;

import com.ecnu.adsmls.utils.FileSystem;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;
import java.io.File;
import java.util.*;

/**
 * 树形多级目录
 */
public class MultiLevelDirectory {
    // 根文件
    private File directory;

    private TreeView<File> treeView = new TreeView<>();
    // 根项
    private TreeItem<File> rootItem;

    private Map<String, TreeItem<File>> index = new HashMap<>();

    // 上下文菜单
    private ContextMenu menu = new ContextMenu();

    public MultiLevelDirectory(File directory) {
        this.directory = directory;
        this.initialize();
    }

    private void initialize() {
        this.rootItem = new TreeItem<>(this.directory);
        this.index.put(this.directory.getAbsolutePath(), this.rootItem);
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
            else if(file.isDirectory()) {
                TreeItem<File> treeItem = new TreeItem<>(file);
                // 为了让文件夹有展开箭头（能被展开），对内部有文件的文件夹进行填充
                if(file.listFiles() != null && file.listFiles().length != 0) {
                    treeItem.getChildren().add(new TreeItem<>());
                }
                treeItems.add(treeItem);
                this.index.put(file.getAbsolutePath(), treeItem);
            }
        }
        for(File file : fileBuffer) {
            TreeItem<File> treeItem = new TreeItem<>(file);
            treeItems.add(treeItem);
            this.index.put(file.getAbsolutePath(), treeItem);
        }

        return treeItems;
    }

    private void updateNode(TreeItem<File> item) {
        item.getChildren().clear();
        item.getChildren().addAll(this.createNode(item.getValue()));
        item.setExpanded(true);
    }

    public void updateSameLevel(File file) {
        TreeItem<File> item = this.index.get(file.getAbsolutePath());
        System.out.println(item.getValue().getAbsolutePath());
        this.updateNode(item.getParent());
    }

    /**
     * 新建文件/文件夹时调用，更新显示的内容
     */
    public void newFile() {
        // 文件新建在哪取决于当前哪个节点是 active 的
        TreeItem<File> focusedItem = this.treeView.getFocusModel().getFocusedItem();
        // 如果 active 的不是文件夹，则新建在和该文件同级的位置
        if(focusedItem.getValue().isFile()) {
            focusedItem = focusedItem.getParent();
        }
        this.updateNode(focusedItem);
    }

    /**
     * 删除文件
     * @param itemDeleted 删除的文件对应的 TreeItem
     */
    public void deleteFile(TreeItem<File> itemDeleted) {
        this.updateNode(itemDeleted.getParent());
    }

    public Node getNode() {
        return this.treeView;
    }
}
