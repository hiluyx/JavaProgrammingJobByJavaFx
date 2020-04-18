package model;

import java.io.File;
import controller.SeePicture;
import controller.ViewerPane;
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class PictureNode extends Label {
	private File file;
	private Image image;
	private ImageView imageView;
	private Text pictureName;
	private ViewerPane viewerPane;

	public PictureNode(File aPicture, ViewerPane viewerPane) {
		this.viewerPane = viewerPane;
		this.file = aPicture;
		this.setPickOnBounds(true);
		this.setOnMouseExited(e -> {
			this.setStyle("-fx-background-color: White;");
		});

		this.setOnMouseClicked(e -> {
			if (e.getSceneX() != 0 && e.getSceneY() != 0) {
				int count = e.getClickCount();
				if (count % 2 == 1) {
					this.setStyle("-fx-background-color: #8bb9ff;");
				}

			}

			if (e.getClickCount() == 2) {
				new SeePicture(this.file, this.file.getName());
				/*Stage s = new Stage();
				BorderPane borderPane = new BorderPane();
				Image im = new Image("file:" + this.file.getAbsolutePath());
				ImageView iv = new ImageView(im);
				iv.setPreserveRatio(true);
				if(im.getWidth()>1000){
					iv.setFitWidth(700);
				}
				borderPane.setCenter(iv);
				Scene scene = new Scene(borderPane, 1200, 1000);
				s.setScene(scene);
				s.show();*/
			}

		});
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
