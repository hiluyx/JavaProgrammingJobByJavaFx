package model;

import java.io.File;
import java.util.ArrayList;

import controller.MenuPane;
import controller.SeePicture;
import controller.ViewerPane;
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;
import lombok.Data;


@Data
public class PictureNode extends Label {
    private File file;
    private Image image;
    private ImageView imageView;
    private Text pictureName;
    private ViewerPane viewerPane;

    private MenuPane menuPane = new MenuPane();

    private int count;

    protected static ArrayList<PictureNode> selectedPictures = new ArrayList<>();
    protected static ArrayList<File> selectedPictureFiles = new ArrayList<>();
//    private MenuPane menuPane = new MenuPane();

    public static ArrayList<File> getSelectedPictureFiles() {
        return selectedPictureFiles;
    }

    public static void setSelectedPictureFiles(
            ArrayList<File> selectedPictureFiles) {
        PictureNode.selectedPictureFiles = selectedPictureFiles;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public PictureNode(File aPicture) {

        this.file = aPicture;
        this.setPickOnBounds(true);
//        this.setContextMenu(menuPane.getContextMenu());
//		this.setOnMouseExited(e -> {
//			this.setStyle("-fx-background-color: White;");
//		});

        /**无法实现点击空白取消所有高亮，因为不是在这个this上处理的*/
        this.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                System.out.println("单击");
                this.count += e.getClickCount();
//			if (e.getSceneX() != 0 && e.getSceneY() != 0) {
                if (e.isControlDown()) {
                    if (this.count % 2 == 1) {
                        this.setStyle("-fx-background-color: #8bb9ff;");
                        selectedPictures.add(this);
                        selectedPictureFiles.add(this.file);
                        System.out.println("选中的数量：" + selectedPictures.size());
                    } else {
                        this.setStyle("-fx-background-color: transparent;");
                        selectedPictures.remove(this);
                        selectedPictureFiles.remove(this.file);
                        System.out.println("选中的数量：" + selectedPictures.size());
                    }
                } else {
                    for (PictureNode each : selectedPictures) {
                        if (each.getCount() % 2 == 1) {
                            each.setStyle("-fx-background-color: transparent;");
                            each.setCount(0);
                        }
                    }
                    selectedPictures.clear();
                    selectedPictureFiles.clear();
                    if (this.count % 2 == 1) {

                        this.setStyle("-fx-background-color: #8bb9ff;");
                        selectedPictures.add(this);
                        selectedPictureFiles.add(this.file);
                        System.out.println("选中的数量：" + selectedPictures.size());
                    } else {
                        this.setStyle("-fx-background-color: transparent;");
                        selectedPictures.remove(this);
                        selectedPictureFiles.remove(this.file);
                        System.out.println("选中的数量：" + selectedPictures.size());
                    }
                }
                if (this.getStyle() == "-fx-background-color: #8bb9ff;") {
                    this.setContextMenu(menuPane.getContextMenu());
                }
            }
            //}

            if (e.getClickCount() == 2) {
                new SeePicture(this.file, this.file.getName());

            }
        });

        this.setGraphicTextGap(10);
        this.setPadding(new Insets(10, 10, 10, 10));
        this.setContentDisplay(ContentDisplay.TOP);
        this.setPrefSize(110, 110);

        this.image = new Image("file:" + aPicture.getAbsolutePath(), 100, 100,
                true, true);
        this.imageView = new ImageView(image);
        this.pictureName = new Text(aPicture.getName());
        this.setText(pictureName.getText());
        this.setGraphic(imageView);
        this.setId("pictureNode");
    }

    public static void getSelectedPicturesFiles() {
    }


    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public Text getPictureName() {
        return pictureName;
    }

    public void setPictureName(Text pictureName) {
        this.pictureName = pictureName;
    }

    public ViewerPane getViewerPane() {
        return viewerPane;
    }

    public void setViewerPane(ViewerPane viewerPane) {
        this.viewerPane = viewerPane;
    }

    public int getCount() {
        return count;
    }


    public static ArrayList<PictureNode> getSelectedPictures() {
        return selectedPictures;
    }

    public static void setSelectedPictures(
            ArrayList<PictureNode> selectedPictures) {
        PictureNode.selectedPictures = selectedPictures;
    }

}
