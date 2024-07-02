package sunny.zzhao.zzhao.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sunny.zzhao.zzhao.pojo.AchievementClassItem;
import sunny.zzhao.zzhao.pojo.AchievementItemRoot;
import sunny.zzhao.zzhao.pojo.ResultResponse;
import sunny.zzhao.zzhao.service.AchievementService;

import java.util.List;

@RestController
public class AchievementController {
    @Resource
    AchievementService achievementService;

    @PostMapping("/getAchievementClassItems")
    public ResultResponse<List<AchievementClassItem>> getAchievementClassItems() {
        List<AchievementClassItem> achievementClassItems = achievementService.getAchievementClassItems();
        if (achievementClassItems != null) {
            return ResultResponse.success(achievementClassItems);
        } else {
            return ResultResponse.fail(500, "获取成就栏项目失败");
        }
    }

    @PutMapping("/uploadAchievementClassItemIcon")
    public ResultResponse<String> uploadAchievementClassItemIcon(String id, MultipartFile file) {
        String url = achievementService.uploadAchievementClassItemIcon(id, file);
        if (url != null) {
            return ResultResponse.success(url);
        } else {
            return ResultResponse.fail(500, "上传成就栏项目图标失败");
        }
    }

    @PutMapping("/putAchievementClassItem")
    public ResultResponse<Boolean> putAchievementClassItem(@RequestBody AchievementClassItem achievementClassItem) {
        if (achievementService.putAchievementClassItem(achievementClassItem)) {
            return ResultResponse.success(true);
        } else {
            return ResultResponse.fail(500, "上传成就栏项目失败");
        }
    }

    @GetMapping("/getAchievementItemRoot")
    public ResultResponse<List<AchievementItemRoot>> getAchievementItemRoot() {
        return null;
    }

    @PostMapping("/getAchievementItemRoot/{id}")
    public ResultResponse<AchievementItemRoot> getAchievementItemRootById(@PathVariable String id) {
        AchievementItemRoot achievementItemRoot = achievementService.getAchievementItemRootById(id);
        if (achievementItemRoot != null) {
            return ResultResponse.success(achievementItemRoot);
        } else {
            return ResultResponse.fail(500, "获取成就栏项目详情失败");
        }
    }
}
