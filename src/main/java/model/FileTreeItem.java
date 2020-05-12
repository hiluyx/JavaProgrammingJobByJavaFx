package model;

import java.io.File;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Hi lu
 * @since 2020/4/15
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FileTreeItem extends TreeItem<TreeNode> {

    private File file;
    private TreeNode treeNode;

    public FileTreeItem(File file, String name) {
        this.file = file;
        this.treeNode = new TreeNode(file, name);
        this.setValue(treeNode);
        this.setGraphic(new ImageView(new Image("file:"+new File("folder.png"),20, 20,
                true, true)));
    }
}