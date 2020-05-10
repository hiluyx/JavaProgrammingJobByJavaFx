package mainpane;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.TreeNode;
import util.ButtonUtil;

import java.io.File;

public class PPTPane {

    //组件
    private Timeline timeline;
    private ImageView imageView = new ImageView();
    private BorderPane borderPane;
    private Button star = ButtonUtil.createButton("star");
    private Button end = ButtonUtil.createButton("end");
    private Button leave = ButtonUtil.createButton("leave");

    //其他成员
    private Stage stage;
    private int count = 0;
    private TreeNode treeNode;
    private Pagination pagination;
    private SimpleObjectProperty<TreeNode> currentTreeNode;

    public PPTPane(TreeNode treeNode){

        //初始化
        initPPTPane(treeNode);

        //展示
        show();
    }

    //初始化
    private void initPPTPane(TreeNode treeNode){
        this.treeNode = treeNode;
        this.treeNode.setImages();
        currentTreeNode = new SimpleObjectProperty<>(treeNode);
        pagination = new Pagination(treeNode.getImages().size());
        pagination.setPrefSize(50,50);
        HBox hBox = new HBox(50);
        hBox.setAlignment(Pos.BOTTOM_CENTER);
        hBox.getChildren().add(star);
        hBox.getChildren().add(end);
        borderPane = new BorderPane();
        borderPane.setTop(pagination);
        borderPane.setCenter(imageView);
        borderPane.setBottom(hBox);
        borderPane.setLeft(leave);
        borderPane.setStyle("-fx-background-color: White;");
        Scene scene = new Scene(borderPane);
        stage = new Stage();
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.getIcons().add(new Image("file:"+new File("icon/stageIcon.png"),30, 30,
                true, true));
        stage.setTitle("幻灯片播放");
        stage.show();
        star.setOnMouseClicked(e-> timeline.play() );
        end.setOnMouseClicked(e -> timeline.pause() );
        leave.setOnMouseClicked(e -> stage.close() );
    }

    //展示
    public void show() {

        timeline = new Timeline();

        timeline.setCycleCount(Timeline.INDEFINITE);

        KeyValue keyValue1 = new KeyValue(imageView.opacityProperty(),1);
        KeyValue keyValue2 = new KeyValue(imageView.opacityProperty(),0.1);
        Duration duration = Duration.seconds(3);



        KeyFrame keyFrame = new KeyFrame(duration, event -> {
            if (count < currentTreeNode.getValue().getImages().size()) {
                pagination.setCurrentPageIndex(count);//设置当前页面索引
                pagination.setPickOnBounds(false);
                imageView.setImage(new Image("file:" + currentTreeNode.getValue().getImages().get(count)));
                imageView.setFitWidth(1350);
                imageView.setFitHeight(1350*0.65);
            } else if (count == currentTreeNode.getValue().getImages().size()) {
                count = 0;
                imageView.setOpacity(1);
                timeline.stop();
            }
            count++;
        },keyValue1,keyValue2);

        timeline.getKeyFrames().add(keyFrame);
    }

}
