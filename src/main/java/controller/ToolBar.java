package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.Getter;
import model.PictureNode;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ToolBar extends HBox {

    public int 状态 = -1;//状态为1是粘贴，状态为2是剪切

    private Button copy = createButton("复制");
    private Button cut = createButton("剪切");
    private Button paste = createButton("粘贴");
    private Button delete = createButton("删除");
    private Button reName = createButton("重命名");
    private Button seePicture = createButton("查看");
    @Getter
    private final Label path = new Label();

    public ToolBar(double spacing) {
        super(10);
        this.setPadding(new Insets(10, 10, 10, 10));

        //给粘贴按钮设置粘贴操作
        paste.setOnAction(new paste());
        //把Buttons加到ToolBar
        addButton2Bar();
        //添加功能
        复制功能();
        剪切功能();
        粘贴功能();
        删除功能();
        重命名功能();
        查看功能();
        //初始化按钮的可用性
        copy.setDisable(true);
        cut.setDisable(true);
        paste.setDisable(true);
        delete.setDisable(true);
        reName.setDisable(true);
        seePicture.setDisable(true);
    }

    private void addButton2Bar(){//把button加入ToolBar
        this.getChildren().add(copy);
        this.getChildren().add(cut);
        this.getChildren().add(paste);
        this.getChildren().add(delete);
        this.getChildren().add(reName);
        this.getChildren().add(seePicture);
        this.getChildren().add(path);
    }

    private void 复制功能(){
        this.copy.setOnMouseClicked(event -> {
            if(PictureNode.getSelectedPictures().size()>0){
                状态 = 1;
                paste.setDisable(false);
            }

        });
    }
    private void 剪切功能(){
        this.cut.setOnMouseClicked(event -> {
            if(PictureNode.getSelectedPictures().size()>0){
                状态 = 2;
                paste.setDisable(false);
            }
        });
    }
    private void 粘贴功能(){
        this.paste.setOnMouseClicked(event -> {
            try {
                //srcPath是原路径，destPath是构造出来的目标路径
                // （例如srcPath=G:\0tjx\2.png  destPath =G:\计算机二级\2.png）
                for(PictureNode each:PictureNode.getSelectedPictures()){
                    String srcPath = each.getFile().getAbsolutePath();
                    String destPath = ViewerPane.selectedFolderProperty.getValue().getFile().getAbsolutePath()+"\\"+each.getFile().getName();
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
                paste.setDisable(true);
                //    String destPath = ViewerPane.selectedFolderProperty.getValue().getFile().getAbsolutePath()+"\\"+PictureNode.getSelectedPictures().get(0).getFile().getName();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    private void 删除功能(){
        this.delete.setOnMouseClicked(event -> {
            int num = 0;
            for(PictureNode each:PictureNode.getSelectedPictures()){
                if(each.getFile().delete()){
                    System.out.printf("第%d张图片删除成功",++num);
                    ViewerPane.flowPane.getChildren().remove(each);
                }
            }
        });
    }

    private void 查看功能(){
        this.seePicture.setOnMouseClicked(event -> {
            new SeePicture(ViewerPane.selectedFolderProperty.getValue().getImages().get(0),ViewerPane.selectedFolderProperty.getValue().getImages().get(0).getName());
        });
    }

    private void 重命名功能() {
        this.reName.setOnMouseClicked(event -> {
            boolean single;
            GridPane grid = new GridPane();
            Button submit = new Button("完成");
            Label msg = new Label();
            Label label1 = new Label("名称");
            Stage anotherStage = new Stage();
            TextField name = new TextField();
            TextField startNum = new TextField();

            if (ViewerPane.selectedFolderProperty.getValue().getImages().size() == 1) {
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

    //创建一个Button，button的文字为函数参数
    private Button createButton(String buttonName) {
        Button button = new Button();
        button.setPadding(new Insets(10, 10, 10, 10));
        button.setText(buttonName);
        return button;
    }

    private static class paste implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            Clipboard clipboard = Clipboard.getSystemClipboard();

            if (clipboard.hasFiles()) {
                for (File each : clipboard.getFiles()) {
                    String path = ViewerPane.selectedFolderProperty.getValue().getFile().getAbsolutePath();
                    String oldName = each.getName();
                    String newName = path+"/"+oldName;
                    int num =1;
                    String prefixName = newName.substring(0,
                                                          newName.lastIndexOf("."))+"(_1)";
                    List<File> files = ViewerPane.selectedFolderProperty.getValue().getImages();
                    for(File f:files){

                        String fName = f.getAbsolutePath();
                        String fPrefix = fName.substring(0,fName.lastIndexOf(
                                "."));
                        System.out.println("当前文件："+fPrefix);
                        if(prefixName.equals(fPrefix)){
                            System.out.println("equally");
                            prefixName+="(_1)";
                        }else{
                            System.out.println("prefixName:"+prefixName);
                            System.out.println("fprefix:"+fPrefix);
                        }

                    }
                    String[] strArray =
                            each.getName().split("\\.");
                    int suffixIndex = strArray.length - 1;
                    String suffixName = strArray[suffixIndex];//缺少"."
                    String newFile = prefixName + "." + suffixName;




                    System.out.println(newFile);
                    try {
                        BufferedImage bufferedImage = ImageIO.read(each.getAbsoluteFile());
                        OutputStream ops =
                                new FileOutputStream(new File(newFile));
                        ImageIO.write(bufferedImage, suffixName, ops);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    PictureNode pictureNode = new PictureNode(new File(newFile));
                    ViewerPane.flowPane.getChildren().add(pictureNode);
                }

            }

        }
        /*
        public static String getCopyNewName(String name) {
// 自定义名称 后缀
            String COPY_NAME ="-副本" ;
            String newName;
//判断名称长度是否大于3
            if (name.length() >= COPY_NAME.length() + 1){
                //判断名称后几位是否为“-副本”
                newName = name.substring(name.length() - 4, name.length() - 1);
                if (COPY_NAME.equals(newName)){
                    //尾数加1
                    Integer num= Integer.parseInt(name.substring(name.length() - 1, name.length() )) + 1;
                    newName = name.substring(0,name.length()-COPY_NAME.length()-1) + COPY_NAME + num;
                }else {
                    // 直接拼接
                    newName = name + COPY_NAME + "1";
                }
            }else {
                // 直接拼接
                newName = name + COPY_NAME +"1";
            }
// 判断新生成的文件名 是否已存在
            int count = //检测方法;
            if (count >= 1){
                //如果已存在，递归调用
                newName = getCopyNewName(newName);
            }
            return newName;
        }
*/
    }
//    public void setSelectedFolder(TreeNode selectedFolder) {
//        this.selectedFolderProperty.set(selectedFolder);
//    }

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

    public Button getCopy() {
    return copy;
}

    public void setCopy(Button copy) {
        this.copy = copy;
    }

    public Button getCut() {
        return cut;
    }

    public void setCut(Button cut) {
        this.cut = cut;
    }

    public Button getPaste() {
        return paste;
    }

    public void setPaste(Button paste) {
        this.paste = paste;
    }

    public Button getDelete() {
        return delete;
    }

    public void setDelete(Button delete) {
        this.delete = delete;
    }

    public Button getReName() {
        return reName;
    }

    public Button getSeePicture() {
        return seePicture;
    }
}
