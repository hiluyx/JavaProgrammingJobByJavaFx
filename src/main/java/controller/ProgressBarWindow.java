package controller;


import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Hi lu
 * @since 2020/4/27
 *
 * 删除文件用弹窗
 * 网络加载进度条附在viewerPane的右下角
 *
 * 一个在0和1之间的正数表示了当前进度的百分比，比如0.4表示40%。
 * 一个负数表示当前的进度处在一个不确定的模式。
 * 我们可以使用isIndeterminate方法来判断进度控件是否处于不确定模式。
 */
@Getter
@Setter
public class ProgressBarWindow {

    private ProgressBar progressBar;
    private ProgressIndicator progressIndicator;

    public ProgressBarWindow(){
        this.progressBar = new ProgressBar();
        this.progressIndicator = new ProgressIndicator();
    }

    public void clearBar(){

        progressBar.setProgress(0);
    }
    public void clearIndicator(){
        progressIndicator.setProgress(0);
    }
}
