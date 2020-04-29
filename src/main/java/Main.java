import controller.FileTree;
import controller.ViewerPane;
import javafx.application.Application;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.Mnemonic;
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
            root.setDividerPositions(0.25);
            SplitPane.setResizableWithParent(fileTree.getTreeView(), false);
            Scene scene = new Scene(root, 1500, 927);
            scene.getStylesheets().add(getClass().getResource("test.css").toExternalForm());
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
