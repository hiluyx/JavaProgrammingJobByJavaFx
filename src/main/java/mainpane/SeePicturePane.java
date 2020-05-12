package mainpane;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.TreeNode;
import toolpane.Screen_shot;
import util.ButtonUtil;

import java.io.File;

public class SeePicturePane extends BorderPane {

    private final TreeNode treeNode;
    private StackPane stackPane;
    private BorderPane borderPane;
    private ToolBar toolBar;
    private int clickCount;                     // 计数器
    private int changeNum = 0;                  //缩放系数
    private int currentRotate = 0;              //当前角度
    private boolean isRotate;                   //左转为false，右转为true
    int w = 800;
    int h = 800;

    public SeePicturePane(File file, String nodePane) {
        //前提判断
        treeNode = new TreeNode(file.getParentFile(), file.getParentFile().getName());
        treeNode.setImages();
        if ("".equals(nodePane)) {
            clickCount = 0;
        } else {
            for (int i = 0; i < treeNode.getImages().size(); i++) {
                if (nodePane.equals(treeNode.getImages().get(i).getName())) {
                    clickCount = i;
                    break;
                }
            }
        }   //判断图片

        //初始化界面以及添加监听器
        initSeePicturePane();

    }

    //初始化界面以及添加监听器
    private void initSeePicturePane(){
        //组件
        Button previous = ButtonUtil.createButton("previous");
        Button next = ButtonUtil.createButton("next");
        Button enlarge = ButtonUtil.createButton("enlarge");
        Button small = ButtonUtil.createButton("small");
        Button ppt = ButtonUtil.createButton("ppt");
        Button left_rotate = ButtonUtil.createButton("left_rotate");
        Button right_rotate = ButtonUtil.createButton("right_rotate");
        Button screenshot = ButtonUtil.createButton("screenshot");

        //中间部件，图片
        ImageView iv = new ImageView(new Image("file:" + this.treeNode.getImages().get(clickCount),w,h,true,true));
        borderPane = new BorderPane();
        borderPane.setCenter(iv);
        stackPane = new StackPane(borderPane);
        StackPane.setAlignment(borderPane,Pos.CENTER);
        this.setCenter(stackPane);

        //顶部部件，功能按键
        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.BOTTOM_CENTER);
        hBox.getChildren().addAll(small, left_rotate, ppt, screenshot, right_rotate, enlarge);
        toolBar = new ToolBar(hBox);
        toolBar.setStyle("-fx-background-color:White;");
        hBox.minWidthProperty().bind(toolBar.widthProperty());
        this.setTop(toolBar);

        //两边，功能按键
        this.getChildren().addAll(previous,next);
        previous.layoutYProperty().bind(this.heightProperty().divide(2).subtract(previous.heightProperty()));
        next.layoutYProperty().bind(this.heightProperty().divide(2).subtract(next.heightProperty()));
        next.layoutXProperty().bind(this.widthProperty().subtract(next.widthProperty()).subtract(8));
        previous.setLayoutX(8.0);

        //总体
        this.setStyle("-fx-background-color:White;");
        Scene scene = new Scene(this, 1350, 1350 * 0.65);
        Stage stage = new Stage();
        stage.getIcons().add(new Image("file:"+new File("stageIcon.png"),30, 30,
                true, true));
        stage.setTitle("图片查看界面");
        stage.setScene(scene);
        stage.show();

        ///////////////监听器//////////////////
        previous.setOnMouseClicked(e -> { this.clickCount--;previous_next_action(); });
        next.setOnMouseClicked(e -> { this.clickCount++;previous_next_action(); });
        ppt.setOnMouseClicked(e -> new PPTPane(treeNode) );
        small.setOnMouseClicked(e -> { this.changeNum--;enlarge_small_action(); });
        enlarge.setOnMouseClicked(e -> { this.changeNum++;enlarge_small_action(); });
        left_rotate.setOnMouseClicked(e->{ this.isRotate=false;rotate(); } );
        right_rotate.setOnMouseClicked(e->{ this.isRotate=true;rotate(); } );
        screenshot.setOnAction( e-> new Screen_shot(treeNode.getFile()) );
    }

    //上一张下一张
    private void previous_next_action() {
        if (this.clickCount < 0) {
            tipsBox("这是第一张图片");
            this.clickCount++;
        } else if (this.clickCount > this.treeNode.getImages().size() - 1) {
            tipsBox("这是最后一张图片");
        } else {
            this.currentRotate = 0;
            this.changeNum = 0;
            System.out.println(this.clickCount);
            ImageView iv = new ImageView(new Image("file:" + this.treeNode.getImages().get(clickCount),w,h,true,true));

            borderPane.setCenter(iv);
        }
    }

    //缩放功能
    private void enlarge_small_action() {
        if (this.changeNum <= -5) {
            tipsBox("已是最小");
            changeNum++;
        } else if (this.changeNum >= 10) {
            tipsBox("已是最大");
            changeNum--;
        }

        ImageView iv = (ImageView) borderPane.getCenter();
        iv.setScaleX(changeNum * 0.1 + 1);
        iv.setScaleY(changeNum * 0.1 + 1);
        System.out.println(iv.prefHeight(-1));
        iv.setSmooth(true);
        iv.setPreserveRatio(true);

    }

    //旋转
    private void rotate(){
        if(isRotate){
            currentRotate += 90;
        }else{
            currentRotate -= 90;
        }
        ImageView iv = (ImageView) borderPane.getCenter();
        iv.setRotate(this.currentRotate % 360);
        iv.setSmooth(true);
        iv.setPreserveRatio(true);
    }

    //提示框
    private void tipsBox(String massageOfTips) {
        Stage stage = new Stage();

        //锁定当前提示框
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.getIcons().add(new Image("file:"+new File("stageIcon.png"),30, 30,
                true, true));
        stage.setTitle("提示");
        stage.setMinWidth(300);
        stage.setMaxHeight(200);
        Label label = new Label(massageOfTips);
        Button closeButton = ButtonUtil.createButton("cancel");
        closeButton.setOnAction(e ->stage.close());

        VBox vBox = new VBox(10);
        vBox.setStyle("-fx-background-color: White");
        vBox.getChildren().addAll(label, closeButton);
        vBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene (vBox);
        stage.setScene(scene);
        stage.show();
    }

}