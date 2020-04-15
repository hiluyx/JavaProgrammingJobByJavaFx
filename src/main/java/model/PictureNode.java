package model;

import java.io.File;

import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import lombok.Data;

@Data
public class PictureNode extends Label {
	private Image image;
	private ImageView imageView;
	private Text pictureName;

	public PictureNode(File aPicture) {
		//init(aPicture);
		this.setGraphicTextGap(10);
		this.setPadding(new Insets(10, 10, 10, 10));
		this.setContentDisplay(ContentDisplay.TOP);
		this.setPrefSize(110, 110);

		this.image = new Image("file:" + aPicture.getAbsolutePath(), 100, 100, true, true);
		this.imageView = new ImageView(image);
		this.pictureName = new Text(aPicture.getName());
		this.setText(pictureName.getText());
		this.setGraphic(imageView);
		this.setId("pictureNode");
	}
}
