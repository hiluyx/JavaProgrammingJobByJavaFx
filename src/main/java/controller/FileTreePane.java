package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import model.FileTreeItem;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;

@Getter
@Setter
public class FileTreePane extends VBox implements Initializable {
    /*
    文件树窗口
    JavaFX TreeView + JavaFX ScrollPane
    http://www.xntutor.com/javafx/javafx-treeview.html
    http://www.xntutor.com/javafx/javafx-scrollpane.html



     */
    @FXML
    private TreeView<String> treeView;//树结构模块

    @FXML
    private ScrollPane scrollPane;

    private FileTreeItem rootTreeItem;

    public FileTreePane() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/file-tree-pane.fxml"));
        loader.setRoot(this);
        loader.setController(this);
    }

    //设置根目录
    public void setRootFileTreeItem(File file) {
        rootTreeItem = new FileTreeItem((Function<File, File[]>) file);
        rootTreeItem.setExpanded(true);
        treeView.setRoot(rootTreeItem);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*
        初始化
        PS：对于一个fxml文件来说它首先执行控制器的构造函数，
        这个时候它是无法对@FXML修饰的方法进行访问的，
        然后执行@FXML修饰的方法，最后执行initializable方法，
        我们可以在initializable方法中对fxml文件的控件进行初始化。
         */
    }
}
