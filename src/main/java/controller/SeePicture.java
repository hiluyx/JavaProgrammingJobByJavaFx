package controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.TreeNode;

import java.io.File;

public class SeePicture extends BorderPane {
    private final TreeNode treeNode;
    private int clickCount;                     // 计数器
    private int changeNum = 0;                  //缩放系数
    private int currentRotate = 0;              //当前角度
    private boolean isRotate;                   //左转为false，右转为true

    public SeePicture(File file, String nodePane) {
        this.setStyle("-fx-background-color:#ffffff;");

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

        ///////////////组件////////////
        ImageView iv = new ImageView(new Image("file:" + this.treeNode.getImages().get(clickCount), 600, 600, true, true));
        //modified by sky
        Button previous = createButton("previous");

        Button next = createButton("next");

        Button enlarge = createButton("enlarge");

        Button small = createButton("small");

        Button ppt = createButton("ppt");

        Button left_rotate = createButton("left_rotate");

        Button right_rotate = createButton("right_rotate");

        Button screenshot = createButton("screenshot");

        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.BOTTOM_CENTER);
        hBox.getChildren().addAll(small, left_rotate, ppt, screenshot, right_rotate, enlarge);

        ///////////////监听器//////////////////
        previous.setOnMouseClicked(e -> { this.clickCount--;previous_next_action(); });
        next.setOnMouseClicked(e -> { this.clickCount++;previous_next_action(); });
        ppt.setOnMouseClicked(e -> new PPT(treeNode) );
        small.setOnMouseClicked(e -> { this.changeNum--;enlarge_small_action(); });
        enlarge.setOnMouseClicked(e -> { this.changeNum++;enlarge_small_action(); });
        left_rotate.setOnMouseClicked(e->{ this.isRotate=false;rotate(); } );
        right_rotate.setOnMouseClicked(e->{ this.isRotate=true;rotate(); } );
        screenshot.setOnAction( e-> new Screen_shot(treeNode.getFile()) );

        this.setCenter(iv);
        this.setLeft(previous);
        this.setRight(next);
        this.setTop(hBox);
        setAlignment(this.getLeft(), Pos.CENTER);
        setAlignment(this.getRight(), Pos.CENTER);
        previous.setContentDisplay(ContentDisplay.CENTER);
        next.setContentDisplay(ContentDisplay.CENTER);
        Scene scene = new Scene(this, 1000, 1000);
        Stage stage = new Stage();
        stage.getIcons().add(new Image("file:"+new File("icon/图标.png"),30, 30,
                true, true));
        stage.setTitle("图片查看界面");
        stage.setScene(scene);
        stage.show();
    }

    //上一张下一张
    private void previous_next_action() {
        if (this.clickCount < 0) {
            Label label = new Label("这是第一张图片");
            Pane root = new Pane(label);
            Scene scene = new Scene(root);
            Stage Stage;
            Stage = new Stage();
            Stage.setTitle("提示");
            Stage.setScene(scene);
            Stage.show();
            this.clickCount++;
        } else if (this.clickCount > this.treeNode.getImages().size() - 1) {
            Label label = new Label("这是最后一张图片");
            Pane root = new Pane(label);
            Scene scene = new Scene(root);
            //modified by sky
            Stage Stage;
            Stage = new Stage();
            Stage.setTitle("提示");
            Stage.setScene(scene);
            Stage.show();
            this.clickCount--;
        } else {
            this.currentRotate = 0;
            this.changeNum = 0;
            System.out.println(this.clickCount);
            ImageView iv = new ImageView(new Image("file:" + this.treeNode.getImages().get(clickCount), 600, 600, true, true));
            this.setCenter(iv);
        }
    }

    //缩放功能
    private void enlarge_small_action() {
        if (this.changeNum <= -5) {
            Label label = new Label("已是最小");
            Pane root = new Pane(label);
            Scene scene = new Scene(root);
            Stage Stage;
            Stage = new Stage();
            Stage.setTitle("提示");
            Stage.setScene(scene);
            Stage.show();
            this.changeNum = 0;
        } else if (this.changeNum >= 5) {
            Label label = new Label("已是最大");
            Pane root = new Pane(label);
            Scene scene = new Scene(root);
            Stage Stage;
            Stage = new Stage();
            Stage.setTitle("提示");
            Stage.setScene(scene);
            Stage.show();
            this.changeNum = 0;
        }
        ImageView iv = (ImageView) this.getCenter();
        iv.setFitWidth(600 * (changeNum * 0.1 + 1));
        iv.setFitHeight(600 * (changeNum * 0.1 + 1));
        iv.setPreserveRatio(true);
    }

    //旋转
    private void rotate(){
        if(isRotate){
            currentRotate += 90;
        }else{
            currentRotate -= 90;
        }
        ImageView iv = new ImageView(new Image("file:" + this.treeNode.getImages().get(clickCount), 500, 500, true, true));
        iv.setRotate(this.currentRotate % 360);
        iv.setFitWidth(600 * (changeNum * 0.1 + 1));
        iv.setFitHeight(600 * (changeNum * 0.1 + 1));
        iv.setPreserveRatio(true);
        this.setCenter(iv);
    }

    private Button createButton(String buttonName) {
        Button button = new Button();
        button.setId(buttonName);
        button.setPadding(new Insets(10, 10, 10, 10));
        //button.setText(buttonName);
        button.setStyle("-fx-background-color:#ffffff;");
        button.setGraphic(new ImageView(new Image("file:"+new File("icon/"+buttonName+".png"),30, 30,
                true, true)));
        return button;
    }
}
