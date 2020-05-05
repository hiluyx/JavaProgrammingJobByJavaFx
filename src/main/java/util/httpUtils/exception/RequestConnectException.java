package util.httpUtils.exception;

import controller.FileTree;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import lombok.Getter;

import java.util.Optional;

public class RequestConnectException extends Exception {

    public static final DialogSel dialog = new DialogSel();
    private final String msg;
    public RequestConnectException(String msg){
        this.msg = msg;
    }
    public void errorDialog(){
        dialog.setSel(msg);
    }
    public boolean getDialogSel(RequestConnectException exception){
        return dialog.getSel(exception);
    }
    /*
    重连对话框
     */
    private static class DialogSel{
        @Getter
        private boolean yes = false;
        public void setSel(String p_message){
            Platform.runLater(()->{
                synchronized (dialog){
                    Alert _alert = new Alert(Alert.AlertType.CONFIRMATION, p_message,new ButtonType("取消", ButtonBar.ButtonData.NO),
                            new ButtonType("确定", ButtonBar.ButtonData.YES));
                    Optional<ButtonType> _buttonType = _alert.showAndWait();
                    _buttonType.ifPresent(buttonType -> this.yes = (buttonType.getButtonData().equals(ButtonBar.ButtonData.YES)));
                    dialog.notifyAll();
                }
            });
        }
        public boolean getSel(RequestConnectException exception){
            synchronized (dialog){
                try {
                    exception.errorDialog();
                    dialog.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return !dialog.isYes();
            }
        }
    }
}