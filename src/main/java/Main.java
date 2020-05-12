import javafx.event.EventHandler;
import javafx.scene.control.SplitPane;
import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javafx.stage.WindowEvent;
import mainpane.ViewerPane;
import util.InitMainPaneUtil;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            //创建左界面
            BorderPane left = InitMainPaneUtil.createLeft();
            //创建右界面
            ViewerPane right = InitMainPaneUtil.createRight();
            //创建主界面
            SplitPane root = InitMainPaneUtil.createRoot(left,right);
            //创建初始化primaryStage
            InitMainPaneUtil.initStage(primaryStage,root);
            primaryStage.setOnCloseRequest(event -> System.exit(0));
            //show
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
