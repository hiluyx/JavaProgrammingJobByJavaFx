import controller.FileTree;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Setter
@Getter
public class MainController implements Initializable {

    @FXML
    private BorderPane borderPane;
    @FXML
    private FileTree fileTreePane;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private FlowPane flowPane;

    public MainController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/file-tree-pane.fxml"));
        fxmlLoader.setRoot(new FXMLLoader(getClass().getResource("fxml/main-pane.fxml")).getNamespace().get("fileTreePane"));
        fxmlLoader.load();
        this.fileTreePane = fxmlLoader.getRoot();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void load(ActionEvent actionEvent) {
    }
}
