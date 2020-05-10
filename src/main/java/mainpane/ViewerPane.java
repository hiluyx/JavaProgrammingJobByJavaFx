package mainpane;

import toolpane.ProgressBarWindow;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.ScrollPane;

import javafx.scene.layout.HBox;
import model.PictureNode;
import model.TreeNode;
import toolpane.FunctionBar;
import toolpane.NoSelectedMenuPane;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ViewerPane extends BorderPane {

    //当文件树点击其他文件夹的时候，该变量会随之改变，currentTreeNode.getValue()才能获得对应的TreeNode
    public static SimpleObjectProperty<TreeNode> currentTreeNode = new SimpleObjectProperty<>();

    //ViewerPane中主要的组成模块
    //上方功能栏
    public static FunctionBar functionBar = new FunctionBar();
    //中间的flowPane
    public static FlowPane flowPane = new FlowPane();
    //底部的信息显示栏
    public static HBox bottom = new HBox();
    public static Label massageOfPictures = new Label();
    public static Label selectedNumberOfPicture = new Label();

    //其他成员
    private NoSelectedMenuPane noSelectedMenuPane;
    public static ProgressBarWindow progressBarWindow = new ProgressBarWindow();

    public ViewerPane() {
        //初始化ViewerPane
        initViewerPane();

        //添加监听器
        addListener();

        //添加点击空白处后的操作
        clickOutsideTurnWhite();

        //设置功能的快捷键
        keyboardShortCut();

        //添加鼠标位置的监听器
        mouseListener();

    }

    //初始化ViewerPane
    private void initViewerPane() {
        flowPane.setStyle("-fx-background-color:White;");
        //预览区上方的功能按键
        functionBar.getChildren().add(progressBarWindow.getProgressIndicator());
        progressBarWindow.getProgressIndicator().setProgress(0);
        this.setTop(functionBar);
        //生成图片预览窗口
        createPreview();
        //图片信息(共几张，选中几张)
        progressBarWindow.getProgressBar().setProgress(0);
        //控制布局
        selectedNumberOfPicture.setPadding(new Insets(0,710,0,0));
        bottom.getChildren().addAll(massageOfPictures,selectedNumberOfPicture);
        this.setBottom(bottom);
    }

    //生成图片预览窗口(initViewerPane中用到)
    private void createPreview() {
        flowPane.setHgap(5); flowPane.setVgap(5);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background-color:White;");
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(flowPane);
        //flow背景设置为白色
        scrollPane.setStyle("-fx-background-color: White;");
        this.setCenter(scrollPane);
    }

    //监听文件夹节点变化的监听器
    private void addListener() {
        currentTreeNode.addListener((observable, oldValue, newValue) -> {

            //清空flowPane的子节点(但不清空被锁定的图片)
            try {
                int size = flowPane.getChildren().size();
                int index = 0;
                for (int i=0;i<size;i++){
                    if(!((PictureNode) flowPane.getChildren().get(index)).getLocked()){
                        flowPane.getChildren().remove(index);
                    }
                    else {
                        index++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //更新当前路径
            functionBar.getPath().setText(newValue.getFile().getAbsolutePath());

            //统计图片张数与图片大小
            if (newValue.getImages() != null) {

                //设置进度条
                progressBarWindow.getProgressIndicator().setProgress(0);

                //如果没有图片，设置为完成
                if (newValue.getImages().size()==0){
                    progressBarWindow.getProgressIndicator().setProgress(1);
                }

                //总大小
                long totalByte = 0;

                //添加图片
                for (int i = 0; i < newValue.getImages().size(); i++) {

                    PictureNode pN = new PictureNode(newValue.getImages().get(i));

                    boolean isExit = false;

                    for(int j=0;j<flowPane.getChildren().size();j++){
                        PictureNode pictureInFlowPane = (PictureNode)flowPane.getChildren().get(j);
                        //判断当前图片是否已经存在(因为被锁定的图片不会清空)
                        if(pN.getFile().getAbsolutePath().equals(pictureInFlowPane.getFile().getAbsolutePath())){
                            isExit = true;
                        }
                    }

                    //如果当前图片不存在,可添加进flowPane
                    if(!isExit){
                        flowPane.getChildren().add(pN);
                    }

                    //统计图片大小
                    totalByte += newValue.getImages().get(i).length();

                    //设置进度条
                    progressBarWindow.getProgressIndicator().setProgress(
                            (double) (i + 1) / newValue.getImages().size());

                }

                //更新当前文件夹下的图片信息
                massageOfPictures.setText(String.format("%d张图片(%.2fMB)",
                        newValue.getImages().size(),totalByte / 1024.0 / 1024.0));
            } else {
                massageOfPictures.setText("0张图片(0MB)");
            }

            //刚加载时,没有图片被选中,默认0张
            ViewerPane.selectedNumberOfPicture.setText("-选中0张");

            //设置“查看”按钮的可用性(如果没有图片，则不可用)
            if (ViewerPane.currentTreeNode.getValue().getImages().size() > 0) {
                ViewerPane.functionBar.getSeePicture().setDisable(false);
            } else {
                ViewerPane.functionBar.getSeePicture().setDisable(true);
            }

        });
    }

    //点击空白处取消选中
    private void clickOutsideTurnWhite() {
        ViewerPane.flowPane.setOnMouseClicked(e -> {

            //如果点击了flowPane的空白处
            if (e.getPickResult().getIntersectedNode() instanceof FlowPane) {
                //上传按钮不可用
                FunctionBar.upLoad.setDisable(true);

                //如果是右击则出现菜单
                this.noSelectedMenuPane = new NoSelectedMenuPane(
                        ViewerPane.flowPane);

                //把已选中的图片清空,并保留被锁定图片为灰色
                PictureNode.getSelectedPictures().clear();
                ViewerPane.selectedNumberOfPicture.setText("-选中0张");
                for(Node each:ViewerPane.flowPane.getChildren()){
                    PictureNode pictureNode = (PictureNode)each;
                    if(!pictureNode.getLocked()){
                        pictureNode.setStyle("-fx-background-color: transparent");
                    }else{
                        pictureNode.setStyle("-fx-background-color: lightgray");
                    }
                }
            }
        });
    }

    //设置快捷键
    private void keyboardShortCut() {
        this.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.A) {
                noSelectedMenuPane.allSelectedFunction();
            } else if (event.getCode() == KeyCode.V) {
                noSelectedMenuPane.pasteFunction();
            } else if (event.getCode() == KeyCode.Z) {
                noSelectedMenuPane.revocationFunction();
            }
        });
    }

    //鼠标拖拽多选
    private void mouseListener(){
        AtomicReference<Double> sceneX_start = new AtomicReference<>((double) 0);
        AtomicReference<Double> sceneY_start = new AtomicReference<>((double) 0);
        AtomicReference<Double> width = new AtomicReference<>((double) 0);
        AtomicInteger colNum = new AtomicInteger();     //一行图片数(列数)

        ViewerPane.flowPane.setOnMousePressed(event ->{
            sceneX_start.set(event.getX());
            sceneY_start.set(event.getY());
            width.set(ViewerPane.flowPane.getWidth());
            colNum.set((int) (width.get() / 120));
            System.out.println("colNun:"+colNum);
        });
        ViewerPane.flowPane.setOnDragDetected(event -> flowPane.startFullDrag());
        ViewerPane.flowPane.setOnMouseDragOver(event -> {
            double startX = event.getX() < sceneX_start.get() ? event.getX() : sceneX_start.get();
            double startY = event.getY() < sceneY_start.get() ? event.getY() : sceneY_start.get();
            double endX = event.getX() > sceneX_start.get() ? event.getX() : sceneX_start.get();
            double endY = event.getY() > sceneY_start.get() ? event.getY() : sceneY_start.get();
            int startRow = (int) (startY / 150);//起始行
            int startCol = (int) (startX / 120);//起始列
            int endRow = (int) (endY / 150);    //终止行
            int endCol = (int) (endX /120);     //终止列
            int pictureRow;
            int pictureCol;

            for(int i = 0 , length = ViewerPane.flowPane.getChildren().size(); i < length; i++){
                pictureRow = i / colNum.get();
                pictureCol = i % colNum.get();

                if(pictureRow < startRow || pictureRow > endRow || pictureCol < startCol || pictureCol > endCol){
                    if(PictureNode.getSelectedPictures().indexOf(ViewerPane.flowPane.getChildren().get(i)) != -1){
                        if(!((PictureNode) ViewerPane.flowPane.getChildren().get(i)).getLocked()){
                            PictureNode.getSelectedPictures().remove(ViewerPane.flowPane.getChildren().get(i));
                            ViewerPane.flowPane.getChildren().get(i).setStyle("-fx-background-color: White;");
                        }

                    }
                }else{
                    if(PictureNode.getSelectedPictures().indexOf(ViewerPane.flowPane.getChildren().get(i)) == -1){
                        if(!((PictureNode) ViewerPane.flowPane.getChildren().get(i)).getLocked()){
                            PictureNode.getSelectedPictures().add((PictureNode) ViewerPane.flowPane.getChildren().get(i));
                            ViewerPane.flowPane.getChildren().get(i).setStyle("-fx-background-color: #8bb9ff;");
                        }

                    }
                }
            }
        });

        flowPane.setOnMouseDragExited(event -> {
            System.out.println("结束");
            System.out.println(PictureNode.getSelectedPictures().size());
            //设置上传按钮可用性
            if(PictureNode.getSelectedPictures().size()>0){
                FunctionBar.upLoad.setDisable(false);
            }
            else {
                FunctionBar.upLoad.setDisable(true);
            }
            //更新选中了多少张
            ViewerPane.selectedNumberOfPicture.setText("-选中" + PictureNode.getSelectedPictures().size() + "张");
        });
    }

    //更改文件树节点
    public static void setCurrentTreeNode(TreeNode newTreeNode) {
        currentTreeNode.set(newTreeNode);
    }
}
