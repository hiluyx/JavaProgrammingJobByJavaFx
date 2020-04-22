package controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.ScrollPane;

import model.PictureNode;
import model.TreeNode;

public class ViewerPane extends BorderPane {
    public final static SimpleObjectProperty<TreeNode> selectedFolderProperty = new SimpleObjectProperty<>();
    public final static FlowPane flowPane = new FlowPane();
    private final ToolBar toolBar;
    private final Label massageOfPictures;
    private final PPT ppt = new PPT();

    public ViewerPane() {
        //添加监听器
        addListener();
        //预览区上方的功能按键(复制粘贴剪切删除进入幻灯片播放)可以另外定义这个界面也可以放在构造方法外面
        this.toolBar = new ToolBar(10);
        this.setTop(toolBar);
        //以下为图片预览窗口
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(flowPane);
        scrollPane.setStyle("-fx-background-color: White;");
        flowPane.setStyle("-fx-background-color: White;");
        this.setCenter(scrollPane);
        //以下为图片信息
        this.massageOfPictures = new Label();
        this.setBottom(massageOfPictures);
    }

    private void addListener() {
        selectedFolderProperty.addListener((observable, oldValue, newValue) -> {
            //flowPane清空
            flowPane.getChildren().remove(0, flowPane.getChildren().size());
            toolBar.getPath().setText(newValue.getFile().getAbsolutePath());
            if (newValue.getImages() != null) {
                long totalByte = 0;
                for (int i = 0; i < newValue.getImages().size(); i++) {
                    //添加图片
                    PictureNode iv = new PictureNode(newValue.getImages().get(i));
                    flowPane.getChildren().add(iv);
                    //统计图片大小
                    totalByte += newValue.getImages().get(i).length();
                }
                massageOfPictures.setText(String.format("%d张图片(%.2fMB)", newValue.getImages().size(), totalByte / 1024.0 / 1024.0));
            } else {
                massageOfPictures.setText(String.format("%d张图片(0MB)", 0));
            }

        });
    }

    public void setSelectedFolder(TreeNode selectedFolder) {
        this.selectedFolderProperty.set(selectedFolder);
    }

    public ToolBar getToolBar() {
        return toolBar;
    }

    public PPT getPpt() {
        return ppt;
    }
}
