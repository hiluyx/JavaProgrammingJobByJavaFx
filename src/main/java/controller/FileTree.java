package controller;

import javafx.fxml.FXML;

import java.io.File;

import javafx.scene.control.TreeView;
import lombok.Getter;
import lombok.Setter;
import model.FileTreeItem;
import model.TreeNode;
import util.FileTreeLoader;
import util.TaskThreadPool;

@Getter
@Setter
public class FileTree {
    private TreeNode imageFiles;
    @FXML
    private TreeView<TreeNode> treeView;
    private FileTreeItem rootTreeItem;
    private ViewerPane viewerPane;

    public FileTree(ViewerPane viewerPane) {
        this.viewerPane = viewerPane;
        this.setRootFileTreeItem();
        TaskThreadPool.execute(new FileTreeLoader(this));
        addListener();
    }

    public void setRootFileTreeItem() {
        File substitute = new File("Substitute");
        this.rootTreeItem = new FileTreeItem(substitute, substitute.getName(), true);
        this.treeView = new TreeView<>(rootTreeItem);
        this.treeView.setShowRoot(false);
    }

    public void addListener() {
        this.getTreeView().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            viewerPane.setSelectedFolder(newValue.getValue());
        });
    }
}