package data;

import lombok.Data;

import java.io.File;

@Data
public class dir {
    //文件目录
    private File[] dirs;
    private Image[] images;
}
