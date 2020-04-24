package controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.ScrollPane;

import javafx.scene.layout.HBox;
import model.PictureNode;
import model.TreeNode;

public class ViewerPane extends BorderPane {
    //当文件树点击其他文件夹的时候，该变量会随之改变，selectedFolderProperty.getValue()才能获得对应的TreeNode
    public static SimpleObjectProperty<TreeNode> selectedFolderProperty = new SimpleObjectProperty<>();

    public static FlowPane flowPane = new FlowPane();
    public static ToolBar toolBar = new ToolBar(10);
    public static HBox bottom = new HBox();
    public static Label massageOfPictures = new Label();
    public static Label 选中多少张 = new Label();

    public ViewerPane() {
        //添加监听器
        addListener();
        //预览区上方的功能按键(复制粘贴剪切删除)
        this.setTop(toolBar);
        //以下为图片预览窗口
        生成图片预览窗口();
        //图片信息(共几张，选中几张)
        bottom.getChildren().addAll(massageOfPictures,选中多少张);
        this.setBottom(bottom);
        //点击外面变白
        clickOutsideTurnWhite();
    }

    private void 生成图片预览窗口(){
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(flowPane);
        //flow背景设置为白色
        scrollPane.setStyle("-fx-background-color: White;");
        flowPane.setStyle("-fx-background-color: White;");
        this.setCenter(scrollPane);
    }
    //监听文件夹节点变化的监听器
    private void addListener() {
        selectedFolderProperty.addListener((observable, oldValue, newValue) -> {
            ////////////清空flowPane的子节点
            try {
                flowPane.getChildren().remove(0, flowPane.getChildren().size());
            }catch (Exception e){
                e.printStackTrace();
            }
            /////////////更新当前路径
            toolBar.getPath().setText(newValue.getFile().getAbsolutePath());
            /////////////统计图片张数与图片大小
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
                massageOfPictures.setText(String.format("0张图片(0MB)"));
            }
            ViewerPane.选中多少张.setText(String.format("-选中0张"));
        });
    }

    //假装点击外面变白
    private void clickOutsideTurnWhite(){
        ViewerPane.flowPane.setOnMouseClicked(e->{
            int rowNumber = ViewerPane.flowPane.getChildren().size()/5+1;//获取图片行数(不准确)
            if(e.getY()>rowNumber*110){
                PictureNode.getSelectedPictures().clear();//清空PIctureNode中被选中的图片(不知道还要不要清除其他的)
                for(int i=0;i<ViewerPane.flowPane.getChildren().size();i++){//把所有子节点背景设置为白色
                    ViewerPane.flowPane.getChildren().get(i).setStyle("-fx-background-color: White;");
                }
            }
        });
    }
    
    public Label get选中多少张() {
        return 选中多少张;
    }

    public static void setSelectedFolder(TreeNode selectedFolder) {
        selectedFolderProperty.set(selectedFolder);
    }

    public static TreeNode getSelectedFolder() {
        return selectedFolderProperty.getValue();
    }

}
