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

/**
 * @Author Hi lu
 * @Date 2020/4/15
 */
@Getter
@Setter
public class FileTree {
    private TreeView<TreeNode> treeView;
    private FileTreeItem rootTree;
    private ViewerPane viewerPane;
    private List<FileTreeItem> rootFileTreeItems;
    private FileTreeItem cloudAlbum;//云相册

    public FileTree(ViewerPane viewerPane) {
        this.viewerPane = viewerPane;
        this.setRootFileTreeItems();
        TaskThreadPools.execute(new FileTreeLoader(this));
        addListener();
    }

    public void setRootFileTreeItems() {
        /*
         * 加载系统磁盘和云相册
         */
        File substitute = new File("Substitute");
        this.setCloudAlum();
        this.rootTree = new FileTreeItem(substitute, substitute.getName());
        this.rootFileTreeItems = new ArrayList<>();
        File[] disks = File.listRoots();
        for (File disk : disks) {
            /*
             * 在这里可以忽略加载disks[0]（C盘）以免性能消耗过大
             */
            FileTreeItem item = new FileTreeItem(disk, FileTreeLoader.getDiskName(disk));
            this.rootFileTreeItems.add(item);
            this.rootTree.getChildren().add(item);
        }
        this.rootTree.getChildren().add(this.cloudAlbum);
        this.treeView = new TreeView<>(rootTree);
        this.treeView.setShowRoot(false);
    }

    public void addListener() {
        /*
         * 优化，点击再加载item的图片集
         */
        this.getTreeView().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            newValue.getValue().setImages();
            ViewerPane.setSelectedFolder(newValue.getValue());
//            viewerPane.getToolBar().setSelectedFolder(newValue.getValue());
        });
    }

    /*
     * 添加一个触发器，点击cloudAlbum的时候连接网络加载图片
     */
    public void setCloudAlum(){
        File cloudAlbumFile = new File(System.getProperty("user.dir")+"/cloudAlbum");
        if(cloudAlbumFile.mkdirs()){
            this.cloudAlbum = new FileTreeItem(cloudAlbumFile,cloudAlbumFile.getName());
//            this.cloudAlbum.addEventHandler();
        }
    }
}