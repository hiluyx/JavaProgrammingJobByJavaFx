package model;

import java.io.File;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.swing.filechooser.FileSystemView;

@EqualsAndHashCode(callSuper = true)
@Data
public class FileTreeItem extends TreeItem<TreeNode> {

    private boolean notInitialized = true;
    private File file;
    private TreeNode treeNode;
    private boolean isRoot;

    public FileTreeItem(File file, String name, boolean isRoot) {
        this.file = file;
        this.isRoot = isRoot;
        this.treeNode = new TreeNode(file, name);
        this.setValue(treeNode);
//        System.out.println(this.getTreeNode().getNodeText());
//        System.out.println("---------------------------");
//        if(this.getTreeNode().getImages()!=null)for(File f:this.getTreeNode().getImages())
//        System.out.println(f.getName());
//        System.out.println("---------------------------");
    }

    public void loadChildren() {
        if (isNotInitialized()) {
            this.notInitialized = false;
            File[] childrenDir;
            if (!isRoot) {
                childrenDir = this.file.listFiles(File::isDirectory);
            } else {
                childrenDir = File.listRoots();
            }
            if (childrenDir == null) return;
            for (File child : childrenDir) {
                if (child.isDirectory() && (isRoot || !child.isHidden())) {
                    FileTreeItem item = new FileTreeItem(child, this.getForName(child), false);
                    item.addEventHandler(FileTreeItem.branchExpandedEvent(),
                            (EventHandler<TreeModificationEvent<TreeNode>>) event -> {
                                for (TreeItem<TreeNode> treeItem : event.getSource().getChildren()) {
                                    FileTreeItem currChild = (FileTreeItem) treeItem;
                                    currChild.loadChildren();
                                }
                            });
                    this.getChildren().add(item);
                }
            }

        }
    }

    private String getForName(File file) {
        if (isRoot) {
            FileSystemView fsv = FileSystemView.getFileSystemView();
            return fsv.getSystemDisplayName(new File(file.toString()));
        } else return file.getName();
    }
}