package com.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MyConfig extends WebMvcConfigurerAdapter {
    @Autowired
    private LoginHandlerInterceptor loginHandlerInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }
    @Bean
    public WebMvcConfigurerAdapter WebMvcConfigurerAdapter() {
        WebMvcConfigurerAdapter adapter = new WebMvcConfigurerAdapter() {
            /**添加拦截器*/
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(loginHandlerInterceptor).addPathPatterns("/admin/**")
                        .addPathPatterns("/user/center")
                        .addPathPatterns("/user/perfectinfo")
                        .excludePathPatterns("/admin").
                        excludePathPatterns("/admin/login");
            }

            /**静态资源处理*/
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                //图片资源直接放在file下，不需要再加pic文件夹了。但是数据库中img字段还是要加/pic/前缀。应该只是个虚拟拦截
                registry.addResourceHandler("/pic/**").addResourceLocations("file:C:/campusshops/file/");
                super.addResourceHandlers(registry);
            }
        };
        return adapter;
    }
}