package model;

import javafx.scene.image.Image;
import lombok.Data;
import util.ImageFilter;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
@图片集合模型
 应用于previewPane的数据提供
 */
@Data
public class ImageSet {
    //图片
    private ImageFilter imageFilter;
    private List<Image> images;
    
    public ImageSet(File dir){
        /*
        传入参数Dir（ImageSet必须有一个目录dir找出）
        dir通过过滤器获得目录下的所有图片的url
        据此创建图片数组。
         */
        imageFilter = new ImageFilter();
        String[] imageUrls;
        imageUrls = imageFilter.getImageUrl(dir);
        images = new ArrayList<>();
        for(String s:imageUrls){
            images.add(new Image(s));
        }
    }
}
