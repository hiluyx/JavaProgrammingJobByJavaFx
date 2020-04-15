package controller;


import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import model.PictureNode;
import model.TreeNode;

public class ViewerPane extends BorderPane {
    private SimpleObjectProperty<TreeNode> selectedFolderProperty;

    private FlowPane flowPane;

    public ViewerPane() {
        this.selectedFolderProperty = new SimpleObjectProperty<TreeNode>();
        HBox hBox = new HBox(10);
        hBox.setPadding(new Insets(10, 10, 10, 10));
        Button button = new Button();
        button.setPadding(new Insets(10, 10, 10, 10));
        button.setText("复制");
        Button button2 = new Button();
        button2.setPadding(new Insets(10, 10, 10, 10));
        button2.setText("粘贴");
        hBox.getChildren().add(button);
        hBox.getChildren().add(button2);
        ScrollPane scrollPane = new ScrollPane();
        flowPane = new FlowPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(flowPane);
        this.setTop(hBox);
        this.setCenter(scrollPane);

        selectedFolderProperty.addListener(new ChangeListener<TreeNode>() {
            @Override
            public void changed(ObservableValue<? extends TreeNode> observable, TreeNode oldValue, TreeNode newValue) {
                flowPane.getChildren().remove(0, flowPane.getChildren().size());

                TreeNode p = newValue;
                if (p != null && p.getImages() != null) for (int i = 0; i < p.getImages().size(); i++) {
                    PictureNode iv = new PictureNode(p.getImages().get(i));
                    flowPane.getChildren().add(iv);
                }
                System.out.println("打印结束");
            }
        }); // end of selectedFolderProperty addListener
    }

    public TreeNode getSelectedFolder() {
        return selectedFolderProperty.get();
    }

    public void setSelectedFolder(TreeNode selectedFolder) {
        this.selectedFolderProperty.set(selectedFolder);
    }

}
