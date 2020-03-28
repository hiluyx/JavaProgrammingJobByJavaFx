import component.FileTreePane;
import component.PreviewPane;

import javafx.fxml.FXML;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MainPane {
    /*
    JavaFX BorderPane布局
     */
    @FXML
    private FileTreePane fileTreePane;//文件树布局

    @FXML
    private PreviewPane previewPane;


}
