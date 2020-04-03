package model;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import lombok.Data;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

@Data
public class FileTreeItem extends TreeItem<String> {

    //判断树节点是否被初始化，没有初始化为真
    private boolean notInitialized = true;

    private File file;//路径

    private List<File> imageFile;

    public FileTreeItem(File file) {
        super(file.getName());
        this.file = file;
    }


    @Override
    public ObservableList<TreeItem<String>> getChildren() {//获取当前文件的子文件（文件夹和图片，两个过滤器）

        ObservableList<TreeItem<String>> children = super.getChildren();

        if (this.notInitialized && this.isExpanded()) {
            //没有被初始化而且可以扩展，然后进行初始化
            this.notInitialized = false;

            File[] dirs = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return file.isDirectory();
                }
            });//文件过滤器
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
            });//图片后缀过滤器

            for (File f : dirs) {
                FileTreeItem fileTreeItem = new FileTreeItem(f);
                children.add(fileTreeItem);
            }
            imageFile.addAll(Arrays.asList(images));
        }

        return children;
    }

    //重写叶子方法，如果该文件不是目录，则返回真
    @Override
    public boolean isLeaf() {
        //是叶子，说明到终点了，不可展开

        return !file.isDirectory();
    }

}
