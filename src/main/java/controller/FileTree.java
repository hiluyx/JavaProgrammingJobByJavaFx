package controller;

import javafx.fxml.FXML;

import java.io.File;
import javax.swing.filechooser.FileSystemView;

import javafx.scene.control.TreeView;
import lombok.Getter;
import lombok.Setter;
import model.FileTreeItem;
import model.TreeNode;

@Getter
@Setter
public class FileTree {
    private TreeNode imageFiles;
    @FXML
    private TreeView<TreeNode> treeView;//鏍戠粨鏋勬ā鍧�
    private FileTreeItem rootTreeItem;
    private ViewerPane viewerPane;

    public FileTree(ViewerPane viewerPane) {
        this.viewerPane = viewerPane;
        this.setRootFileTreeItem();
        addListener();
    }

    public void setRootFileTreeItem() {
        File substitute = new File("Substitute");
      //  substitute.mkdir();
        this.rootTreeItem = new FileTreeItem(substitute, substitute.getName());

        File[] roots = File.listRoots();
        FileSystemView fsv = FileSystemView.getFileSystemView();
        for (File f : roots) {
            String name = fsv.getSystemDisplayName(new File(f.toString()));
            FileTreeItem child = new FileTreeItem(f, name);
            this.rootTreeItem.getChildren().add(child);
        }
        this.treeView = new TreeView<>(rootTreeItem);
        this.treeView.setShowRoot(false);
    }

    public void addListener() {
        this.getTreeView().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            System.out.println("正在更新数据");
            viewerPane.setSelectedFolder(newValue.getValue());
            System.out.println("数据更新结束");
        });
    }

    public TreeNode getImageFiles() {
        return imageFiles;
    }

    public void setImageFiles(TreeNode imageFiles) {
        this.imageFiles = imageFiles;
    }

//	 public void addListener() {
//	        this.treeView.getSelectionModel().selectionModeProperty().addListener(new ChangeListener<SelectionMode>() {
//	            @Override
//	            public void changed(ObservableValue<? extends SelectionMode> observable, SelectionMode oldValue, SelectionMode newValue) {
//	                imageFiles = treeView.getSelectionModel().getSelectedItem().getValue();
//	                System.out.println("你被监听了");
//	            }
//	        });
//	    }


//	public TreeNode getImageFiles() {
//		return imageFiles;
//	}
//
//	public void setImageFiles(TreeNode imageFiles) {
//		this.imageFiles = imageFiles;
//	}


}