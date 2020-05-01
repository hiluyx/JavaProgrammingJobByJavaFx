package util.httpUtils;

import lombok.Data;

import java.io.File;

/**
 * 绑定云相册的id与文件路径
 * 文本保存关系
 * @Date 2020/4/26
 * @Author Hi lu
 */
@Data
public class CloudImageNote {
    private int id;
    private File image;
    public CloudImageNote(int id){
        this.id = id;
    }
}
