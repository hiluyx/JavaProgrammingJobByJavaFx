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
     * 将文件转成base64 字符串
     * @param targetFilePaths 文件路径
     * @return base64EncodeImages
     */

    public static List<String> encodeImages(String[] targetFilePaths) throws IOException{
        List<String> base64EncodedImages = new ArrayList<>();
        List<File> targetFiles = new ArrayList<>();
        long targetFileLength = 0;
        for(String path : targetFilePaths){
            File targetFile = new File(path);
            targetFiles.add(targetFile);
            targetFileLength += targetFile.length();
        }
        for(File targetFile : targetFiles){
            byte[] buffer = new byte[(int) targetFile.length()];
            long finalTargetFileLength = targetFileLength;
            CountingInputStream countingInputStream = new CountingInputStream(targetFile, transferredBytes -> {
                ProgressBarWindow.updateProgressBar(3,transferredBytes, finalTargetFileLength);
            });
            int read = countingInputStream.read(buffer);
            base64EncodedImages.add(new BASE64Encoder().encode(buffer));
        }
        return base64EncodedImages;
    }
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

    protected static class CountingInputStream extends FileInputStream{
        private final ProgressListener listener;
        private long transferred;

        protected CountingInputStream(File targetFile,ProgressListener listener) throws FileNotFoundException {
            super(targetFile);
            this.listener = listener;
            this.transferred = 0;
        }
        @Override
        public int read(byte[] buffer) throws IOException {
            int read = super.read(buffer);
            readCount(read);
            return read;
        }
        public void readCount(long read){
            if(read > 0){
                this.transferred += read;
                this.listener.transferred(this.transferred);
            }
        }
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