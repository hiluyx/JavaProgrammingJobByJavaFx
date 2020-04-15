package model;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import lombok.Data;

@Data
public class FileTreeItem extends TreeItem<TreeNode> {

    private boolean notInitialized = true;
    private File file;
    private TreeNode treeNode;

    public FileTreeItem(File file, String name) {
        this.file = file;
        this.treeNode = new TreeNode();
        this.setValue(treeNode);
        this.getValue().setNodeText(name);
    }

    @Override
    public ObservableList<TreeItem<TreeNode>> getChildren() {

        ObservableList<TreeItem<TreeNode>> children = super.getChildren();

        if (this.notInitialized && this.isExpanded()) {
            this.notInitialized = false;
            File[] dirs = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });//鏂囦欢杩囨护鍣�
            File[] images = file.listFiles(new FileFilter() {
                private Set<String> set = new HashSet<>();

                @Override
                public boolean accept(File pathname) {
                    set.addAll(Arrays.asList("jpg", "png", "gif", "bmp"));
                    return set.contains(getExtension(pathname.getName()));
                }

                public String getExtension(String name) {
                    if (name == null) {
                        return null;
                    }
                    int index = name.lastIndexOf(".");
                    return index > -1 ? name.substring(index + 1) : null;
                }
            });//鍥剧墖鍚庣紑杩囨护鍣�

            if (dirs != null) {
                for (File f : dirs) {
                    FileTreeItem fileTreeItem = new FileTreeItem(f, f.getName());
                    children.add(fileTreeItem);
                }
            }
            if (images != null) this.getTreeNode().setImages(Arrays.asList(images));
        }
        return children;
    }

    @Override
    public boolean isLeaf() {
        return !file.isDirectory();
    }
}