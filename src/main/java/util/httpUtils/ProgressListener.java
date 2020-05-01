package util.httpUtils;

public interface ProgressListener {
    void transferred(long transferredBytes);
}
