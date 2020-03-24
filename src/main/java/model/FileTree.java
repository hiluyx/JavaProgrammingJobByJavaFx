package model;

import lombok.Data;

import javax.print.DocFlavor;
import java.io.File;

@Data
public class FileTree {
    //文件目录
    private File[] dirs;

    public FileTree() {

    }
}
