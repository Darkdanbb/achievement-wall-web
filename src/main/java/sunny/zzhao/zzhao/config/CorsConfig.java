package sunny.zzhao.zzhao.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*") // 允许来自所有域名的请求
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许特定的 HTTP 方法
                .maxAge(3600); // 跨域请求的缓存时间
    }
}