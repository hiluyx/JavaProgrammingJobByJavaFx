package controller;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import model.PictureNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author helefeng
 * @date 2020/5/1 5:08 下午
 */
public class NoSelectedMenuPane {
    private MenuPane menuPane = new MenuPane();

    public MenuItem getPaste() {
        return paste;
    }

    public void setPaste(MenuItem paste) {
        this.paste = paste;
    }

    public MenuItem getRevocation() {
        return revocation;
    }

    public void setRevocation(MenuItem revocation) {
        this.revocation = revocation;
    }
    public static boolean isDeleted = true;
    private MenuItem allSelected = new MenuItem("全选");
    private MenuItem paste = new MenuItem("粘贴");
    private MenuItem revocation = new MenuItem("撤销");
    private ContextMenu contextMenu = new ContextMenu();

    public static ArrayList<PictureNode> getRevocationPictures() {
        return revocationPictures;
    }

    public static void setRevocationPictures(
            ArrayList<PictureNode> revocationPictures) {
        NoSelectedMenuPane.revocationPictures = revocationPictures;
    }

    public static ArrayList<PictureNode> revocationPictures = new ArrayList<>();
    public static ArrayList<File>revocationPictureFiles = new ArrayList<>();
    public static ArrayList<Image> revocationPictureImages = new ArrayList<>();

    public NoSelectedMenuPane(Node node) {
        contextMenu.getItems().addAll(paste, allSelected, revocation);
        setStatus(); show(node); setMenuItemFunction();
    }

    private void show(Node node) {
        node.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
            if (e.getPickResult().getIntersectedNode() instanceof FlowPane) {
                if (e.getButton() == MouseButton.SECONDARY) {// 鼠标右键
                    contextMenu.show(node, e.getScreenX(), e.getScreenY());
                } else {
                    if (contextMenu.isShowing()) {
                        contextMenu.hide();
                    }
                }
            } else {
                if (contextMenu.isShowing()) {
                    contextMenu.hide();
                }
            }
//            if (e.getPickResult().getIntersectedNode() instanceof FlowPane) {
//                if (e.getButton() == MouseButton.SECONDARY) {
//                    contextMenu.show(node, e.getScreenX(), e.getScreenY());
//                } else {
//                    if (contextMenu.isShowing()) {
//                        contextMenu.hide();
//                    }
//                }
//            } else {
//                if (contextMenu.isShowing()) {
//                    contextMenu.hide();
//                }
//            }

//            else{
//                contextMenu.
//            }
        });
    }

    private void setStatus() {
        if (MenuPane.status == -1) {
            paste.setDisable(true); revocation.setDisable(true);
        } else {
            paste.setDisable(false); revocation.setDisable(false);
        }
    }

    private void setMenuItemFunction() {
        this.allSelected.setOnAction(event -> {
            allSelectedFunction();
        }); this.revocation.setOnAction(event -> {
            revocationFunction();
        }); this.paste.setOnAction(event -> {
            pasteFunction();
        });
    }

    public void allSelectedFunction() {
//        allSelected.setOnAction(event -> {
        System.out.println("allselected");
        for (Node each : ViewerPane.flowPane.getChildren()) {
            if (PictureNode.getSelectedPictures()
                           .contains((PictureNode) each) == false) {
                each.setStyle("-fx-background-color: #8bb9ff;");
                PictureNode.getSelectedPictures().add((PictureNode) each);
            }
        }
//        });
    }

    public void revocationFunction() {
//        revocation.setOnAction(event -> {
        System.out.println("revocation"); System.out.println(
                "需要撤回的大小：" + NoSelectedMenuPane.revocationPictures.size());

        if (revocationPictureImages != null&&revocationPictureFiles!=null) {
            for(int i=0;i<revocationPictureFiles.size();i++){
                PictureNode pictureNode =
                        new PictureNode(revocationPictureFiles.get(i),
                                        revocationPictureImages.get(i));
                ViewerPane.flowPane.getChildren().add(pictureNode);
                System.out.println(revocationPictureFiles.get(i).exists());
            }
//            for (PictureNode each : revocationPictures) {
//                ViewerPane.flowPane.getChildren().add(each);
//
//                System.out.println("文件是否存在："+each.getFile().exists());
//                if(!each.getFile().exists()){
//                    each.getFile().mkdir();
//                }
//                System.out.println(each.getFile().exists());
//                System.out.println("文件名："+each.getFile().getName());
//            }

//        for(PictureNode each:NoSelectedMenuPane.revocationPictures){
//
//            ViewerPane.flowPane.getChildren().add(each);
//        }
//        });
        }
    }

    public void pasteFunction() {
//        this.paste.setOnAction(event -> {
        System.out.println("paste");
        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard != null) {
            paste.setDisable(true);
        } else {
            paste.setDisable(false);
        } ArrayList<File> picFiles = (ArrayList<File>) clipboard
                .getContent(DataFormat.FILES);
        ArrayList<PictureNode> processedPictures = new ArrayList<>();
        for (File each : picFiles) {
            processedPictures.add(new PictureNode(each));
        } System.out.println(processedPictures.size()); clipboard.clear(); try {
            //srcPath是原路径，destPath是构造出来的目标路径
            //例如srcPath=G:\0tjx\2.png  destPath =G:\计算机二级\2.png
            for (PictureNode each : processedPictures) {
                String srcPath = each.getFile().getAbsolutePath();
                String path = ViewerPane.currentTreeNode.getValue().getFile()
                                                        .getAbsolutePath();
                String picName = each.getFile().getName();
                String destPath = path + "/" + picName;
                StringBuilder destPrefix = new StringBuilder(
                        destPath.substring(0, destPath.lastIndexOf(".")));

                String destTyle = picName
                        .substring(picName.lastIndexOf("."), picName.length());
                System.out.println(destPrefix + destTyle);
                destPath = destPrefix + destTyle;
                while (new File(destPath).exists()) {
                    destPrefix.append("(_1)"); destPath = destPrefix + destTyle;
                }
                //观察路径
                System.out.println(srcPath); System.out.println(destPath);
                //直接从文件层面复制
                menuPane.copyFile(srcPath, destPath);
                //添加到flowPane
                File file = new File(destPath);
                PictureNode p = new PictureNode(file);
                ViewerPane.flowPane.getChildren().add(p);
            } if (MenuPane.status == 2) {//如果为剪切状态，删除原路径下的图片
                int num = 0; for (PictureNode each : processedPictures) {
                    if (each.getFile().delete()) {
                        System.out.printf("第%d张图片删除成功\n", ++num);
                        ViewerPane.flowPane.getChildren().remove(each);
                    }
                }
            }
            //粘贴一次之后设置为不可用
            paste.setDisable(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        });
    }
}
