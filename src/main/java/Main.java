import controller.FileTree;
import controller.MenuPane;
import controller.ViewerPane;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.Mnemonic;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import org.apache.http.util.Asserts;

import java.io.File;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            MenuPane.recycleBin.mkdir();
            File[] files = MenuPane.recycleBin.listFiles();
            for(int i=0;i<files.length;i++){
                files[i].delete();
            }
            SplitPane root = new SplitPane();
            ViewerPane vi = new ViewerPane();
            FileTree fileTree = new FileTree(vi);
            root.getItems().addAll(fileTree.getTreeView(), vi);
            root.setDividerPositions(0.25);
            SplitPane.setResizableWithParent(fileTree.getTreeView(), false);
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("test.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(new Image("file:"+new File("icon/图标.png"),30, 30,
                    true, true));
            primaryStage.setTitle("互抱大腿的小弟制作的——丑图看看");
            //设置关闭程序时要执行的操作
            primaryStage.setOnCloseRequest(event -> {
                System.out.println("即将关闭程序");
                try {
                    //删除云相册数据
                    File cloudAlbum = new File("cloudAlbum");
                    File[] filesOfCloudAlbum = cloudAlbum.listFiles();
                    if(filesOfCloudAlbum != null){
                        for(File each:filesOfCloudAlbum){
                            System.out.println("删除"+each.getName()+":"+each.delete());
                        }
                    }
                    System.out.println("云相册删除："+cloudAlbum.delete());

                    //删除回收站数据
                    File recycleBin = new File("recycleBin");
                    File[] filesOfRecycleBin = recycleBin.listFiles();
                    if(filesOfRecycleBin != null){
                        for(File each:filesOfRecycleBin){
                            System.out.println("删除"+each.getName()+":"+each.delete());
                        }
                    }
                    System.out.println("回收站删除："+recycleBin.delete());

                }catch (Exception e){
                    e.printStackTrace();
                }
            });
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
