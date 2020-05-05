package controller;


import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import lombok.Getter;
import lombok.Setter;
import util.TaskThreadPools;

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

    public void setErrorBar(){
        progressBar.setProgress(-1);
    }

    public void clearIndicator(){
        progressIndicator.setProgress(0);
    }

    public static void updateProgressBar(int step){
        updateProgressBar(step,0,0,0);
    }

    public static void updateProgressBar(int step,int pageSize){
        updateProgressBar(step,0,0,pageSize);
    }

    public static void updateProgressBar(int step,long transferredBytes,long targetFileLength){
        updateProgressBar(step,transferredBytes,targetFileLength,0);
    }

    public static void updateProgressBar(int step,long transferredBytes,long targetFileLength,int pageSize){
        TaskThreadPools.execute(()->{
            synchronized (ViewerPane.progressBarWindow.getProgressBar()){
                if(step == 1){
                    try {
                        for (int i = 0; i < 100; i++) {
                            Thread.sleep(10 + pageSize / 2);
                            double pro = (int) ((i / 100.0) * 650 + 150);
                            Platform.runLater(()-> ViewerPane.progressBarWindow.getProgressBar().setProgress(pro / 1000));
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Platform.runLater(() -> {
                    if (step == 0) {
                        ViewerPane.progressBarWindow.getProgressBar().setProgress(0.15);
                    }else if(step == 2) {
                        double inputProgress = (int) (100 * transferredBytes / targetFileLength);
                        ViewerPane.progressBarWindow.getProgressBar().setProgress(inputProgress * 0.2 + 0.8);
                    }
                });
            }
        });
    }
}
