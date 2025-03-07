package com.roc.his.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan("com.roc.*")
@MapperScan("com.roc.his.api.db.dao")
@ServletComponentScan
@EnableAsync
public class HisApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(HisApiApplication.class, args);
    }
}
