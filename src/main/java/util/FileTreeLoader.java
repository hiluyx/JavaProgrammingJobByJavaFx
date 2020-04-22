package util;

import controller.FileTree;
import model.FileTreeItem;

import javafx.application.Platform;
import javax.swing.filechooser.FileSystemView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FileTreeLoader implements Runnable {

    private final FileTree fileTree;

    public FileTreeLoader(FileTree fileTree) {
        this.fileTree = fileTree;
    }

    public static String getDiskName(File file) {
        FileSystemView fsv = FileSystemView.getFileSystemView();
        return fsv.getSystemDisplayName(new File(file.toString()));
    }

    @Override
    public void run() {
        /*
        深度遍历+多线程算法，当文件层数大于3时，启动多线程加载。
         */
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
                        System.out.println(Thread.currentThread().getName());
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
}
