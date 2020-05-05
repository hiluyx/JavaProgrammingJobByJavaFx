package controller;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeView;
import lombok.Getter;
import lombok.Setter;

import model.FileTreeItem;
import model.TreeNode;
import util.TaskThreadPools;
import util.fileUtils.FileTreeLoader;
import model.CloudImageNote;
import util.httpUtils.HttpUtil;
import util.httpUtils.exception.RequestConnectException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private boolean isOpened;
    public static final DialogSel dialog = new DialogSel();

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
            if(newValue == null) return;
            if(newValue == this.cloudAlbum&&!isOpened) {
                /*
                这里应该弹窗询问是否加载
                 */
                isOpened = true;
                TaskThreadPools.execute(()->{
                    while (true) {
                        ProgressBarWindow.updateProgressBar(0);
                        try {
                            HttpUtil.doGetPageImages(this.cloudImageNoteList,0,10);
                        } catch (RequestConnectException | URISyntaxException exception) {
                            /*
                            连接出现错误，退出提示框
                             */
                            if(dialog.errorDialog(exception)) break;
                        }
                        this.cloudAlbum.getTreeNode().setImages();
                        Platform.runLater(()-> ViewerPane.setCurrentTreeNode(this.cloudAlbum.getTreeNode()));
                    }
                });
            }else if(newValue != this.cloudAlbum){
                newValue.getValue().setImages();
//                TaskThreadPools.execute(()->{
//                    List<String> paths = new ArrayList<>();
//                    List<File> images = newValue.getValue().getImages();
//                    if(images.size() != 0){
//                        for(File file : images){
//                            paths.add(file.getAbsolutePath());
//                        }
//                        while(true){
//                            try {
//                                HttpUtil.doPostJson(paths);
//                            } catch (URISyntaxException | RequestConnectException exception) {
//                                exception.printStackTrace();
//                                if(dialog.errorDialog(exception)) break;
//                            }
//                        }
//                    }
//                });
                ViewerPane.setCurrentTreeNode(newValue.getValue());
            }else{
                ViewerPane.setCurrentTreeNode(this.cloudAlbum.getTreeNode());
                System.out.println("云相册已经开始加载或者已经完成！");
            }
        });
    }

    public void setCloudAlum() throws IOException {
        File cloudAlbumFile = new File(System.getProperty("user.dir")+"/cloudAlbum");
        if(!cloudAlbumFile.exists()){
            if(cloudAlbumFile.mkdirs())
                Runtime.getRuntime().exec("attrib +H \"" + cloudAlbumFile.getAbsolutePath() + "\"");
        }
        this.cloudAlbum = new FileTreeItem(cloudAlbumFile,cloudAlbumFile.getName());
        this.cloudImageNoteList = new ArrayList<>();
        this.isOpened = false;
    }
    /*
    重连对话框
     */
    public static class DialogSel{
        @Getter
        private boolean yes = false;
        public void setYes(String p_message){
            Platform.runLater(()->{
                synchronized (dialog){
                    Alert _alert = new Alert(Alert.AlertType.CONFIRMATION, p_message,new ButtonType("取消", ButtonBar.ButtonData.NO),
                            new ButtonType("确定", ButtonBar.ButtonData.YES));
                    Optional<ButtonType> _buttonType = _alert.showAndWait();
                    _buttonType.ifPresent(buttonType -> {
                        this.yes = (buttonType.getButtonData().equals(ButtonBar.ButtonData.YES));
                    });
                    FileTree.dialog.notifyAll();
                }
            });
        }
        public boolean errorDialog(Exception exception){
            synchronized (dialog){
                if (exception instanceof RequestConnectException){
                    try {((RequestConnectException) exception).errorDialog();
                        dialog.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return !dialog.isYes();
                }else return true;
            }
        }
    }
}