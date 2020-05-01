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
    public static FunctionBar functionBar = new FunctionBar(10);
    public static HBox bottom = new HBox();
    public static Label massageOfPictures = new Label();
    public static Label selectedNumberOfPicture = new Label();

    private ProgressBarWindow progressBarWindow = new ProgressBarWindow();

    public ViewerPane() {
        flowPane.setId("flowPane");
        //添加监听器
        addListener();
        //预览区上方的功能按键
        functionBar.getChildren().add(progressBarWindow.getProgressIndicator());
        progressBarWindow.getProgressIndicator().setProgress(0);
        this.setTop(functionBar);
        //生成图片预览窗口
        createPreview();
        //图片信息(共几张，选中几张)
        progressBarWindow.getProgressBar().setProgress(0);
        bottom.getChildren().addAll(massageOfPictures,selectedNumberOfPicture,progressBarWindow.getProgressBar());
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
    //    flowPane.setStyle("-fx-background-color: White;");
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
            functionBar.getPath().setText(newValue.getFile().getAbsolutePath());

            //统计图片张数与图片大小
            if (newValue.getImages() != null) {
                progressBarWindow.getProgressIndicator().setProgress(0);
                long totalByte = 0;
                for (int i = 0; i < newValue.getImages().size(); i++) {
                    //添加图片
                    PictureNode iv = new PictureNode(newValue.getImages().get(i));
                    flowPane.getChildren().add(iv);
                    //统计图片大小
                    totalByte += newValue.getImages().get(i).length();
                    progressBarWindow.getProgressIndicator().setProgress((double) (i+1)/newValue.getImages().size());
                }
                massageOfPictures.setText(String.format("%d张图片(%.2fMB)", newValue.getImages().size(), totalByte / 1024.0 / 1024.0));
            } else {
                massageOfPictures.setText("0张图片(0MB)");
            }
            ViewerPane.selectedNumberOfPicture.setText("-选中0张");

            //设置“查看”按钮的可用性
            if(ViewerPane.currentTreeNode.getValue().getImages().size()>0){
                ViewerPane.functionBar.getSeePicture().setDisable(false);
            }
            else {
                ViewerPane.functionBar.getSeePicture().setDisable(true);
            }

        });
    }

    //点击空白处取消选中
    private void clickOutsideTurnWhite() {
        ViewerPane.flowPane.setOnMouseClicked(e -> {
            double width = ViewerPane.flowPane.getWidth();
            double height = ViewerPane.flowPane.getHeight();
            //计算最后一行还有多少图片
            int rowNum = 1+ViewerPane.flowPane.getChildren()
                    .size() / ((int) width / 120);
            int lastPicNum=0;
            if (ViewerPane.flowPane.getChildren().size() != 0) {
                lastPicNum = ViewerPane.flowPane.getChildren()
                        .size() % ((int) width / 120);
                if (lastPicNum == 0) {
                    lastPicNum = (int) width / 120;
                }
            }if(lastPicNum==0||lastPicNum== (int) width / 120){
                rowNum--;
            }
            if ((e.getX()>lastPicNum*120&&e.getY()>(rowNum-1)*150)||e.getY()>rowNum*150||e.getX()>((int)width/120)*120)
            {
                PictureNode.getSelectedPictures().clear();//清空PIctureNode中被选中的图片
                ViewerPane.selectedNumberOfPicture.setText(new String("-选中0张"));
               for (int i = 0; i < ViewerPane.flowPane.getChildren()
                    .size(); i++) {//把所有子节点背景设置为白色
                ViewerPane.flowPane.getChildren().get(i).setStyle(
                        "-fx-background-color: White;");
            }
            }
        });
    }

    //设置新的文件树节点
    public static void setCurrentTreeNode(TreeNode newTreeNode) {
        currentTreeNode.set(newTreeNode);
    }

}
