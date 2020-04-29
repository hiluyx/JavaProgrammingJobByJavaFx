package controller;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.PictureNode;

public class Screen_shot {
    static File nodeFile;
    static ImageView iv;
    static VBox vBox;
    static Stage stage;
    static double sceneX_start;     //截图起点
    static double sceneY_start;
    static double sceneX_end;       //截图终点
    static double sceneY_end;

    Screen_shot(File file) {
        iv = new ImageView();
        nodeFile = file;
        show();
    }

    public void show() {
        AnchorPane an = new AnchorPane();
        an.setStyle("-fx-background-color:#B5B5B522");
        Scene scene = new Scene(an);
        scene.setFill(Paint.valueOf("#ffffff00"));
        stage = new Stage();
        stage.setFullScreenExitHint("");// 设置空字符串,不弄会出现按esc退出的东东
        stage.setScene(scene);
        stage.setFullScreen(true);// 全屏
        stage.initStyle(StageStyle.TRANSPARENT);// 透明
        stage.show();

        drag(an);// 矩形拖拉
        //按esc退出
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                stage.close();
                stage.setIconified(false);
            }
        });

    }

    // 矩形拖拉
    public void drag(AnchorPane an) {
        //是否点击
        an.setOnMousePressed(event -> {
            an.getChildren().clear();
            vBox = new VBox();
            vBox.setBackground(null);
            vBox.setBorder(new Border(new BorderStroke(Paint.valueOf("#CD3700"), BorderStrokeStyle.SOLID, null,
                    new BorderWidths(2))));

            // 获取起点坐标
            sceneX_start = event.getSceneX();
            sceneY_start = event.getSceneY();
            an.getChildren().add(vBox);
            AnchorPane.setLeftAnchor(vBox, sceneX_start);
            AnchorPane.setTopAnchor(vBox, sceneY_start);
        });

        // 拖拽检测
        an.setOnDragDetected(event -> an.startFullDrag());

        // 获取坐标
        an.setOnMouseDragOver((EventHandler<MouseEvent>) event -> {
            Label label = new Label();
            label.setAlignment(Pos.CENTER);
            label.setPrefWidth(170);
            label.setPrefHeight(30);
            an.getChildren().add(label);
            AnchorPane.setLeftAnchor(label, sceneX_start);
            AnchorPane.setTopAnchor(label, sceneY_start - label.getPrefHeight());
            label.setTextFill(Paint.valueOf("#ffffff"));
            label.setStyle("-fx-background-color:#000000");
            double sceneX = event.getSceneX();
            double sceneY = event.getSceneY();
            double width = sceneX - sceneX_start;
            double height = sceneY - sceneY_start;
            vBox.setPrefWidth(width);
            vBox.setPrefHeight(height);
            System.out.println("1:" + vBox.getPrefHeight());
            System.out.println("1:" + vBox.getPrefWidth());
            label.setText("宽度：" + width + "高度：" + height);
        });

        // 当鼠标弄出矩形后，可以通过点击完成，得到截图
        an.setOnMouseDragExited(event -> {
            sceneX_end = event.getSceneX();
            sceneY_end = event.getSceneY();
            System.out.println(sceneX_end);
            System.out.println(sceneY_end);
            Button finish = new Button("完成");
            vBox.getChildren().add(finish);
            vBox.setAlignment(Pos.BOTTOM_RIGHT);

            finish.setOnAction(event1 -> {
                try {
                    getScreenImg();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

    }

    //保存图片
    public void getScreenImg() throws Exception {
        stage.close();// 关闭当前窗口
        double w = sceneX_end - sceneX_start;
        double h = sceneY_end - sceneY_start;

        // 截图
        Robot robot = new Robot();
        Rectangle rec = new Rectangle((int) sceneX_start, (int) sceneY_start, (int) w, (int) h);
        BufferedImage buffimg = robot.createScreenCapture(rec);

        // 将图片显示在面板上
        WritableImage wi = SwingFXUtils.toFXImage(buffimg, null);
        iv.setImage(wi);

        // 获取系统剪切板
        Clipboard cb = Clipboard.getSystemClipboard();

        // 将图片放在剪切板上
        ClipboardContent content = new ClipboardContent();
        content.putImage(wi);
        cb.setContent(content);

        //把图片写入当前文件结点
        int number = (int) (Math.random() * 1000);
        File file = new File(nodeFile.getAbsolutePath() + "\\" + number + "img.png");
        ImageIO.write(buffimg, "png", file);
        PictureNode node = new PictureNode(file);
        ViewerPane.flowPane.getChildren().add(node);
    }

}
