package controller;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import lombok.Getter;
import util.ButtonUtil;

@Getter
public class FunctionBar extends HBox {
    //按钮
    private Button seePicture = ButtonUtil.createButton("seePicture");
    public static Button upLoad = ButtonUtil.createButton("upLoad");
    private final Label path = new Label();

    public FunctionBar() {
        super(10);
        //设置padding

        path.setMaxWidth(800);
        path.setMinWidth(800);
        path.setPadding(new Insets(15,0,0,0));

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
        this.getChildren().add(upLoad);
        this.getChildren().add(path);

    }

    //给button加上功能
    private void addFunction2Button(){
        seePictureFunction();
    }

    //初始化button不可用
    private void setButtonDisable(){
        this.seePicture.setDisable(true);
        this.upLoad.setDisable(true);
    }

    private void seePictureFunction(){
        this.seePicture.setOnMouseClicked(event -> {
            new SeePicture(ViewerPane.currentTreeNode.getValue().getImages().get(0),ViewerPane.currentTreeNode.getValue().getImages().get(0).getName());
        });
    }



}
