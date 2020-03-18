package data;

import lombok.Data;

import java.util.List;
@Data
public class FileClick {
    //被右击的单件数据
    private List<Image> imageClickList;//多选
    private Image imageClick;//单选
}
