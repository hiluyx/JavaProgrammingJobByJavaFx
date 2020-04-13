package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import model.FileTreeItem;
import model.TreeNode;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Getter
@Setter
public class FileTreePane extends VBox implements Initializable {
    @FXML
    private TreeView<TreeNode> treeView;//树结构模块
    private FileTreeItem rootTreeItem;

    public FileTreePane() {
        this.setRootFileTreeItem();
        this.getChildren().add(this.treeView);
    }

    //设置根目录
    public void setRootFileTreeItem() {
        /*
        创建一个假文件substitute来作为树的根
         */
        File substitute = new File("Substitute");
        substitute.mkdir();
        this.rootTreeItem = new FileTreeItem(substitute, substitute.getName());

        File[] roots = File.listRoots();
        FileSystemView fsv = FileSystemView.getFileSystemView();
        for (File f : roots) {
            String name = fsv.getSystemDisplayName(new File(f.toString()));
            FileTreeItem child = new FileTreeItem(f, name);
            this.rootTreeItem.getChildren().add(child);
        }
        this.treeView = new TreeView<TreeNode>(rootTreeItem);
        this.treeView.setShowRoot(false);//隐藏假文件
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        this.treeView.prefHeightProperty().bind(this.heightProperty());

        this.treeView.getSelectionModel().selectionModeProperty().addListener((observable, oldValue, newValue) -> {
            TreeNode imageFiles = this.treeView.getSelectionModel().getSelectedItem().getValue();
        });
    }

    public String getURL(FileTreeItem fileTreeItem) {
        return fileTreeItem.getFile().getAbsolutePath();//返回绝对路径
    }
}
