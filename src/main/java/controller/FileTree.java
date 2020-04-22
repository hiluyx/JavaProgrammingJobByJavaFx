package controller;

import javafx.scene.control.TreeView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import model.FileTreeItem;
import model.TreeNode;
import util.FileTreeLoader;
import util.TaskThreadPool;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileTree {
    private TreeNode imageFiles;
    private TreeView<TreeNode> treeView;
    private FileTreeItem rootTreeItem;
    private ViewerPane viewerPane;
    private List<FileTreeItem> fileTreeItems;

    public FileTree(ViewerPane viewerPane) {
        this.viewerPane = viewerPane;
        this.setRootFileTreeItem();
        TaskThreadPool.execute(new FileTreeLoader(this));
        addListener();
    }

    public void setRootFileTreeItem() {
        /*
        加载磁盘
         */
        File substitute = new File("Substitute");
        this.rootTreeItem = new FileTreeItem(substitute, substitute.getName());
        this.fileTreeItems = new ArrayList<>();
        File[] childrenDir = File.listRoots();
        for (File child : childrenDir) {
            FileTreeItem item = new FileTreeItem(child, FileTreeLoader.getDiskName(child));
            this.fileTreeItems.add(item);
            this.rootTreeItem.getChildren().add(item);
        }
        this.treeView = new TreeView<>(rootTreeItem);
        this.treeView.setShowRoot(false);
    }

    public void addListener() {
        /*
        优化，点击再加载item的图片集
         */
        this.getTreeView().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            newValue.getValue().setImages();
            viewerPane.setSelectedFolder(newValue.getValue());
        });
    }
}