package util;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import mainpane.FileTreePane;
import mainpane.ViewerPane;
import util.fileUtils.FileTreeLoader;
import util.httpUtils.HttpUtil;
import util.httpUtils.exception.RequestConnectException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

//用来初始化主界面组件的工具类
public class InitMainPaneUtil {

    //创建左界面
    public static BorderPane createLeft() throws IOException {
        FileTreePane fileTree = new FileTreePane();
        BorderPane left = new BorderPane();
        HBox myComputer = new HBox(10);
        myComputer.setStyle("-fx-background-color:#ffffff;");
        myComputer.setPadding(new Insets(5,0,5,5));
        ImageView computer = new ImageView(new Image("file:"+new File("icon/computer.png"),30, 30,
                true, true));
        Label myCom = new Label("我的电脑");
        myCom.setFont(new Font(18));
        myCom.setPadding(new Insets(5,30,0,0));

        Button could = ButtonUtil.createButton("could");
        could.setPadding(new Insets(5,0,0,0));
        could.setText("云相册");
        addListener2Could(could,fileTree);

        myComputer.getChildren().addAll(computer,myCom,could);
        left.setTop(myComputer);
        fileTree.getTreeView().setStyle("-fx-background-color:#ffffff;");
        left.setCenter(fileTree.getTreeView());
        return left;
    }

    //创建右界面
    public static ViewerPane createRight(){
        return new ViewerPane();
    }

    //创建主界面
    public static SplitPane createRoot(BorderPane left, ViewerPane right){
        SplitPane root = new SplitPane();
        root.getItems().addAll(left, right);
        root.setDividerPositions(0.25);
        SplitPane.setResizableWithParent(left, false);
        return root;
    }

    //创建初始化primaryStage
    public static void initStage(Stage primaryStage, SplitPane root){
        int width = 1350;
        int height = (int) (width*0.65);
        Scene scene = new Scene(root, width, height);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("file:"+new File("icon/stageIcon.png"),30, 30,
                true, true));
        primaryStage.setTitle("电子图片管理程序--第三组 何乐烽 卢运喜 孙考毅 张宏林");
        //设置关闭程序时要执行的操作
        primaryStage.setOnCloseRequest(event -> {
            //System.out.println("即将关闭程序");
            try {
                //删除云相册数据
                File cloudAlbum = new File("cloudAlbum");
                File[] filesOfCloudAlbum = cloudAlbum.listFiles();
                if(filesOfCloudAlbum != null){
                    for(File each:filesOfCloudAlbum){
                        //System.out.println("删除本地"+each.getName()+":"+each.delete());
                        each.delete();
                    }
                }
               // System.out.println("本地云相册删除："+cloudAlbum.delete());
                cloudAlbum.delete();
               //  System.out.println("关闭网络连接");
                HttpUtil.client.close();
                //删除回收站数据
                File recycleBin = new File("recycleBin");
                File[] filesOfRecycleBin = recycleBin.listFiles();
                if(filesOfRecycleBin != null){
                    for(File each:filesOfRecycleBin){
                        //System.out.println("删除"+each.getName()+":"+each.delete());
                        each.delete();
                    }
                }
                //System.out.println("回收站删除："+recycleBin.delete());
                recycleBin.delete();

            }catch (Exception exception){
                exception.printStackTrace();
            }
            while(true){
                try {
                    HttpUtil.doDelete(FileTreePane.deletedCloudImages);
                } catch (URISyntaxException uriSyntaxException) {
                    uriSyntaxException.printStackTrace();
                } catch (RequestConnectException exception) {
                    if (exception.getDialogSel(exception))
                        break;
                    else continue;
                }
                break;
            }
        });
    }

    //云相册下载
    private static void addListener2Could(Button could, FileTreePane fileTree){
        could.setOnAction(event -> {
            if (!fileTree.isOpened()) FileTreeLoader.getCloudImages(fileTree);
        });
    }


}
