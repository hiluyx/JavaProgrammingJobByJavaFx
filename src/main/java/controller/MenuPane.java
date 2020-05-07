package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import model.PictureNode;
import util.ButtonUtil;
import util.ClipboardUtil;
import util.fileUtils.FileTreeLoader;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author helefeng
 * @date 2020/4/20 10:45 上午
 */
@Getter
@Setter
public class MenuPane extends MenuItem {

    //用于信息交互
    public static int status = -1;
    private PictureNode pictureNodeOfThisMenu;
    public static File recycleBin = new File("recycleBin");

    //功能按钮
    private MenuItem copy = new MenuItem("复制");
    private MenuItem cut = new MenuItem("剪切");
    private MenuItem delete = new MenuItem("删除");
    private MenuItem reName = new MenuItem("重命名");
    private MenuItem seePicture = new MenuItem("查看");
    private MenuItem upLoad = new MenuItem("上传");
    private MenuItem attribute = new MenuItem("属性");
    private MenuItem lock = new MenuItem("锁定");

    private ContextMenu contextMenu = new ContextMenu();

    //构造方法
    public MenuPane(){
        //把所有功能加进contextMenu
        contextMenu.getItems().addAll(copy,cut,delete,reName,seePicture,upLoad,attribute,lock);
        //把功能加到对应的按钮中
        addFunction2Button();
        //设置快捷键
        shortcut();
        recycleBin.mkdir();
    }

    public MenuPane(PictureNode pictureNodeOfThisMenu) {
        this();
        this.pictureNodeOfThisMenu = pictureNodeOfThisMenu;
    }

    //把功能加到对应的按钮中
    private void addFunction2Button(){
        copyFunction();
        cutFunction();
        deleteFunction();
        seePictureFunction();
        reNameFunction();
        attributeFunction();
        upLoadFunction();
        lockFunction();
    }

    //设置上传功能
    private void upLoadFunction(){
        this.upLoad.setOnAction(event -> {
            //跳转到另一个函数中
            FileTreeLoader.postCloudImages();
        });
    }

    //设置复制功能
    private void copyFunction(){
        this.copy.setOnAction(event -> {
            //改变状态位
            if(PictureNode.getSelectedPictures().size()>0){
                status = 1;
                new ClipboardUtil();
            }
        });
    }

    //设置剪切功能
    private void cutFunction(){
        this.cut.setOnAction(event -> {
            //改变状态位
            if(PictureNode.getSelectedPictures().size()>0){
                status = 2;
                new ClipboardUtil();
            }
        });
    }

    //设置删除功能
    private void deleteFunction(){
        this.delete.setOnAction(event -> {
            //改变状态位
            MenuPane.status = 3;

            //生成一个询问框并给按钮设置相应的操作
            Stage checkStage =  deleteCheckStage();

            checkStage.show();
        });
    }

    //设置图片查看功能
    private void seePictureFunction(){
        this.seePicture.setOnAction(event -> {
            //调用图片查看窗口类
            new SeePicture(ViewerPane.currentTreeNode.getValue().getImages().get(0),ViewerPane.currentTreeNode.getValue().getImages().get(0).getName());
        });
    }

    //设置属性查看功能
    private void attributeFunction(){
        this.attribute.setOnAction(event -> {
            if(PictureNode.getSelectedPictures().size()>0){
                for(PictureNode each:PictureNode.getSelectedPictures()){
                    GridPane gridPane = new GridPane();
                    /*后缀名*/
                    String fileName = each.getFile().getName();
                    String[] strArray = fileName.split("\\.");
                    int suffixIndex = strArray.length-1;

                    Label name =
                            new Label("文件名："+fileName.substring(0,
                                    fileName.lastIndexOf(".")));
                    Label type = new Label("种类："+strArray[suffixIndex]);
                    /*大小*/
                    Label size = new Label("大小："+each.getFile().length()+"字节");
                    /*位置*/
                    Label path =
                            new Label("位置："+each.getFile().getParent());
                    /*创建时间与修改时间*/
                    Date createTimeDate = null;
                    Date lastModfiyTimeDate = null;
                    Path filePath = Paths.get(each.getFile().getAbsolutePath());
                    BasicFileAttributeView basicFileAttributeView = Files
                            .getFileAttributeView(filePath,
                                    BasicFileAttributeView.class,
                                    LinkOption.NOFOLLOW_LINKS);
                    BasicFileAttributes attr;

                    try{
                        attr = basicFileAttributeView.readAttributes();
                        lastModfiyTimeDate =
                                new Date(attr.lastModifiedTime().toMillis());
                        createTimeDate = new Date(attr.creationTime().toMillis());
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//12小时制
                    Date createDate = new Date();
                    Date lastModifyDate = new Date();
                    createDate.setTime(createTimeDate.getTime());
                    lastModifyDate.setTime(lastModfiyTimeDate.getTime());
                    Label createdTime =
                            new Label("创建时间："+simpleDateFormat.format(createDate));
                    Label lastModfiyTime =
                            new Label("修改时间："+simpleDateFormat.format(lastModifyDate));
                    System.out.println(each.getLocked());
                    String eachStatus= each.getLocked()?"不可更改": "可更改";
                    Label status = new Label("状态："+eachStatus);
                    gridPane.setAlignment(Pos.CENTER);
                    gridPane.setPadding(new Insets(10, 10, 10, 10));
                    gridPane.setVgap(15);
                    gridPane.setHgap(15);
                    gridPane.add(name,0,0);

                    gridPane.add(type,0,1);
                    gridPane.add(size,0,2);
                    gridPane.add(path,0,3);
                    gridPane.add(createdTime,0,4);
                    gridPane.add(lastModfiyTime,0,5);
                    gridPane.add(status,0,6);
                    gridPane.setStyle("-fx-background-color: White;");
                    gridPane.setMinSize(300,300);
                    Scene scene = new Scene(gridPane);
                    Stage stage = new Stage();
                    stage.getIcons().add(new Image("file:" + new File("icon/stageIcon.png"), 30, 30,
                            true, true));
                    stage.setTitle("属性");
                    stage.setScene(scene);
                    stage.show();
                }
            }
        });
    }

    //设置锁定与解锁功能
    private void lockFunction(){
        lock.setOnAction(event -> {
//            if (this.lock.getText().equals("锁定")){
//                for(PictureNode each:PictureNode.getSelectedPictures()){
//                    each.setLocked(true);
//                    each.getMenuPane().lock.setText("解锁");
//                    each.getMenuPane().disableFunction();
//                    each.setStyle("-fx-background-color:#cdcdcd;");
//                    PictureNode.getLockedPictures().add(each);
//                }
//                PictureNode.getSelectedPictures().clear();
//            }
//            else {
//                this.pictureNodeOfThisMenu.setLocked(false);
//                this.pictureNodeOfThisMenu.getMenuPane().lock.setText("锁定");
//                this.pictureNodeOfThisMenu.getMenuPane().enableFunction();
//                this.pictureNodeOfThisMenu.setStyle("-fx-background-color:White;");
//                PictureNode.getLockedPictures().remove(this.pictureNodeOfThisMenu);
//            }
            if (PictureNode.getSelectedPictures().size() > 0) {
                for (PictureNode each : PictureNode.getSelectedPictures()) {
                    if (each.getLocked() == false) {
                        each.setLocked(true); each.getMenuPane().lock.setText("解锁");
                        each.setStyle("-fx-background-color: lightgray");
                        each.getMenuPane().disableFunction();
                    }else{
                        each.setLocked(false);each.getMenuPane().lock.setText("锁定");
                        each.setStyle("-fx-background-color: transparent");
                        each.getMenuPane().enableFunction();
                    }
                }

            }
        });
    }

    //设置重命名功能
    private void reNameFunction() {
        this.reName.setOnAction(event -> {
            boolean single;
            GridPane grid = new GridPane();
            Button submit = ButtonUtil.createButton("submit");
            Label msg = new Label();
            Label label1 = new Label("名称");
            Stage anotherStage = new Stage();
            TextField name = new TextField();
            TextField startNum = new TextField();
            TextField bitNum = new TextField();

            //单选多选
            single = PictureNode.getSelectedPictures().size() == 1;

            grid.setPadding(new Insets(10, 10, 10, 10));
            grid.setVgap(4);
            grid.setHgap(4);
            GridPane.setConstraints(label1, 0, 0);
            name.setPromptText("请输入新名字");
            name.setPrefColumnCount(20);
            name.getText();
            GridPane.setConstraints(name, 1, 0);
            grid.getChildren().addAll(label1, name);
            grid.setStyle("-fx-background-color:White;");
            if (single) {
                GridPane.setConstraints(msg, 1, 1);
                GridPane.setConstraints(submit, 2, 1);
                grid.getChildren().addAll(submit, msg);
            } else {
                Label label2 = new Label("起始编号");
                GridPane.setConstraints(label2, 0, 1);
                startNum.setPromptText("请输入起始编号");
                startNum.setPrefColumnCount(15);
                startNum.getText();
                GridPane.setConstraints(startNum, 1, 1);
                Label label3 = new Label("编号位数");
                GridPane.setConstraints(label3, 0, 2);
                bitNum.setPromptText("请输入编号位数");
                bitNum.setPrefColumnCount(10);
                bitNum.getText();
                GridPane.setConstraints(bitNum, 1, 2);
                GridPane.setConstraints(msg, 1, 3);
                GridPane.setConstraints(submit, 2, 3);
                grid.getChildren().addAll(label2, startNum, submit, msg,label3,bitNum);
            }

            submit.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    if (single) {
                        if ((name.getText() != null && !name.getText().isEmpty())) {
                            if (renameSingle(name.getText())) {
                                anotherStage.close();
                            } else {
                                msg.setText("已有该名字的图片存在，请重新输入");
                            }
                        } else {
                            msg.setText("请输入!");
                        }
                    } else {
                        if ((name.getText() != null && !name.getText().isEmpty())
                                && (startNum.getText() != null && !startNum.getText().isEmpty())
                                &&(bitNum.getText() != null && !bitNum.getText().isEmpty())){
                            if (renameMore(name.getText(),startNum.getText(),bitNum.getText())) {
                                anotherStage.close();
                            } else {
                                msg.setText("错误！请重新输入");
                            }
                        } else {
                            msg.setText("你没有输入，请输入!");
                        }
                    }
                }
            });
            Scene scene = new Scene(grid);
            anotherStage.setTitle("重命名");
            anotherStage.getIcons().add(new Image("file:"+new File("icon/stageIcon.png"),30, 30,
                    true, true));
            anotherStage.setScene(scene);
            anotherStage.initModality(Modality.APPLICATION_MODAL);
            anotherStage.show();

        });
    }

    //创建名字(重命名功能用到的函数)
    private String createName(String newFileName,int id,int bit) {
        StringBuilder newName = new StringBuilder(newFileName);
        int startNum = id;
        int linBit = 0;
        if(startNum == 0)  linBit++;
        while(startNum!=0) {
            linBit++;
            startNum/=10;
        }
        while(bit>linBit) {
            newName.append(0);
            linBit++;
        }
        newName.append(id);
        return newName.toString();
    }

    //重命名单个文件
    private boolean renameSingle(String newFileName) {
        PictureNode oldNode = PictureNode.getSelectedPictures().get(0);
        File file = oldNode.getFile();
        String FileName = file.getParent()+"\\" + newFileName + file.getName().substring(file.getName().lastIndexOf("."));
        File newFile = new File(FileName);
        if(!file.renameTo(newFile)) {
            newFile.delete();
            return false;
        }
        PictureNode newNode = new PictureNode(newFile);
        PictureNode.getSelectedPictures().remove(0);
        ViewerPane.flowPane.getChildren().remove(oldNode);
        ViewerPane.flowPane.getChildren().add(newNode);
        return true;
    }

    //重命名多个文件
    private boolean renameMore(String newFileName,String startNum,String bitNum) {
        File file;
        int id = Integer.parseInt(startNum);
        int bit = Integer.valueOf(bitNum);
        ArrayList<PictureNode> oldList = new ArrayList<>();
        ArrayList<PictureNode> newList = new ArrayList<>();

        for (PictureNode picture : PictureNode.getSelectedPictures()) {
            file = picture.getFile();
            String newname = createName(newFileName, id++, bit);
            String FileName = file.getParent()+ "\\" + newname +file.getName().substring(file.getName().lastIndexOf("."));
            File newFile = new File(FileName);
            if(!file.renameTo(newFile)) {
                newFile.delete();
                return false;
            }
            oldList.add(picture);
            PictureNode newImage = new PictureNode(newFile);
            newList.add(newImage);
        }
        for(int i=0; i<oldList.size(); i++) {
            PictureNode.getSelectedPictures().remove(0);
            ViewerPane.flowPane.getChildren().remove(oldList.get(i));
            ViewerPane.flowPane.getChildren().add(newList.get(i));
        }
        return true;
    }

    //复制文件的函数
    public void copyFile(String srcPath, String destPath) throws IOException {
        // 打开输入流
        FileInputStream fis = new FileInputStream(srcPath);
        // 打开输出流
        FileOutputStream fos = new FileOutputStream(destPath);

        // 读取和写入信息
        int len = 0;
        while ((len = fis.read()) != -1) {
            fos.write(len);
        }

        // 关闭流  先开后关  后开先关
        fos.close(); // 后开先关
        fis.close(); // 先开后关

    }

    //给功能加上快捷键
    private void shortcut(){
        copy.setAccelerator(KeyCombination.valueOf("shift+c"));
        cut.setAccelerator(KeyCombination.valueOf("shift+t"));
        delete.setAccelerator(KeyCombination.valueOf("shift+d"));
        reName.setAccelerator(KeyCombination.valueOf("shift+r"));
        seePicture.setAccelerator(KeyCombination.valueOf("shift+o"));
        upLoad.setAccelerator(KeyCombination.valueOf("shift+u"));
        attribute.setAccelerator(KeyCombination.valueOf("shift+i"));
        lock.setAccelerator(KeyCombination.valueOf("shift+l"));
    }

    //生成删除时确认的Stage
    private Stage deleteCheckStage() {
        //以下为一些页面布局，具体功能就是实现删除时的确认
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: White;");
        Label label = new Label("是否删除");
        label.setFont(new Font(30));
        HBox hBox = new HBox(25);
        Button yes = ButtonUtil.createButton("submit");
        Button no = ButtonUtil.createButton("cancel");
        yes.setPrefWidth(50);
        no.setPrefWidth(50);
        hBox.getChildren().add(yes);
        hBox.getChildren().add(no);
        hBox.setAlignment(Pos.BOTTOM_CENTER);
        hBox.setPadding(new Insets(5, 5, 10, 5));
        root.setCenter(label);
        root.setBottom(hBox);
        Scene scene = new Scene(root, 450, 150);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.getIcons().add(new Image("file:" + new File("icon/stageIcon.png"), 30, 30,
                true, true));
        stage.setTitle("确认删除");

        //确认删除
        yes.setOnMouseClicked(event1 -> {
            stage.close();
            int num = 0;
            //把要删除的照片移动到回收站
            ViewerPane.bottom.getChildren().add(ViewerPane.progressBarWindow.getProgressBar());
            NoSelectedMenuPane.everyRevocationNum.add(PictureNode.getSelectedPictures().size());

            for (PictureNode each : PictureNode.getSelectedPictures()) {
                if(each.getFile().getParent().equals(new File("cloudAlbum").getAbsolutePath())){
                    FileTree.deletedCloudImages.add(each.getFile());
                }

                String srcPath = each.getFile().getAbsolutePath();
                String destPath =
                        recycleBin.getAbsolutePath() + "/"
                                + each.getFile().getName();
                NoSelectedMenuPane.revocationPictureFiles.add(each.getFile());
                try {
                    copyFile(srcPath, destPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (each.getFile().delete()) {
                    System.out.printf("第%d张图片删除成功\n", ++num);
                    ViewerPane.flowPane.getChildren().remove(each);
                }
            }
        });

        //取消删除
        no.setOnMouseClicked(event1 -> {
            stage.close();
        });

        return stage;
    }

    private void disableFunction(){
        this.copy.setDisable(true);
        this.cut.setDisable(true);
        this.delete.setDisable(true);
        this.reName.setDisable(true);
        this.seePicture.setDisable(true);
        this.upLoad.setDisable(true);
        this.attribute.setDisable(true);
    }

    private void enableFunction(){
        this.copy.setDisable(false);
        this.cut.setDisable(false);
        this.delete.setDisable(false);
        this.reName.setDisable(false);
        this.seePicture.setDisable(false);
        this.upLoad.setDisable(false);
        this.attribute.setDisable(false);
    }
}