package controller;

import javafx.scene.control.TreeView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import model.FileTreeItem;
import model.TreeNode;
import util.FileTreeLoader;
import util.TaskThreadPools;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileTree {
    private TreeView<TreeNode> treeView;
    private FileTreeItem rootTree;
    private ViewerPane viewerPane;
    private List<FileTreeItem> rootFileTreeItems;

    public FileTree(ViewerPane viewerPane) {
        this.viewerPane = viewerPane;
        this.setRootFileTreeItems();
        TaskThreadPools.execute(new FileTreeLoader(this));
        addListener();
    }

    public void setRootFileTreeItems() {
        /*
        加载磁盘
         */
        File substitute = new File("Substitute");
        this.rootTree = new FileTreeItem(substitute, substitute.getName());
        this.rootFileTreeItems = new ArrayList<>();
        File[] childrenDir = File.listRoots();
        for (File child : childrenDir) {
            FileTreeItem item = new FileTreeItem(child, FileTreeLoader.getDiskName(child));
            this.rootFileTreeItems.add(item);
            this.rootTree.getChildren().add(item);
        }
        this.treeView = new TreeView<>(rootTree);
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