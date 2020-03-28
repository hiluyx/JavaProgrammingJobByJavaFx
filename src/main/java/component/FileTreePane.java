package component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import model.FileTreeItem;

import java.io.File;
import java.util.function.Function;

@Getter
@Setter
public class FileTreePane extends VBox {
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

}
