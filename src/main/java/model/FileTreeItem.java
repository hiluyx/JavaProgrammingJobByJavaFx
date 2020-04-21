package model;

import java.io.File;
import javafx.scene.control.TreeItem;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FileTreeItem extends TreeItem<TreeNode> {

    private Boolean isNotInit = true;
    private File file;
    private TreeNode treeNode;
    private boolean isRoot;

    public FileTreeItem(File file, String name, boolean isRoot) {
        this.file = file;
        this.isRoot = isRoot;
        this.treeNode = new TreeNode(file, name);
        this.setValue(treeNode);
    }
}