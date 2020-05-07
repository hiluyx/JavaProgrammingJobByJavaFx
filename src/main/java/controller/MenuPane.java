package controller;

import javafx.application.Platform;
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
import util.TaskThreadPools;
import util.fileUtils.FileTreeLoader;
import util.httpUtils.HttpUtil;
import util.httpUtils.exception.RequestConnectException;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author helefeng
 * @date 2020/4/20 10:45 上午
 */
@Getter
@Setter
public class MenuPane extends MenuItem {

    public static int status = -1;
    public static File recycleBin = new File("recycleBin");


    private MenuItem copy = new MenuItem("复制");
    private MenuItem cut = new MenuItem("剪切");
    private MenuItem delete = new MenuItem("删除");
    private MenuItem reName = new MenuItem("重命名");
    private MenuItem seePicture = new MenuItem("查看");
    private MenuItem upLoad = new MenuItem("上传");
    private MenuItem attribute = new MenuItem("属性");
    private MenuItem lock = new MenuItem("锁定");
    private ContextMenu contextMenu = new ContextMenu();
    private PictureNode pictureNodeOfThisMenu;
    public MenuPane(PictureNode pictureNodeOfThisMenu) {
        //把所有功能加进contextMenu
        contextMenu.getItems().addAll(copy, cut, delete, reName, seePicture,upLoad,attribute,lock);
        addFunction2Button();
        shortcut();
        recycleBin.mkdir();
        this.pictureNodeOfThisMenu = pictureNodeOfThisMenu;
    }

    public MenuPane(){
        contextMenu.getItems().addAll(copy, cut, delete, reName, seePicture,upLoad,attribute,lock);
        addFunction2Button();
        shortcut();
        recycleBin.mkdir();
    }
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

    private void upLoadFunction(){
        this.upLoad.setOnAction(event -> {
            FileTreeLoader.postCloudImages();
        });
    }

    private void copyFunction(){
        this.copy.setOnAction(event -> {
            if(PictureNode.getSelectedPictures().size()>0){
                status = 1;
                new ClipboardUtil();
            }
        });
    }
    private void cutFunction(){
        this.cut.setOnAction(event -> {
            if(PictureNode.getSelectedPictures().size()>0){
                status = 2;
                new ClipboardUtil();
            }
        });
    }

    private void deleteFunction(){
        this.delete.setOnAction(event -> {
            MenuPane.status = 3;

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
            hBox.setPadding(new Insets(5,5,10,5));
            root.setCenter(label);
            root.setBottom(hBox);
            Scene scene = new Scene(root,450,150);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.getIcons().add(new Image("file:"+new File("icon/stageIcon.png"),30, 30,
                    true, true));
            stage.setTitle("删除确认");
            stage.show();
            yes.setOnMouseClicked(event1 -> {
                stage.close();
                int num = 0;
                NoSelectedMenuPane.everyRevocationNum.add(PictureNode.getSelectedPictures().size());
                for(PictureNode each:PictureNode.getSelectedPictures()){

                    String srcPath = each.getFile().getAbsolutePath();
                    String destPath =
                            recycleBin.getAbsolutePath()+"/"
                                    +each.getFile().getName();
                    NoSelectedMenuPane.revocationPictureFiles.add(each.getFile());
                    try {
                        copyFile(srcPath,destPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(each.getFile().delete()){
                        System.out.printf("第%d张图片删除成功",++num);
                        ViewerPane.flowPane.getChildren().remove(each);
                    }
                }
            });
            no.setOnMouseClicked(event1 -> {
                stage.close();
            });
        });
    }
    private void seePictureFunction(){
        this.seePicture.setOnAction(event -> {
            new SeePicture(ViewerPane.currentTreeNode.getValue().getImages().get(0),ViewerPane.currentTreeNode.getValue().getImages().get(0).getName());
        });
    }

    private void attributeFunction(){
        attribute.setOnAction(event -> {
            GridPane 属性面板 = new GridPane();
            PictureNode e = PictureNode.getSelectedPictures().get(0);
            Label 类型 = new Label("类型:"+e.getFile().getName().substring(e.getFile().getName().lastIndexOf(".")));
            Label 大小 = new Label("大小");
            Label 位置 = new Label("位置");
            属性面板.add(类型,0,0);
            Scene scene = new Scene(属性面板);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        });
    }

    private void lockFunction(){
        lock.setOnAction(event -> {
            if (this.lock.getText().equals("锁定")){
                for(PictureNode each:PictureNode.getSelectedPictures()){
                    each.setLocked(true);
                    each.getMenuPane().lock.setText("解锁");
                    each.setStyle("-fx-background-color:#cdcdcd;");
                    PictureNode.getLockedPictures().add(each);
                }
                PictureNode.getSelectedPictures().clear();
            }
            else {
                this.pictureNodeOfThisMenu.setLocked(false);
                this.pictureNodeOfThisMenu.getMenuPane().lock.setText("锁定");
                this.pictureNodeOfThisMenu.setStyle("-fx-background-color:White;");
                PictureNode.getLockedPictures().remove(this.pictureNodeOfThisMenu);
                /*for (PictureNode each:PictureNode.getLockedPictures()){
                    each.setLocked(false);
                    each.getMenuPane().lock.setText("锁定");
                    each.setStyle("-fx-background-color:White;");

                }
                PictureNode.getLockedPictures().clear();*/
            }

        });
    }

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

    //创建名字
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
//        int bit = String.valueOf(PictureNode.getSelectedPictures().size()).length();
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

    private void shortcut(){
        copy.setAccelerator(KeyCombination.valueOf("shift+c"));
        cut.setAccelerator(KeyCombination.valueOf("shift+t"));
        delete.setAccelerator(KeyCombination.valueOf("shift+d"));
        reName.setAccelerator(KeyCombination.valueOf("shift+r"));
        seePicture.setAccelerator(KeyCombination.valueOf("shift+o"));
        upLoad.setAccelerator(KeyCombination.valueOf("shift+u"));
        attribute.setAccelerator(KeyCombination.valueOf("shift+i"));
//        allSelectedMenuItem.setAccelerator(KeyCombination.valueOf("shift+a"));
    }


}