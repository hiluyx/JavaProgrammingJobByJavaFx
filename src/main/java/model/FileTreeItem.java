package model;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Data
public class FileTreeItem extends TreeItem<String> {
    //当前文件目录,包含该目录下的所有子目录dirs
    private File file;
    private boolean notInitialized = true;
    private final Function<File, File[]> supplier;

    public FileTreeItem(File file, Function<File, File[]> supplier) {
        this.supplier = supplier;
    }

    @Override
    public ObservableList<TreeItem<String>> getChildren() {

        ObservableList<TreeItem<String>> children = super.getChildren();
        //没有加载子目录时，则加载子目录作为树节点的孩子
        if (this.notInitialized && this.isExpanded()) {

            this.notInitialized = false;    //设置没有初始化为假

            /*判断树节点的文件是否是目录，
             *如果是目录，着把目录里面的所有的文件目录添加入树节点的孩子中。
             */

            if (this.getFile().isDirectory()) {
                for (File f : supplier.apply(this.getFile())) {
                    //如果文件是目录，则把它加到树节点上
                    if (f.isDirectory()) {
                        children.add(new FileTreeItem((Function<File, File[]>) f));
                    }
                }

            }
        }
        return children;
    }
}
