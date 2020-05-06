package util.fileUtils;

import controller.FileTree;
import controller.ProgressBarWindow;
import controller.ViewerPane;
import model.FileTreeItem;

import javafx.application.Platform;
import model.PictureNode;
import util.TaskThreadPools;
import util.httpUtils.HttpUtil;
import util.httpUtils.exception.RequestConnectException;

import javax.swing.filechooser.FileSystemView;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @since 2020/4/22
 * @author Hi lu
 */
public class FileTreeLoader implements Runnable {

    private final FileTree fileTree;

    public FileTreeLoader(FileTree fileTree) {
        this.fileTree = fileTree;
    }

    public static String getDiskName(File file) {
        FileSystemView fsv = FileSystemView.getFileSystemView();
        return fsv.getSystemDisplayName(new File(file.toString()));
    }

    /**
     * 深度遍历+多线程算法，当文件层数大于3时，启动多线程加载。
     *
     * 预加载
     */
    @Override
    public void run() {
        List<FileTreeItem> fileTreeItems = fileTree.getRootFileTreeItems();
        int dirLevel = 0;
        while (fileTreeItems.size() > 0) {
            List<FileTreeItem> allChildren = new ArrayList<>();
            for (FileTreeItem item : fileTreeItems) {
                if (dirLevel < 3) {
                    List<FileTreeItem> childList = loadChildren(item);
                    if (childList != null) {
                        allChildren.addAll(childList);
                    }
                } else {
                    TaskThreadPools.executeOnCachedThreadPool(() -> {
                        loadChildren(item);
                    });
                }
            }
            dirLevel++;
            fileTreeItems = allChildren;
        }
    }

    public List<FileTreeItem> loadChildren(FileTreeItem item) {
        File curFile = item.getFile();
        List<FileTreeItem> curChildren = new ArrayList<>();
        File[] childFiles = curFile.listFiles(File::isDirectory);
        if (childFiles == null) return null;
        for (File childFile : childFiles) {
            if(childFile.isHidden()) continue;
            FileTreeItem child = new FileTreeItem(childFile, childFile.getName());
            curChildren.add(child);
        }
        Platform.runLater(() -> {
            item.setExpanded(false);
            for (FileTreeItem child : curChildren) {
                item.getChildren().add(child);
            }
        });
        return curChildren;
    }

    /**
     * about the cloud album of fileTree
     *
     * get by main
     *
     * post by menuPane
     *
     * @param fileTree fileTree
     */
    public static void getCloudImages(FileTree fileTree){
        fileTree.setOpened(true);
        ViewerPane.bottom.getChildren().add(ViewerPane.progressBarWindow.getProgressBar());
        TaskThreadPools.execute(()->{
            while (true) {
                ProgressBarWindow.updateProgressBar(0);
                try {
                    HttpUtil.doGetPageImages(FileTree.cloudImageNoteList,0,10);
                } catch (RequestConnectException | URISyntaxException exception) {
                    /*
                    连接出现错误，退出提示框
                    */
                    if(exception instanceof RequestConnectException)
                        if (((RequestConnectException) exception).
                                getDialogSel((RequestConnectException) exception)){
                            Platform.runLater(
                                    ()->ViewerPane.bottom.getChildren().
                                            remove(ViewerPane.progressBarWindow.getProgressBar()));
                            break;
                        }
                }
                fileTree.getCloudAlbum().getTreeNode().setImages();
                Platform.runLater(()-> ViewerPane.setCurrentTreeNode(fileTree.getCloudAlbum().getTreeNode()));
            }
        });
    }

    public static void postCloudImages(){
        ViewerPane.bottom.getChildren().add(ViewerPane.progressBarWindow.getProgressBar());
        TaskThreadPools.execute(()->{
            List<String> paths = new ArrayList<>();
            List<PictureNode> pictureNodes = PictureNode.getSelectedPictures();
            List<File> images = new ArrayList<>();
            for(PictureNode pictureNode : pictureNodes){
                images.add(pictureNode.getFile());
            }
            if(images.size() != 0){
                for(File file : images){
                    paths.add(file.getAbsolutePath());
                }
                while(true){
                    try {
                        HttpUtil.doPostJson(paths);
                    } catch (URISyntaxException | RequestConnectException exception) {
                            /*
                             连接出现错误，退出提示框
                            */
                        if(exception instanceof RequestConnectException)
                            if (((RequestConnectException) exception).
                                    getDialogSel((RequestConnectException) exception)){
                                Platform.runLater(
                                        ()->ViewerPane.bottom.getChildren().
                                                remove(ViewerPane.progressBarWindow.getProgressBar()));
                                break;
                            }
                    }
                }
            }
        });
    }
}
