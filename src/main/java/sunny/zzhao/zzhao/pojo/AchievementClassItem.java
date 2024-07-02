package sunny.zzhao.zzhao.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * 左侧 成就栏 的内容项
 */
@Setter
@Getter
public class AchievementClassItem {
    private String id;
    private String name;
    private String icon;
    private String description;

    public AchievementClassItem(String id, String name, String icon, String description) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.description = description;
    }

    public AchievementClassItem() {
    }
}
