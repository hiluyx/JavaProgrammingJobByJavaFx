package model;

import lombok.Data;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class TreeNode {
    /*
    @param nodeText 节点名称
    @param images 节点包含的图片集
    */
    private File file;
    private String nodeText;
    private List<File> images;

    public TreeNode(File file, String name) {
        this.setFile(file);
        this.setNodeText(name);
        this.setImages();
    }

    @Override
    public String toString() {
        return this.getNodeText();
    }

    public void setImages() {
        File[] imagesFile;
        imagesFile = this.getFile().listFiles(new FileFilter() {
            private Set<String> set = new HashSet<>();

            @Override
            public boolean accept(File pathname) {
                set.addAll(Arrays.asList("jpg", "png", "gif", "bmp",
                        "JPG", "PNG", "GIF", "BMP"));
                return set.contains(getExtension(pathname.getName()));
            }

            public String getExtension(String name) {
                if (name == null) {
                    return null;
                }
                int index = name.lastIndexOf(".");
                return index > -1 ? name.substring(index + 1) : null;
            }
        });
        if (imagesFile != null) {
            this.setImages(Arrays.asList(imagesFile));
        }
    }
}
