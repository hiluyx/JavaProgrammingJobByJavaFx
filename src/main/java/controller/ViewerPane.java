package controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.ScrollPane;

import javafx.scene.layout.HBox;
import model.PictureNode;
import model.TreeNode;

public class ViewerPane extends BorderPane {
    //当文件树点击其他文件夹的时候，该变量会随之改变，currentTreeNode.getValue()才能获得对应的TreeNode
    public static SimpleObjectProperty<TreeNode> currentTreeNode = new SimpleObjectProperty<>();

    public static FlowPane flowPane = new FlowPane();
    public static ToolBar toolBar = new ToolBar(10);
    public static HBox bottom = new HBox();
    public static Label massageOfPictures = new Label();
    public static Label selectedNumberOfPicture = new Label();

    private MenuPane menuPane = new MenuPane();

    public ViewerPane() {
        //添加监听器
        addListener();
        //预览区上方的功能按键(复制粘贴剪切删除)
        this.setTop(toolBar);
        //生成图片预览窗口
        createPreview();
        //图片信息(共几张，选中几张)
        bottom.getChildren().addAll(massageOfPictures,selectedNumberOfPicture);
        this.setBottom(bottom);
        //点击空白处取消选中
        clickOutsideTurnWhite();
    }

    //生成图片预览窗口
    private void createPreview(){
        flowPane.setHgap(5);
        flowPane.setVgap(5);
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
        currentTreeNode.addListener((observable, oldValue, newValue) -> {

            //清空flowPane的子节点
            try {
                flowPane.getChildren().remove(0, flowPane.getChildren().size());
            }catch (Exception e){
                e.printStackTrace();
            }

            //更新当前路径
            toolBar.getPath().setText(newValue.getFile().getAbsolutePath());

            //统计图片张数与图片大小
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
                massageOfPictures.setText("0张图片(0MB)");
            }
            ViewerPane.selectedNumberOfPicture.setText("-选中0张");

            //设置“查看”按钮的可用性
            if(ViewerPane.currentTreeNode.getValue().getImages().size()>0){
                ViewerPane.toolBar.getSeePicture().setDisable(false);
            }
            else {
                ViewerPane.toolBar.getSeePicture().setDisable(true);
            }

        });
    }

    //点击空白处取消选中
    private void clickOutsideTurnWhite(){
        ViewerPane.flowPane.setOnMouseClicked(e->{
            if(e.getButton()== MouseButton.SECONDARY){
                System.out.println("右击了flowPane");
                //出现右键菜单
            }
            else{
                int rowNumber = ViewerPane.flowPane.getChildren().size()/5+1;//获取图片行数
                if(e.getY()>rowNumber*110){
                    PictureNode.getSelectedPictures().clear();//清空PIctureNode中被选中的图片
                    ViewerPane.selectedNumberOfPicture.setText("-选中0张");
                    //设置“复制、剪切、删除、重命名”按钮的可用性
                    if (PictureNode.getSelectedPictures().size()>0){
                        ViewerPane.toolBar.getCopy().setDisable(false);
                        ViewerPane.toolBar.getCut().setDisable(false);
                        ViewerPane.toolBar.getDelete().setDisable(false);
                        ViewerPane.toolBar.getReName().setDisable(false);
                    }
                    else {
                        ViewerPane.toolBar.getCopy().setDisable(true);
                        ViewerPane.toolBar.getCut().setDisable(true);
                        ViewerPane.toolBar.getDelete().setDisable(true);
                        ViewerPane.toolBar.getReName().setDisable(true);
                    }
                    for(int i=0;i<ViewerPane.flowPane.getChildren().size();i++){//把所有子节点背景设置为白色
                        ViewerPane.flowPane.getChildren().get(i).setStyle("-fx-background-color: White;");
                    }
                }
            }
        });
    }

    //设置新的文件树节点
    public static void setCurrentTreeNode(TreeNode newTreeNode) {
        currentTreeNode.set(newTreeNode);
    }

}
