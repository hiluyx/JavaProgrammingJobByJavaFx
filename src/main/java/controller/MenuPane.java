package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import lombok.Getter;
import lombok.Setter;
import model.PictureNode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * @author helefeng
 * @date 2020/4/20 10:45 上午
 */
public class MenuPane extends MenuItem {
    private final MenuItem openMenuItem = new MenuItem("打开");
    @Getter
    @Setter
    private MenuItem copyMenuItem = new MenuItem("复制");
    @Getter
    @Setter
    private MenuItem pasteMenuItem = new MenuItem("粘贴");
    @Getter
    @Setter
    private ContextMenu contextMenu = new ContextMenu();


    public MenuPane() {
        contextMenu.getItems().addAll(copyMenuItem, pasteMenuItem);
        copyMenuItem.setOnAction(new copy());
        pasteMenuItem.setOnAction(new paste());
    }

    private static class copy implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboard.clear();
            clipboardContent.putFiles(PictureNode.getSelectedPictureFiles());
            clipboard.setContent(clipboardContent);
        }
    }

    /**
     * 路径需要变换
     */
    private static class paste implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            Clipboard clipboard = Clipboard.getSystemClipboard();

            if (clipboard.hasFiles()) {
                for (File each : clipboard.getFiles()) {
                    String path = ViewerPane.selectedFolderProperty.getValue().getFile().getAbsolutePath();

                    String prefixName = path.substring(0,
                            path.lastIndexOf(".")) + "(副本)";
                    String[] strArray = each.getName().split("\\.");
                    int suffixIndex = strArray.length - 1;
                    String suffixName = strArray[suffixIndex];//缺少"."
                    String newFile = prefixName + "." + suffixName;
                    try {
                        BufferedImage bufferedImage = ImageIO.read(each.getAbsoluteFile());
                        OutputStream ops =
                                new FileOutputStream(new File(newFile));
                        ImageIO.write(bufferedImage, suffixName, ops);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(prefixName + suffixName);
                    PictureNode pictureNode = new PictureNode(new File(newFile));
                    ViewerPane.flowPane.getChildren().add(pictureNode);
                }

            }

        }
    }

}
