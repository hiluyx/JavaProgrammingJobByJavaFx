package toolpane;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import lombok.Getter;
import mainpane.SeePicturePane;
import mainpane.ViewerPane;
import util.ButtonUtil;
import util.fileUtils.FileTreeLoader;

@Getter
public class FunctionBar extends HBox {
    //按钮
    private Button seePicture = ButtonUtil.createButton("seePicture");
    public static Button upLoad = ButtonUtil.createButton("upLoad");
    //当前文件夹路径
    private final Label path = new Label();

    public FunctionBar() {
        super(10);

        //初始化FunctionBar的一些参数
        initFunctionBar();

        //把buttons加到FunctionBar
        addButton2Bar();

        //给按钮添加功能
        addFunction2Button();

        //初始化按钮不可用
        setButtonDisable();
    }

    //初始化FunctionBar的一些参数
    private void initFunctionBar(){
        //设置文件路径的padding
        path.setMaxWidth(800);
        path.setMinWidth(800);
        path.setPadding(new Insets(15,0,0,0));
        this.setPadding(new Insets(10, 10, 10, 10));
        this.setStyle("-fx-background-color: White;");
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
        upLoadFunction();
    }

    //初始化button不可用
    private void setButtonDisable(){
        this.seePicture.setDisable(true);
        upLoad.setDisable(true);
    }

    //图片查看按钮的功能
    private void seePictureFunction(){
        this.seePicture.setOnMouseClicked(event -> new SeePicturePane(ViewerPane.currentTreeNode.getValue().getImages().get(0),ViewerPane.currentTreeNode.getValue().getImages().get(0).getName()));
    }

    //上传按钮的功能
    private void upLoadFunction(){
        this.upLoad.setOnAction(event -> FileTreeLoader.postCloudImages());
    }

}
