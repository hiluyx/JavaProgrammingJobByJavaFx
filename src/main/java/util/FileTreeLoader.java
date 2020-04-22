package util;

import controller.FileTree;
import model.FileTreeItem;

import javafx.application.Platform;
import javafx.concurrent.Task;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class FileTreeLoader extends Task {

    @Setter
    @Getter
    private FileTree fileTree;

    public FileTreeLoader(FileTree fileTree) {
        this.fileTree = fileTree;
    }

    @Override
    protected Object call() throws Exception {
        List<FileTreeItem> fileTreeItems = fileTree.getFileTreeItems();
        while (fileTreeItems.size() > 0) {
            List<FileTreeItem> allChildren = new ArrayList<>();
            for (FileTreeItem item : fileTreeItems) {
                System.out.println(item.getValue().getNodeText());
                File curFile = item.getFile();
                if (curFile.isDirectory()) {
                    List<FileTreeItem> curChildren = new ArrayList<>();
                    File[] childFiles = curFile.listFiles(File::isDirectory);
                    if (childFiles == null) continue;
                    for (File childFile : childFiles) {
                        FileTreeItem child = new FileTreeItem(childFile, childFile.getName(), false);
                        curChildren.add(child);
                    }
                    Platform.runLater(() -> {
                        item.setExpanded(false);
                        for (FileTreeItem child : curChildren) {
                            item.getChildren().add(child);
                        }
                    });
                    allChildren.addAll(curChildren);
                }
            }
            fileTreeItems = allChildren;
        }
        return null;
    }

    public static String getDiskName(File file) {
        FileSystemView fsv = FileSystemView.getFileSystemView();
        return fsv.getSystemDisplayName(new File(file.toString()));
    }
}
