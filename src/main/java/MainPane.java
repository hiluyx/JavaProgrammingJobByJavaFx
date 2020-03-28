import controller.FileTreePane;
import controller.PreviewPane;

import controller.URLControllerPane;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

@Setter
@Getter
public class MainPane implements Initializable {
    /*
    JavaFX BorderPane布局实现主界面
     */
    @FXML
    private BorderPane borderPane;
    @FXML
    private FileTreePane fileTreePane;//主界面的左部分：文件树布局
    @FXML
    private PreviewPane previewPane;//主界面的右部分：预览窗口
    @FXML
    private URLControllerPane urlControllerPane;//主界面的上部分：路径操作
    @FXML
    private TextField textField;//下部分用textField统计信息（当前我文件夹的图片个数，图片集大小等等）

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
