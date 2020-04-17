package controller;

import javafx.fxml.FXML;

import java.io.File;
import javax.swing.filechooser.FileSystemView;

import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import lombok.Getter;
import lombok.Setter;
import model.FileTreeItem;
import model.TreeNode;

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
        addListener();

        for (TreeItem<TreeNode> fileTreeItem : this.rootTreeItem.getChildren()) {
            ((FileTreeItem) fileTreeItem).loadChildren();
        }
    }

    public void setRootFileTreeItem() {
        File substitute = new File("Substitute");
        this.rootTreeItem = new FileTreeItem(substitute, substitute.getName(), true);
        this.rootTreeItem.loadChildren();
        this.treeView = new TreeView<>(rootTreeItem);
        this.treeView.setShowRoot(false);
//        this.treeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void addListener() {
        this.getTreeView().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            viewerPane.setSelectedFolder(newValue.getValue());
        });
    }
}