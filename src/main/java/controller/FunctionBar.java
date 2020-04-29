package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lombok.Getter;
import model.PictureNode;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
public class FunctionBar extends HBox {

    public int status = -1;//状态为1是粘贴，状态为2是剪切
    //按钮
    private Button copy = createButton("copy");
    private Button cut = createButton("cut");
    private Button paste = createButton("paste");
    private Button delete = createButton("delete");
    private Button reName = createButton("reName");
    private Button seePicture = createButton("查看");
    @Getter
    private final Label path = new Label();

    public FunctionBar(double spacing) {
        super(10);
        //设置padding
        this.setPadding(new Insets(10, 10, 10, 10));
        this.setStyle("-fx-background-color: White;");
        //把buttons加到ToolBar
        addButton2Bar();
        //给按钮添加功能
        addFunction2Button();
        //初始化按钮为不可用
        //setButtonDisable();
    }

    //把button加入ToolBar
    private void addButton2Bar(){
        //  this.getChildren().add(copy);
        //  this.getChildren().add(cut);
        //  this.getChildren().add(paste);
        //  this.getChildren().add(delete);
        //  this.getChildren().add(reName);
        this.getChildren().add(seePicture);
        this.getChildren().add(path);
    }

    //给button加上功能
    private void addFunction2Button(){
        copyFunction();
        cutFunction();
        pasteFunction();
        deleteFunction();
        seePictureFunction();
        reNameFunction();
    }

    //初始化button不可用
    private void setButtonDisable(){
        this.copy.setDisable(true);
        this.cut.setDisable(true);
        this.paste.setDisable(true);
        this.delete.setDisable(true);
        this.reName.setDisable(true);
        this.seePicture.setDisable(true);
    }

    //六大功能
    private void copyFunction(){
        this.copy.setOnMouseClicked(event -> {
            if(PictureNode.getSelectedPictures().size()>0){
                this.status = 1;
                paste.setDisable(false);
            }
        });
    }
    private void cutFunction(){
        this.cut.setOnMouseClicked(event -> {
            if(PictureNode.getSelectedPictures().size()>0){
                this.status = 2;
                paste.setDisable(false);
            }
        });
    }
    private void pasteFunction(){
        this.paste.setOnMouseClicked(event -> {
            try {
                //srcPath是原路径，destPath是构造出来的目标路径
                //例如srcPath=G:\0tjx\2.png  destPath =G:\计算机二级\2.png
                for(PictureNode each:PictureNode.getSelectedPictures()){
                    String srcPath = each.getFile().getAbsolutePath();
                    String path = ViewerPane.currentTreeNode.getValue().getFile().getAbsolutePath();
                    String picName = each.getFile().getName();
                    String destPath = path+"/"+picName;
                    String destPrefix = destPath.substring(0,
                            destPath.lastIndexOf("."));
                    List<File> files = ViewerPane.currentTreeNode.getValue().getImages();
                    String destTyle = picName.substring(picName.lastIndexOf(
                            "."),picName.length());
                    System.out.println(destPrefix+destTyle);
                    destPath = destPrefix+destTyle;
                    while(new File(destPath).exists()){
                        destPrefix+="(_1)";
                        destPath = destPrefix+destTyle;
                    }
                    //观察路径
                    System.out.println(srcPath);
                    System.out.println(destPath);
                    //直接从文件层面复制
                    copyFile(srcPath,destPath);
                    //添加到flowPane
                    File file = new File(destPath);
                    PictureNode p = new PictureNode(file);
                    ViewerPane.flowPane.getChildren().add(p);
                }
                if(status==2){//如果为剪切状态，删除原路径下的图片
                    int num = 0;
                    for(PictureNode each:PictureNode.getSelectedPictures()){
                        if(each.getFile().delete()){
                            System.out.printf("第%d张图片删除成功\n",++num);
                            ViewerPane.flowPane.getChildren().remove(each);
                        }
                    }
                }
                //粘贴一次之后设置为不可用
                paste.setDisable(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    private void deleteFunction(){
        this.delete.setOnMouseClicked(event -> {

            //以下为一些页面布局，具体功能就是实现删除时的确认
            AtomicBoolean isDelete = new AtomicBoolean(false);

            BorderPane root = new BorderPane();
            root.setStyle("-fx-background-color: White;");
            Label label = new Label("是否删除");
            label.setFont(new Font(35));
            HBox hBox = new HBox(25);
            Button yes = new Button("是");
            Button no = new Button("否");
            yes.setPrefWidth(50);
            no.setPrefWidth(50);
            hBox.getChildren().add(yes);
            hBox.getChildren().add(no);
            hBox.setAlignment(Pos.BOTTOM_CENTER);
            hBox.setPadding(new Insets(5,5,15,5));
            root.setCenter(label);
            root.setBottom(hBox);
            Scene scene = new Scene(root,450,150);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
            yes.setOnMouseClicked(event1 -> {
                isDelete.set(true);
                stage.close();
                int num = 0;
                for(PictureNode each:PictureNode.getSelectedPictures()){
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
        this.seePicture.setOnMouseClicked(event -> {
            new SeePicture(ViewerPane.currentTreeNode.getValue().getImages().get(0),ViewerPane.currentTreeNode.getValue().getImages().get(0).getName());
        });
    }
    private void reNameFunction() {
        this.reName.setOnMouseClicked(event -> {
            boolean single;
            GridPane grid = new GridPane();
            Button submit = new Button("完成");
            Label msg = new Label();
            Label label1 = new Label("名称");
            Stage anotherStage = new Stage();
            TextField name = new TextField();
            TextField startNum = new TextField();
            TextField bitNum = new TextField();

            //单选多选
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
                Label label3 = new Label("编号位数");
                GridPane.setConstraints(label3, 0, 2);
                bitNum.setPromptText("请输入编号位数");
                bitNum.setPrefColumnCount(10);
                bitNum.getText();
                GridPane.setConstraints(bitNum, 1, 2);
                GridPane.setConstraints(msg, 1, 3);
                GridPane.setConstraints(submit, 0, 4);
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
            anotherStage.setScene(scene);
            anotherStage.show();

        });
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
    private boolean renameMore(String newFileName,String startNum,String bitNum) {
        File file;
        int id = Integer.valueOf(startNum);
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
    //创建一个Button，button的文字为函数参数
    private Button createButton(String buttonName) {
        Button button = new Button();
        button.setId(buttonName);
        button.setPadding(new Insets(10, 10, 10, 10));
        button.setText(buttonName);
        return button;
    }

    //复制文件的函数
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

}
