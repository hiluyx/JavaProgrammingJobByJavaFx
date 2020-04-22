package controller;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.BoundsAccessor;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.TreeNode;

import java.io.File;
import java.net.MalformedURLException;

public class PPT {
    private Timeline timeline;
    private int count = 0;
    private int index;
    private ImageView imageView = new ImageView();
    private BorderPane borderPane;
    private Stage stage;
    private Button star = new Button("star");
    private Button end = new Button("end");
    private Button leave = new Button("leave");
    private SimpleObjectProperty<TreeNode> selectedFolderProperty = new SimpleObjectProperty<>();

    public PPT() {

        timeline = new Timeline();

        timeline.setCycleCount(Timeline.INDEFINITE);

        KeyValue keyValue = new KeyValue(imageView.scaleXProperty(), 2);
        KeyValue keyValue2 = new KeyValue(imageView.scaleYProperty(), 2);
        Duration duration = Duration.seconds(3);

        EventHandler<ActionEvent> onFinished = (ActionEvent t) -> {

            if (count < selectedFolderProperty.getValue().getImages().size()) {
                imageView.setImage(new Image("file:" + selectedFolderProperty.getValue().getImages().get(count)));

            } else if (count == selectedFolderProperty.getValue().getImages().size()) {
                count = 0;
                timeline.stop();
            }
            count++;
        };


        KeyFrame keyFrame1 = new KeyFrame(duration, onFinished, keyValue, keyValue2);

        timeline.getKeyFrames().add(keyFrame1);

        star.setOnMouseClicked(e -> {
            timeline.play();
        });
        end.setOnMouseClicked(e -> {
            timeline.stop();
        });

        leave.setOnMouseClicked(event -> {
            close();
        });

    }

    public void setSelectedFolder(TreeNode selectedFolder) {
        this.selectedFolderProperty.set(selectedFolder);
    }

    /*
            Image image = new Image("file:G:\\0tjx\\1.png");
            ImageView imageView = new ImageView(image);
            BorderPane borderPane = new BorderPane();
            Scene scene = new Scene(borderPane);
            borderPane.setCenter(imageView);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        }

     */
    public void show() {
        borderPane = new BorderPane();
        Scene scene = new Scene(borderPane);
        borderPane.setCenter(imageView);
        HBox hBox = new HBox(10);
        hBox.getChildren().add(star);
        hBox.getChildren().add(end);
        borderPane.setBottom(hBox);
        borderPane.setLeft(leave);
        stage = new Stage();
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public void close() {
        stage.close();
    }
}
