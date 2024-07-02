package sunny.zzhao.zzhao.service;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sunny.zzhao.zzhao.pojo.AchievementClassItem;
import sunny.zzhao.zzhao.pojo.AchievementItemRoot;
import sunny.zzhao.zzhao.utils.AchievementWallUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class AchievementService {
    @Resource
    AchievementWallUtil achievementWallUtil;

    /**
     * 获取所有左侧成就栏的项目列表并且解析成AchievementClassItem对象列表
     *
     * @return AchievementClassItem对象列表
     */
    public List<AchievementClassItem> getAchievementClassItems() {
        List<AchievementClassItem> achievementClassItems = new ArrayList<>();
        for (JSONObject object : achievementWallUtil.downloadAndParseAchievementClassItem(achievementWallUtil.getAchievementClassItems())) {
            achievementClassItems.add(JSONObject.parseObject(object.toJSONString(), AchievementClassItem.class));
        }
        return achievementClassItems;
    }

    /**
     * 上传成就栏项目的图标
     */
    public String uploadAchievementClassItemIcon(String id, MultipartFile file) {
        try {
            if (achievementWallUtil.doesAchievementClassItemIconExist(id)) {
                achievementWallUtil.deleteAchievementClassItemIcon(id);
            }
            return achievementWallUtil.uploadAchievementClassItemIcon(id, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Boolean putAchievementClassItem(AchievementClassItem achievementClassItem) {
        return achievementWallUtil.putAchievementClassItem(achievementClassItem);
    }

    public AchievementItemRoot getAchievementItemRootById(String id) {
        try {
            return achievementWallUtil.getAchievementItemRootById(id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
