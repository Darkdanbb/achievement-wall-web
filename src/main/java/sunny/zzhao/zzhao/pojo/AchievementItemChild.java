package sunny.zzhao.zzhao.pojo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AchievementItemChild {
    private String id;
    private String pid;
    private String label;
    private String icon;
    private String description;
    private AchievementItemChild[] children;
}
