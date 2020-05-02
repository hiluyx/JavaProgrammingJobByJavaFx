package util.httpUtils;

import controller.ProgressBarWindow;
import controller.ViewerPane;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @since 2020/4/26
 * @author Hi lu
 */
public class FileCode {
    /**
     * 将base64字符解码保存文件
     */
    public static void decodeBASE64(String BASE64, String targetPath, long targetFileLength)
            throws IOException {
        byte[] buffer = new BASE64Decoder().decodeBuffer(BASE64);
        CountingOutputStream countingOutputStream = new CountingOutputStream(targetPath, transferredBytes -> {
            ProgressBarWindow.updateProgressBar(3,transferredBytes,targetFileLength);
        });
        countingOutputStream.write(buffer);
    }

    protected static class CountingOutputStream extends FileOutputStream {
        private final ProgressListener listener;
        private long transferred;

        public CountingOutputStream(String targetPath,ProgressListener listener) throws FileNotFoundException {
            super(targetPath);
            this.listener = listener;
            this.transferred = 0;
        }
        @Override
        public void write(byte[] buffer) throws IOException {
            super.write(buffer);
            writeCount(buffer.length);
        }

        public void writeCount(long write){
            if (write > 0){
                this.transferred += write;
                this.listener.transferred(this.transferred);
            }
        }
    }
}