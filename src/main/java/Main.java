import controller.FileTreePane;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        try {
            BorderPane root = new BorderPane();
            Scene scene = new Scene(root, 900, 700);
            primaryStage.setScene(scene);
            FileTreePane fileTreePane = new FileTreePane();
            root.setLeft(fileTreePane);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
