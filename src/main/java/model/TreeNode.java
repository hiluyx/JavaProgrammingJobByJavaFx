package model;

import lombok.Data;

import java.io.File;
import java.util.List;

@Data
public class TreeNode {
    /*
    @param nodeText 节点名称
    @param images 节点包含的图片集
    */
    private String nodeText;
    private List<File> images;

    @Override
    public String toString() {
        return this.getNodeText();
    }

}
