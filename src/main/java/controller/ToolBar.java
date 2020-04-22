package controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import lombok.Getter;
import model.PictureNode;
import model.TreeNode;

public class ToolBar extends HBox {
    private Button copy = createButton("复制");
    private Button cut = createButton("剪切");
    private Button paste = createButton("粘贴");
    private Button delete = createButton("删除");
    @Getter
    private final Label path = new Label();

    //    private SimpleObjectProperty<TreeNode> selectedFolderProperty = new SimpleObjectProperty<>();
    public ToolBar(double spacing) {
        super(10);
        this.setPadding(new Insets(10, 10, 10, 10));

        this.getChildren().add(copy);
        this.getChildren().add(cut);
        this.getChildren().add(paste);
        this.getChildren().add(delete);
        this.getChildren().add(path);
//        selectedFolderProperty.addListener((observable, oldValue, newValue) -> {
//            if(newValue!=null&&newValue.getFile().getAbsolutePath()!=null)
//            path.setText(newValue.getFile().getAbsolutePath());
//        });
    }

    private Button createButton(String buttonName) {
        Button button = new Button();
        button.setPadding(new Insets(10, 10, 10, 10));
        button.setText(buttonName);
        return button;
    }

    public Button getCopy() {
        return copy;
    }

    public void setCopy(Button copy) {
        this.copy = copy;
    }

    public Button getCut() {
        return cut;
    }

    public void setCut(Button cut) {
        this.cut = cut;
    }

    public Button getPaste() {
        return paste;
    }

    public void setPaste(Button paste) {
        this.paste = paste;
    }

    public Button getDelete() {
        return delete;
    }

    public void setDelete(Button delete) {
        this.delete = delete;
    }


//    public void setSelectedFolder(TreeNode selectedFolder) {
//        this.selectedFolderProperty.set(selectedFolder);
//    }
}
