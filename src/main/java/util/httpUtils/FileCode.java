package util.httpUtils;

import controller.ProgressBarWindow;
import javafx.application.Platform;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @since 2020/4/26
 * @author Hi lu
 */
public class FileCode {
    /**
     * 将文件转成base64 字符串
     * @param path 文件路径
     */
    public static String encodeImages(String path) throws IOException{
        File file = new File(path);
        byte[] buffer = new byte[(int) file.length()];
        try{
            FileInputStream fileInputStream = new FileInputStream(file);
            int read = fileInputStream.read(buffer);
            fileInputStream.close();
        }catch (IOException exception){
            exception.printStackTrace();
            throw new IOException();
        }
        return new BASE64Encoder().encode(buffer);
    }
    /**
     * 将base64字符解码保存文件
     */
    public static void decodeBASE64(String BASE64,String targetPath,long targetFileLength) throws IOException {
        byte[] buffer = new BASE64Decoder().decodeBuffer(BASE64);
        /*
        监控下载输出进度
         */
        CountingOutputStream countingOutputStream = new CountingOutputStream(targetPath, transferredBytes -> {
            /*
            告知ProgressBarWindow
             */
            Platform.runLater(()->{
                ProgressBarWindow.progressBar.setProgress((int)(100*transferredBytes/targetFileLength));
            });
        });
        countingOutputStream.write(buffer);
//        FileOutputStream fileOutputStream = new FileOutputStream(targetPath);
//        fileOutputStream.write(buffer);
    }
}