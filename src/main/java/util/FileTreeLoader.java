package util;

import controller.FileTree;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.Setter;
import model.FileTreeItem;
import model.TreeNode;

import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class FileTreeLoader implements Runnable {
    @Setter
    @Getter
    private FileTree fileTree;

    public FileTreeLoader(FileTree fileTree) {
        this.fileTree = fileTree;
    }

    @Override
    public void run() {
        FileTreeItem root = fileTree.getRootTreeItem();
        loadChildren(root);
        for (TreeItem<TreeNode> item : root.getChildren()) {
            loadChildren((FileTreeItem) item);
        }
    }

    public static void loadChildren(FileTreeItem f) {
        if (f.getIsNotInit()) {
            f.setIsNotInit(false);
            File[] childrenDir;
            boolean isSystemDisk = true;
            if (!f.isRoot()) {
                childrenDir = f.getFile().listFiles(File::isDirectory);
                isSystemDisk = false;
            } else {
                childrenDir = File.listRoots();
            }
            if (childrenDir == null) return;
            for (File child : childrenDir) {
                if (child.isHidden() && !isSystemDisk) continue;
                FileTreeItem item = new FileTreeItem(child, FileTreeLoader.getForName(child, f.isRoot()), false);
                f.getChildren().add(item);
                item.addEventHandler(FileTreeItem.branchExpandedEvent(),
                        (EventHandler<TreeItem.TreeModificationEvent<TreeNode>>) event -> {
                            for (TreeItem<TreeNode> treeItem : event.getSource().getChildren()) {
                                FileTreeItem currChild = (FileTreeItem) treeItem;
                                loadChildren(currChild);
                            }
                        });
            }
        }
    }

    private static String getForName(File file, Boolean isSystemRoot) {
        if (isSystemRoot) {
            FileSystemView fsv = FileSystemView.getFileSystemView();
            return fsv.getSystemDisplayName(new File(file.toString()));
        } else return file.getName();
    }
}
