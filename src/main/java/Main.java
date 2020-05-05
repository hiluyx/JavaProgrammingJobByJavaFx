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
import util.httpUtils.HttpUtil;

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
            int width = 1350;
            int height = (int) (width*0.65);
            SplitPane root = new SplitPane();
            ViewerPane vi = new ViewerPane();
            FileTree fileTree = new FileTree(vi);
            root.getItems().addAll(fileTree.getTreeView(), vi);
            root.setDividerPositions(0.25);
            SplitPane.setResizableWithParent(fileTree.getTreeView(), false);
            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(getClass().getResource("test.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(new Image("file:"+new File("icon/图标.png"),30, 30,
                    true, true));
            primaryStage.setTitle("电子图片管理程序--by互抱大腿的小弟们");
            //设置关闭程序时要执行的操作
            primaryStage.setOnCloseRequest(event -> {
                System.out.println("即将关闭程序");
                try {
                    System.out.println("关闭网络连接");
                    HttpUtil.client.close();
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
