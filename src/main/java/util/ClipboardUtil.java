package util;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import model.PictureNode;

import java.util.ArrayList;

/**
 * @author helefeng
 * @date 2020/5/3 10:34 上午
 */
public class ClipboardUtil {
    public static Clipboard clipboard = Clipboard.getSystemClipboard();
    public static ClipboardContent clipboardContent = new ClipboardContent();

    public ClipboardUtil() {
        clipboard.clear(); clipboardContent.clear();
        PictureNode.getSelectedPictureFiles().clear();
        for (PictureNode each : PictureNode.getSelectedPictures()) {
            if (PictureNode.getSelectedPictureFiles()
                           .contains(each.getFile()) == false) {
                PictureNode.getSelectedPictureFiles().add(each.getFile());
            }
        } clipboardContent.putFiles(PictureNode.getSelectedPictureFiles());
        clipboard.setContent(clipboardContent);
    }
}
