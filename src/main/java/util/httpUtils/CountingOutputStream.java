package util.httpUtils;

import lombok.Getter;
import lombok.Setter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@Getter
@Setter
public class CountingOutputStream extends FileOutputStream {
    private final ProgressListener listener;
    private long transferred;

    protected CountingOutputStream(String targetPath,ProgressListener listener) throws FileNotFoundException {
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
