package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import model.PictureNode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

/**
 * @author helefeng
 * @date 2020/4/20 10:45 上午
 */
public class MenuPane extends MenuItem {

    public int 状态 = -1;

    @Getter
    private MenuItem copyMenuItem = new MenuItem("复制");
    private MenuItem cutMenuItem = new MenuItem("剪切");
    private MenuItem pasteMenuItem = new MenuItem("粘贴");
    private MenuItem deleteMenuItem = new MenuItem("删除");
    private MenuItem reNameMenuItem = new MenuItem("重命名");
    private MenuItem seePictureMenuItem = new MenuItem("查看");
    private ContextMenu contextMenu = new ContextMenu();

    public MenuPane() {
        contextMenu.getItems().addAll(copyMenuItem, cutMenuItem,pasteMenuItem,deleteMenuItem,reNameMenuItem,seePictureMenuItem);
        copyMenuItem.setOnAction(event -> {
            if(PictureNode.getSelectedPictures().size()>0){
                状态 = 1;
                pasteMenuItem.setDisable(false);
            }
        });
        cutMenuItem.setOnAction(event -> {
            if(PictureNode.getSelectedPictures().size()>0){
                状态 = 2;
                pasteMenuItem.setDisable(false);
            }
        });
        pasteMenuItem.setOnAction(event -> {
            try {
                //srcPath是原路径，destPath是构造出来的目标路径
                // （例如srcPath=G:\0tjx\2.png  destPath =G:\计算机二级\2.png）
                for(PictureNode each:PictureNode.getSelectedPictures()){
                    String srcPath = each.getFile().getAbsolutePath();
                    String destPath = ViewerPane.currentTreeNode.getValue().getFile().getAbsolutePath()+"\\"+each.getFile().getName();
                    //观察路径，无实际作用
                    System.out.println(srcPath);
                    System.out.println(destPath);
                    //网上找的复制代码，直接从文件层面复制
                    copyFile(srcPath,destPath);
                    //添加到flowPane
                    File file = new File(destPath);
                    PictureNode p = new PictureNode(file);
                    ViewerPane.flowPane.getChildren().add(p);
                }
                if(状态==2){//剪切功能
                    int num = 0;
                    for(PictureNode each:PictureNode.getSelectedPictures()){
                        if(each.getFile().delete()){
                            System.out.printf("第%d张图片删除成功",++num);
                            ViewerPane.flowPane.getChildren().remove(each);
                        }
                    }
                }
                PictureNode.getSelectedPictures().clear();
                pasteMenuItem.setDisable(true);
                //    String destPath = ViewerPane.selectedFolderProperty.getValue().getFile().getAbsolutePath()+"\\"+PictureNode.getSelectedPictures().get(0).getFile().getName();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        deleteMenuItem.setOnAction(event -> {
            int num = 0;
            for(PictureNode each:PictureNode.getSelectedPictures()){
                if(each.getFile().delete()){
                    System.out.printf("第%d张图片删除成功",++num);
                    ViewerPane.flowPane.getChildren().remove(each);
                }
            }
        });
        reNameMenuItem.setOnAction(event -> {
            boolean single;
            GridPane grid = new GridPane();
            Button submit = new Button("完成");
            Label msg = new Label();
            Label label1 = new Label("名称");
            Stage anotherStage = new Stage();
            TextField name = new TextField();
            TextField startNum = new TextField();

            if (PictureNode.getSelectedPictures().size() == 1) {
                single = true;
            } else {
                single = false;
            }

            grid.setPadding(new Insets(10, 10, 10, 10));
            grid.setVgap(5);
            grid.setHgap(5);
            GridPane.setConstraints(label1, 0, 0);
            name.setPromptText("请输入新名字");
            name.setPrefColumnCount(20);
            name.getText();
            GridPane.setConstraints(name, 1, 0);
            grid.getChildren().addAll(label1, name);

            if (single) {
                GridPane.setConstraints(msg, 0, 1);
                GridPane.setConstraints(submit, 0, 2);
                grid.getChildren().addAll(submit, msg);
            } else {
                Label label2 = new Label("起始编号");
                GridPane.setConstraints(label2, 0, 1);
                startNum.setPromptText("请输入起始编号");
                startNum.setPrefColumnCount(15);
                startNum.getText();
                GridPane.setConstraints(startNum, 1, 1);
                GridPane.setConstraints(msg, 1, 3);
                GridPane.setConstraints(submit, 0, 4);
                grid.getChildren().addAll(label2, startNum, submit, msg);
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
                            msg.setText("你没有输入，请输入!");
                        }
                    } else {
                        if ((name.getText() != null && !name.getText().isEmpty())
                                && (startNum.getText() != null && !startNum.getText().isEmpty())) {
                            if (renameMore(name.getText(),startNum.getText())) {
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
            anotherStage.setScene(scene);
            anotherStage.show();

        });
        seePictureMenuItem.setOnAction(event -> {
            new SeePicture(ViewerPane.currentTreeNode.getValue().getImages().get(0),ViewerPane.currentTreeNode.getValue().getImages().get(0).getName());
        });
    }

    private void copyFile(String srcPath, String destPath) throws IOException {
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
    //创建名字
    private String createName(String newFileName,int id,int bit) {
        String newName = newFileName;
        int tt = id;
        int cnt=0;
        while(tt!=0) {
            cnt++;
            tt/=10;
        }
        if(id==0)  cnt++;
        while(bit>cnt) {
            newName+=0;
            cnt++;
        }
        newName += id;
        return newName;
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
    private boolean renameMore(String newFileName,String startNum) {
        File file;
        int id = Integer.valueOf(startNum);
        int bit = String.valueOf(PictureNode.getSelectedPictures().size()).length();
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

    public ContextMenu getContextMenu() {
        return contextMenu;
    }
}
