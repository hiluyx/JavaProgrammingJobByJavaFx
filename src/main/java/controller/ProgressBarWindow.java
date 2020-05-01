package controller;

import javafx.scene.control.ProgressBar;

/**
 * @author Hi lu
 * @ate 2020/4/27
 * d
 * 加载进度条窗口，包含异常处理
 *
 * 一个在0和1之间的正数表示了当前进度的百分比，比如0.4表示40%。
 * 一个负数表示当前的进度处在一个不确定的模式。
 * 我们可以使用isIndeterminate方法来判断进度控件是否处于不确定模式。
 */
public class ProgressBarWindow {
    public static final ProgressBar progressBar = new ProgressBar();

    public void clearBar(){
        progressBar.setProgress(0);
    }
}
