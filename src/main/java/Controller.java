import javafx.fxml.FXML;
import javafx.scene.control.Button;

import javafx.event.ActionEvent;

public class Controller {
    
    @FXML
    private Button hello;


    @FXML
    public void handleButtonAction(ActionEvent actionEvent) {
        hello.setText("Hello World, but sky is the best cool Super Saia!");
    }

}
