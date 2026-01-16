package com.ht.user;

import io.github.yedaxia.apidocs.Docs;
import io.github.yedaxia.apidocs.DocsConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@MapperScan(basePackages = {"com.ht.user.*.mapper"})
//@EnableEurekaClient
@EnableFeignClients()
public class MUserApplication {

    public static void main(String[] args) {

        SpringApplication.run(MUserApplication.class, args);
    }

}
