package controller;


import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;

import model.PictureNode;
import model.TreeNode;

public class ViewerPane extends BorderPane {
    private final SimpleObjectProperty<TreeNode> selectedFolderProperty = new SimpleObjectProperty<>();
    private PictureNode pictureNodeProperty;
    public final static FlowPane flowPane = new FlowPane();

    public ViewerPane() {

        //预览区上方的功能按键(复制粘贴剪切删除进入幻灯片播放)可以另外定义这个界面也可以放在构造方法外面
        HBox hBox = new HBox(10);
        hBox.setPadding(new Insets(10, 10, 10, 10));
        Button button = new Button();
        button.setPadding(new Insets(10, 10, 10, 10));
        button.setText("复制");
        Button button2 = new Button();
        button2.setPadding(new Insets(10, 10, 10, 10));
        button2.setText("粘贴");
        hBox.getChildren().add(button);
        hBox.getChildren().add(button2);
        //以下为图片预览窗口
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(flowPane);
        this.setTop(hBox);
        this.setCenter(scrollPane);

        selectedFolderProperty.addListener((observable, oldValue, newValue) -> {
            flowPane.getChildren().remove(0, flowPane.getChildren().size());
            if (newValue != null && newValue.getImages() != null) {
                int imageSetSize = newValue.getImages().size();
                for (int i = 0; i < imageSetSize; i++) {
                    PictureNode iv = new PictureNode(newValue.getImages().get(i));
                    flowPane.getChildren().add(iv);
                }
            }
        });


    }

    public FlowPane getFlowPane() {
        return flowPane;
    }

    public TreeNode getSelectedFolder() {
        return selectedFolderProperty.get();
    }

    public void setSelectedFolder(TreeNode selectedFolder) {
        this.selectedFolderProperty.set(selectedFolder);
    }

    public void setPictureNodeProperty(PictureNode pn) {
        this.pictureNodeProperty = pn;
    }

}
