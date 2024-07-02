package sunny.zzhao.zzhao.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class AchievementItemRoot {
    private String id;
    private String label;
    private String icon;
    private String description;
    private List<AchievementItemChild> children;
}
