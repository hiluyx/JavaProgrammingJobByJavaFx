package controller;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import lombok.Getter;

import java.io.File;

@Getter
public class FunctionBar extends HBox {
    //按钮
    private Button seePicture = createButton("seePicture");
    private final Label path = new Label();

    public FunctionBar(double spacing) {
        super(10);
        seePicture.setGraphic(new ImageView(new Image("file:"+new File("icon/播放.png"),30, 30,
                true, true)));
        //设置padding
        this.setPadding(new Insets(10, 10, 10, 10));
        this.setStyle("-fx-background-color: White;");
        //把buttons加到ToolBar
        addButton2Bar();
        //给按钮添加功能
        addFunction2Button();
        //不可用
        setButtonDisable();
    }

    //把button加入ToolBar
    private void addButton2Bar(){
        this.getChildren().add(seePicture);
        this.getChildren().add(path);
    }

    //给button加上功能
    private void addFunction2Button(){
        seePictureFunction();
    }

    //初始化button不可用
    private void setButtonDisable(){
        this.seePicture.setDisable(true);
    }

    private void seePictureFunction(){
        this.seePicture.setOnMouseClicked(event -> {
            new SeePicture(ViewerPane.currentTreeNode.getValue().getImages().get(0),ViewerPane.currentTreeNode.getValue().getImages().get(0).getName());
        });
    }

    //创建一个Button，button的文字为函数参数
    private Button createButton(String buttonName) {
        Button button = new Button();
        button.setId(buttonName);
        button.setPadding(new Insets(10, 10, 10, 10));
        //button.setText(buttonName);
        return button;
    }

}
