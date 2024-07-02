package sunny.zzhao.zzhao.pojo;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 存放 AliOSS 的配置
 * key 和 secret 存在了本机电脑环境变量中
 */
@Getter
@Component
public class AliOSSConfig {
    @Value("${ali-oss-config.endpoint}")
    private String endpoint;
    @Value("${ali-oss-config.bucketName}")
    private String bucketName;
}
