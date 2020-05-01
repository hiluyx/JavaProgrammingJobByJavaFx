package controller;

import javafx.scene.control.TreeView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.FileTreeItem;
import model.TreeNode;
import util.fileUtils.FileTreeLoader;
import util.TaskThreadPools;

import lombok.Getter;
import lombok.Setter;
import util.httpUtils.CloudImageNote;

/**
 * @author Hi lu
 * @since 2020/4/15
 */
@Getter
@Setter
public class FileTree {
    private TreeView<TreeNode> treeView;
    private FileTreeItem rootTree;
    private ViewerPane viewerPane;
    private List<FileTreeItem> rootFileTreeItems;
    /**
     * 隐藏的虚拟根目录
     * 程序结束时，清空cloudAlbum下的所有文件
     */
    private FileTreeItem cloudAlbum;//云相册
    /**
     * cloudImageNoteList 每次运行程序，打开云相册的临时抽象数据链表
     * 每次do get cloudImageNoteList会自动增长
     * ，记录id，以便删除
     */
    private List<CloudImageNote> cloudImageNoteList;

    public FileTree(ViewerPane viewerPane) throws IOException {
        this.viewerPane = viewerPane;
        this.setRootFileTreeItems();
        TaskThreadPools.execute(new FileTreeLoader(this));
        addListener();
    }

    public void setRootFileTreeItems() throws IOException {
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
            if(newValue == this.cloudAlbum) {
                /*
                点击的是cloudAlum文件选项，
                进行弹窗提示
                网络连接
                 */

            }else{
                newValue.getValue().setImages();
                ViewerPane.setCurrentTreeNode(newValue.getValue());
            }
//            viewerPane.getToolBar().setSelectedFolder(newValue.getValue());
        });
    }

    /**
     * 添加一个触发器，点击cloudAlbum的时候连接网络加载图片
     */
    public void setCloudAlum() throws IOException {
        File cloudAlbumFile = new File(System.getProperty("user.dir")+"/cloudAlbum");
        if(!cloudAlbumFile.exists()){
            if(cloudAlbumFile.mkdirs())
                Runtime.getRuntime().exec("attrib +H \"" + cloudAlbumFile.getAbsolutePath() + "\"");
        }
        this.cloudAlbum = new FileTreeItem(cloudAlbumFile,cloudAlbumFile.getName());
    }
}