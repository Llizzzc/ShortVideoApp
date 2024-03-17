package com.tiktok.configration;

import com.tiktok.interceptor.PassportInterceptor;
import com.tiktok.interceptor.UserTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import javax.annotation.Resource;

/**
 * 拦截器配置类.
 *
 * @author lzc
 * @version 1.0
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Resource
    private PassportInterceptor passportInterceptor;
    @Resource
    private UserTokenInterceptor userTokenInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器
        registry.addInterceptor(passportInterceptor)
                .addPathPatterns("/passport/getSMSCode");
        registry.addInterceptor(userTokenInterceptor)
                .addPathPatterns("/userInfo/*")
                .excludePathPatterns("/userInfo/query")
                .addPathPatterns("/vlog/*")
                .excludePathPatterns("/vlog/indexList")
                .excludePathPatterns("/vlog/detail")
                .excludePathPatterns("/vlog/myList")
                .excludePathPatterns("/vlog/totalLikedCounts")
                .addPathPatterns("/fans/*")
                .excludePathPatterns("/fans/queryMyFollows")
                .excludePathPatterns("/fans/queryMyFans")
                .excludePathPatterns("/fans/queryRelationship")
                .addPathPatterns("/comment/*")
                .excludePathPatterns("/comment/list")
                .excludePathPatterns("/comment/counts");
    }
}