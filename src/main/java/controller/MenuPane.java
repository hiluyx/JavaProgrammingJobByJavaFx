package controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import model.PictureNode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * @author helefeng
 * @date 2020/4/20 10:45 上午
 */
public class MenuPane extends MenuItem {
    private MenuItem openMenuItem = new MenuItem("打开");
    private MenuItem copyMenuItem = new MenuItem("复制");
    private MenuItem pasteMenuItem = new MenuItem("粘贴");
    private ContextMenu contextMenu = new ContextMenu();


    public MenuPane() {
        contextMenu.getItems().addAll(copyMenuItem, pasteMenuItem);
        copyMenuItem.setOnAction(new copy());
        pasteMenuItem.setOnAction(new paste());
    }

    public MenuItem getOpenMenuItem() {
        return openMenuItem;
    }

    public void setOpenMenuItem(MenuItem openMenuItem) {
        this.openMenuItem = openMenuItem;
    }

    public ContextMenu getContextMenu() {
        return contextMenu;
    }

    public void setContextMenu(ContextMenu contextMenu) {
        this.contextMenu = contextMenu;
    }

    public MenuItem getCopyMenuItem() {
        return copyMenuItem;
    }

    public void setCopyMenuItem(MenuItem copyMenuItem) {
        this.copyMenuItem = copyMenuItem;
    }

    public MenuItem getPasteMenuItem() {
        return pasteMenuItem;
    }

    public void setPasteMenuItem(MenuItem pasteMenuItem) {
        this.pasteMenuItem = pasteMenuItem;
    }

    private class copy implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboard.clear();
            clipboardContent.putFiles(PictureNode.getSelectedPictureFiles());
            clipboard.setContent(clipboardContent);
//            if(clipboard.hasFiles()){
//              for(File each:clipboard.getFiles()){
//                  System.out.println(each.getName());
//              }
//            }


        }
    }

    /**
     * 路径需要变换
     */
    private class paste implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            Clipboard clipboard = Clipboard.getSystemClipboard();

            if (clipboard.hasFiles()) {
                for (File each : clipboard.getFiles()) {
                    String prefixName = each.getAbsolutePath().substring(0,
                            each.getAbsolutePath().lastIndexOf(".")) + "(副本)";
                    String[] strArray = each.getName().split("\\.");
                    int suffixIndex = strArray.length - 1;
                    String suffixName = strArray[suffixIndex];//缺少"."
                    String newFile = prefixName + "." + suffixName;
                    try {
                        BufferedImage bufferedImage = ImageIO.read(each.getAbsoluteFile());
                        OutputStream ops =
                                new FileOutputStream(new File(newFile));
                        ImageIO.write(bufferedImage, suffixName, ops);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
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
