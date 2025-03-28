package com.roc.his.api;

import com.roc.his.api.async.InitializeWorkAsync;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

@SpringBootApplication
@ComponentScan("com.roc.*")
@MapperScan("com.roc.his.api.db.dao")
@ServletComponentScan
@EnableAsync
@EnableCaching
@EnableScheduling
@RequiredArgsConstructor
public class HisApiApplication {
    private final InitializeWorkAsync initializeWorkAsync;

    public static void main(String[] args) {
        SpringApplication.run(HisApiApplication.class, args);
    }

    @PostConstruct
    public void init() {
        initializeWorkAsync.init();
    }
}
