import controller.FileTreePane;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        //
        try {
            FXMLLoader root = new FXMLLoader(getClass().getResource("fxml/main-pane.fxml"));
            primaryStage.setTitle("PhotoView");

//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/file-tree-pane.fxml"));
//            fxmlLoader.setRoot(root.getNamespace().get("fileTreePane"));
//            fxmlLoader.load();

            primaryStage.setScene(new Scene(root.load()));
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(500);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
