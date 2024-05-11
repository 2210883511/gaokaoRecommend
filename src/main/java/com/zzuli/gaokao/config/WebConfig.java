package com.zzuli.gaokao.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    HandlerInterceptor adminLoginInterceptor;

    @Autowired
    HandlerInterceptor userLoginInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")  // 允许跨域访问的路径
                .allowedOriginPatterns("*")
                .allowedHeaders("*")    // 允许跨域访问的源
                .allowedMethods("POST", "GET", "DELETE", "PUT", "OPTIONS") // 允许请求方法
                .allowCredentials(true);    // 是否允许发送cookie
        WebMvcConfigurer.super.addCorsMappings(registry);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminLoginInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login")
                .excludePathPatterns("/admin/captcha");
//        registry.addInterceptor(userLoginInterceptor)
//                .addPathPatterns("/api/**")
//                .excludePathPatterns("/api/user/login")
//                .excludePathPatterns("/api/user/captcha");
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
