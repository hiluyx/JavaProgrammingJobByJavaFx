package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import model.FileTreeItem;
import model.PictureNode;
import model.TreeNode;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

@Getter
@Setter
public class FileTreePane extends VBox implements Initializable {

    @FXML
    private TreeView<TreeNode> treeView;//树结构模块
    private FileTreeItem rootTreeItem;
    private TreeNode treeNode;

    public FileTreePane() {
        this.setRootFileTreeItem();
        this.getChildren().add(this.treeView);

        this.addListener();
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.treeView.prefHeightProperty().bind(this.heightProperty());
    }

    public void addListener() {
        this.getTreeView().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("你被监听了");
            this.treeNode = this.treeView.getSelectionModel().getSelectedItem().getValue();
        });
    }

    public String getURL(FileTreeItem fileTreeItem) {
        return fileTreeItem.getFile().getAbsolutePath();//返回绝对路径
    }
}
