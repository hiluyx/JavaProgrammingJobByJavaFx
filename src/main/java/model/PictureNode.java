package model;

import controller.MenuPane;
import controller.SeePicture;
import controller.ViewerPane;
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;
import lombok.Data;

import java.io.File;
import java.util.ArrayList;

@Data
public class PictureNode extends Label {
    private File file;//图片对应的文件
    private Image image;//由文件加载出来的Image
    private ImageView imageView;
    private Text pictureName;

    private MenuPane menuPane = new MenuPane();

    public int count = 0;

    //保存被点击图片节点，图片节点中包含图片数据
    protected static ArrayList<PictureNode> selectedPictures = new ArrayList<>();


    public PictureNode(File aPictureFile) {
        this.file = aPictureFile;
        根据参数初始化图片节点(aPictureFile);
        给图片节点添加点击事件监听器();

    }

    private void 给图片节点添加点击事件监听器(){
        /**无法实现点击空白取消所有高亮，因为不是在这个this上处理的*/
        this.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                System.out.println("单击了:"+this.file.getName());
                //记录该节点被点击的总次数
                this.count += e.getClickCount();
                //如果control键没有按下
                if (!e.isControlDown()) {
                    for (PictureNode each : selectedPictures) {
                        each.setStyle("-fx-background-color: transparent;");
                        each.setCount(0);
                    }
                    selectedPictures.clear();
                }
                if (this.count % 2 == 1) {
                    this.setStyle("-fx-background-color: #8bb9ff;");
                    this.count=0;
                    selectedPictures.add(this);
                } else {
                    this.setStyle("-fx-background-color: transparent;");
                    selectedPictures.remove(this);
                }
                System.out.println("选中的数量：" + selectedPictures.size());
                显示选了多少张();
                if (this.getStyle().equals("-fx-background-color: #8bb9ff;")) {
                    this.setContextMenu(menuPane.getContextMenu());
                }
            }

            if (selectedPictures.size()>0){
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
            //双击图片进入查看界面
            if (e.getClickCount() == 2) {
                //e.getClickCount() == 2,双击
                //创建一个SeePicture类，传参数进去
                System.out.println("双击了:"+this.file.getName());
                new SeePicture(this.file, this.file.getName());
            }
        });
    }

    private void 显示选了多少张(){
        ViewerPane.选中多少张.setText(String.format("-选中%d张",PictureNode.selectedPictures.size()));
    }

    private void 根据参数初始化图片节点(File aPictureFile){
        this.setPickOnBounds(true);
        this.setGraphicTextGap(10);
        this.setPadding(new Insets(10, 10, 10, 10));
        this.setContentDisplay(ContentDisplay.TOP);
        this.setPrefSize(110, 110);

        this.image = new Image("file:" + aPictureFile.getAbsolutePath(), 100, 100,
                true, true);
        this.imageView = new ImageView(image);
        this.pictureName = new Text(aPictureFile.getName());
        this.setText(pictureName.getText());
        this.setGraphic(imageView);
    }


    public static ArrayList<PictureNode> getSelectedPictures() {
        return selectedPictures;
    }

    public void setCount(){
        this.count = 0;
    }

}
