import controller.FileTree;
import controller.ViewerPane;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {

            SplitPane root = new SplitPane();

            ViewerPane vi = new ViewerPane();
            FileTree fileTree = new FileTree(vi);

            root.getItems().addAll(fileTree.getTreeView(), vi);

            root.setDividerPositions(0.275);
            SplitPane.setResizableWithParent(fileTree.getTreeView(), false);
            Scene scene = new Scene(root, 900, 700);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
