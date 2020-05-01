package util.httpUtils;

/**
 * @author hi lu
 * @since 2020/5/1
 */
public interface ProgressListener {
    void transferred(long transferredBytes);
}
