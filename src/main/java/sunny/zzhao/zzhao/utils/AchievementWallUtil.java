package sunny.zzhao.zzhao.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.model.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import sunny.zzhao.zzhao.pojo.AchievementClassItem;
import sunny.zzhao.zzhao.pojo.AchievementItemChild;
import sunny.zzhao.zzhao.pojo.AchievementItemRoot;
import sunny.zzhao.zzhao.pojo.AliOSSConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class AchievementWallUtil {
    @Resource
    AliOSSConfig aliOSSConfig;

    private OSS getOssClient() {
        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
        EnvironmentVariableCredentialsProvider credentialsProvider;
        try {
            credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        } catch (com.aliyuncs.exceptions.ClientException e) {
            throw new RuntimeException(e);
        }
        // 创建OSSClient实例。
        return new OSSClientBuilder().build(aliOSSConfig.getEndpoint(), credentialsProvider);
    }

    public List<String> getAchievementClassItems() {
        OSS ossClient = getOssClient();
        List<String> achievementClassItemsFileNames = new ArrayList<>();
        try {
            // ossClient.listObjects返回ObjectListing实例，包含此次listObject请求的返回结果。
            ObjectListing objectListing = ossClient.listObjects(aliOSSConfig.getBucketName(), "AchievementClassItems");
            // objectListing.getObjectSummaries获取所有文件的描述信息。
            for (OSSObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                achievementClassItemsFileNames.add(objectSummary.getKey());
            }
        } catch (OSSException oe) {
            ossError(oe);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return achievementClassItemsFileNames;
    }


    /**
     * 下载文件并且解析成 JSONObject
     *
     * @param filenames 文件名列表 格式是 "AchievementClassItems/filename.json"
     * @return 解析后的JSONObject列表  交给service处理
     */
    public List<JSONObject> downloadAndParseAchievementClassItem(List<String> filenames) {
        List<JSONObject> jsonObjects = new ArrayList<>();
        for (String filename : filenames) {
            OSS ossClient = getOssClient();
            try {
                OSSObject object = ossClient.getObject(aliOSSConfig.getBucketName(), filename);
                BufferedReader reader = new BufferedReader(new InputStreamReader(object.getObjectContent()));
                StringBuilder sb = new StringBuilder();
                while (true) {
                    String line = reader.readLine();
                    if (line == null) break;
                    sb.append(line);
                }
                if (!sb.isEmpty()) {
                    jsonObjects.add(JSONObject.parseObject(sb.toString()));
                }
            } catch (OSSException oe) {
                ossError(oe);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (ossClient != null) {
                    ossClient.shutdown();
                }
            }
        }
        return jsonObjects;
    }

    public Boolean doesAchievementClassItemIconExist(String id) {
        OSS ossClient = getOssClient();
        boolean b;
        for (String fileSuffix : new String[]{"png", "jpg", "jpeg", "gif"}) {
            b = ossClient.doesObjectExist(aliOSSConfig.getBucketName(), "Images/AchievementClassItems/" + id + "." + fileSuffix);
            if (b) {
                ossClient.shutdown();
                return b;
            }
        }
        return false;
    }

    public void deleteAchievementClassItemIcon(String id) {
        OSS ossClient = getOssClient();
        for (String fileSuffix : new String[]{"png", "jpg", "jpeg", "gif"}) {
            ossClient.deleteObject(aliOSSConfig.getBucketName(), "Images/AchievementClassItems/" + id + "." + fileSuffix);
        }
        ossClient.shutdown();
    }

    public String uploadAchievementClassItemIcon(String id, MultipartFile file) throws IOException {
        OSS ossClient = getOssClient();
        String savePath = "Images/AchievementClassItems/" + id + "." + Objects.requireNonNull(file.getContentType()).split("/")[1];
        PutObjectRequest putObjectRequest = new PutObjectRequest(aliOSSConfig.getBucketName(), savePath, FileUtil.convert(file));
        ossClient.putObject(putObjectRequest);
        ossClient.shutdown();
        if (StringUtil.containsChineseCharacters(id)) {
            return "https://achievement-wall.oss-cn-guangzhou.aliyuncs.com/Images/AchievementClassItems/" + StringUtil.coverToURL(id) + "." + Objects.requireNonNull(file.getContentType()).split("/")[1];
        } else {
            return "https://achievement-wall.oss-cn-guangzhou.aliyuncs.com/Images/AchievementClassItems/" + id + "." + Objects.requireNonNull(file.getContentType()).split("/")[1];
        }
    }

    public Boolean putAchievementClassItem(AchievementClassItem achievementClassItem) {
        OSS ossClient = getOssClient();
        try {
            PutObjectResult result = ossClient.putObject(aliOSSConfig.getBucketName(), "AchievementClassItems/" + achievementClassItem.getId() + ".json", new ByteArrayInputStream(JSONObject.toJSONString(achievementClassItem).getBytes()));
            return result != null;
        } catch (OSSException oe) {
            ossError(oe);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return false;
    }

    /**
     * 获取成就项的根节点 包含所有成就项的id和名称 和 叶子节点的id和名称
     *
     * @param id 左侧成就栏类别项的id !!! 注意 左侧成就栏类别项的id跟ItemRoot的id是一致的
     * @return 返回AchievementItemRoot对象
     */
    public AchievementItemRoot getAchievementItemRootById(String id) throws IOException {
        AchievementItemRoot achievementItemRoot = new AchievementItemRoot();
        OSS ossClient = getOssClient();
        String fileName = "AchievementItems/" + id + "/" + id + ".json";
        OSSObject ossObject = ossClient.getObject(aliOSSConfig.getBucketName(), fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(ossObject.getObjectContent()));
        StringBuilder sb = new StringBuilder();
        while (true) {
            String line = reader.readLine();
            if (line == null) break;
            sb.append(line);
        }
        if (!sb.isEmpty()) {
            achievementItemRoot = JSON.parseObject(sb.toString(), AchievementItemRoot.class);
        }
        List<AchievementItemChild> itemRootChildren = new ArrayList<>();
        ObjectListing objectListing = ossClient.listObjects(aliOSSConfig.getBucketName(), "AchievementItems/" + id + "/childs");
        for (OSSObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            String childFileName = objectSummary.getKey();
            if (childFileName.endsWith(".json")) {
                ossObject = ossClient.getObject(aliOSSConfig.getBucketName(), childFileName);
                reader = new BufferedReader(new InputStreamReader(ossObject.getObjectContent()));
                sb = new StringBuilder();
                while (true) {
                    String line = reader.readLine();
                    if (line == null) break;
                    sb.append(line);
                }
                if (!sb.isEmpty()) {
                    itemRootChildren.add(JSON.parseObject(sb.toString(), AchievementItemChild.class));
                }
            }
        }
        achievementItemRoot.setChildren(itemRootChildren);
        return achievementItemRoot;
    }

    private void ossError(OSSException oe) {
        System.out.println("Caught an OSSException, which means your request made it to OSS, " + "but was rejected with an error response for some reason.");
        System.out.println("Error Message:" + oe.getErrorMessage());
        System.out.println("Error Code:" + oe.getErrorCode());
        System.out.println("Request ID:" + oe.getRequestId());
        System.out.println("Host ID:" + oe.getHostId());
    }
}
