package util;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class ButtonUtil {
    //创建一个Button，button的文字为函数参数
    public static Button createButton(String buttonName) {
        Button button = new Button();
        button.setId(buttonName);
        button.setPadding(new Insets(10, 10, 10, 10));
        //button.setText(buttonName);
        button.setStyle("-fx-background-color:#ffffff;");
        button.setGraphic(new ImageView(new Image("file:"+new File("icon/"+buttonName+".png"),30, 30,
                true, true)));
        button.setTooltip(new Tooltip(buttonName));
        return button;
    }
}
