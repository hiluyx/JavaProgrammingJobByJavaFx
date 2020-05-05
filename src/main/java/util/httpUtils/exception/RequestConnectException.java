package util.httpUtils.exception;

import controller.FileTree;

public class RequestConnectException extends Exception {

    private String msg;
    public RequestConnectException(String msg){
        this.msg = msg;
    }
    public void errorDialog(){
        FileTree.dialog.setYes(msg);
    }
}
