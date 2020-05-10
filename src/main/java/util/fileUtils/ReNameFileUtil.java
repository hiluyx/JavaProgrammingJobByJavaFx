package util.fileUtils;

import mainpane.ViewerPane;
import model.PictureNode;

import java.io.File;
import java.util.ArrayList;

public class ReNameFileUtil {
    //创建名字(重命名功能用到的函数)
    public static String createName(String newFileName,int id,int bit) {
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
    public static boolean renameSingle(String newFileName) {
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
    public static boolean renameMore(String newFileName,String startNum,String bitNum) {
        File file;
        int id = Integer.parseInt(startNum);
        int bit = Integer.parseInt(bitNum);
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
}
