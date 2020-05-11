package toolpane;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.*;
import javafx.scene.layout.FlowPane;
import lombok.Getter;
import lombok.Setter;
import mainpane.ViewerPane;
import model.PictureNode;
import util.TaskThreadPools;
import util.fileUtils.CopyFileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author helefeng
 * @date 2020/5/1 5:08 下午
 */
@Getter
@Setter
public class NoSelectedMenuPane {
    private MenuPane menuPane = new MenuPane();

    private static int index =0;

    private MenuItem allSelected = new MenuItem("全选");
    private MenuItem paste = new MenuItem("粘贴");
    private MenuItem revocation = new MenuItem("撤销");
    private ContextMenu contextMenu = new ContextMenu();

    public static ArrayList<File>revocationPictureFiles = new ArrayList<>();
    public static ArrayList<Integer>everyRevocationNum = new ArrayList<>();

    public NoSelectedMenuPane(Node node) {
        contextMenu.getItems().addAll(paste, allSelected, revocation);
        setStatus();
        show(node);
        setMenuItemFunction();
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
            TaskThreadPools.execute(this::revocationFunction);
        }); this.paste.setOnAction(event -> {
            pasteFunction();
        });
    }

    //全选图片
    public void allSelectedFunction() {
        this.contextMenu.hide();
        for (Node each : ViewerPane.flowPane.getChildren()) {
            if (!PictureNode.getSelectedPictures()
                    .contains(each)&&((PictureNode)each).getLocked()==false) {
                each.setStyle("-fx-background-color: #8bb9ff;");
                PictureNode.getSelectedPictures().add((PictureNode) each);
            }
        }
        //设置上传按钮可用性，更新选中的张数
        FunctionBar.upLoad.setDisable(false);
        ViewerPane.selectedNumberOfPicture.setText(new String("-选中"+PictureNode.getSelectedPictures().size()+"张"));
    }

    public void revocationFunction() {
        this.contextMenu.hide();
        System.out.println(everyRevocationNum.size());
        File[] files = MenuPane.recycleBin.listFiles();

        index = everyRevocationNum.size()-1;

        int curIndex = files.length-1;
        System.out.println(everyRevocationNum.get(index));
        for(int i=0;i<everyRevocationNum.get(index);i++,curIndex--){
            System.out.println(files[curIndex].exists());
            System.out.println(files[curIndex].getAbsolutePath());

            String destPath = revocationPictureFiles.get(curIndex).getAbsolutePath();
            files[i].renameTo(new File(destPath));
            revocationPictureFiles.remove(revocationPictureFiles.get(curIndex));
            File file = new File(destPath).getParentFile();
            if(file.getAbsolutePath().equals(FunctionBar.path.getText())){
                Platform.runLater(()->ViewerPane.flowPane.getChildren().add(new PictureNode(new File(destPath))));
            }
            System.out.println("目标文件是否存在："+new File(destPath).exists());
        }
        everyRevocationNum.remove(index);
    }

    public void pasteFunction() {
        this.contextMenu.hide();
        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard != null) {
            paste.setDisable(true);
        } else {
            paste.setDisable(false);
        }
        ArrayList<File> picFiles = (ArrayList<File>) clipboard.getContent(DataFormat.FILES);
        ArrayList<PictureNode> processedPictures = new ArrayList<>();
        for (File each : picFiles) {
            processedPictures.add(new PictureNode(each));
        }
        System.out.println(processedPictures.size());
        clipboard.clear();
        try {
            if (MenuPane.status == 2) {//如果为剪切状态，删除原路径下的图片
                for (PictureNode each : processedPictures) {
                    if (each.getFile().delete()) {
                        Platform.runLater(()->ViewerPane.flowPane.getChildren().remove(each));
                    }
                }
            }
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
                        .substring(picName.lastIndexOf("."));
                System.out.println(destPrefix + destTyle);
                destPath = destPrefix + destTyle;
                while (new File(destPath).exists()) {
                    destPrefix.append("(_1)"); destPath = destPrefix + destTyle;
                }
                //观察路径
                System.out.println(srcPath); System.out.println(destPath);
                //直接从文件层面复制
                CopyFileUtil.copyFile(srcPath, destPath);
                //添加到flowPane
                File file = new File(destPath);
                PictureNode p = new PictureNode(file);
                Platform.runLater(()-> ViewerPane.flowPane.getChildren().add(p));
            }
            //粘贴一次之后设置为不可用
            paste.setDisable(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
