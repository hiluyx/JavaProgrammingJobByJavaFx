package model;

import lombok.Data;

import java.io.File;

/**
 * 绑定云相册的id与文件路径
 * 文本保存关系
 * @since 2020/4/26
 * @author Hi lu
 */
@Data
public class CloudImageNote {
    private int id;
    private String fileName;
    public CloudImageNote(int id,String fileName){
        this.fileName = fileName;
        this.id = id;
    }
    public int matchingIdByName(String fileName){
        if(this.fileName.equals(fileName)) return this.id;
        else return 0;
    }
}
