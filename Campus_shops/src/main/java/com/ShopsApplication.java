package com;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync //开启异步任务
@EnableScheduling //开启基于注解的定时任务
@SpringBootApplication
@MapperScan({"com.mapper"})
public class ShopsApplication  extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ShopsApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ShopsApplication.class);
    }

}
