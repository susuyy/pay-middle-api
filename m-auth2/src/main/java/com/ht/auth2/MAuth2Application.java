package com.ht.auth2;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
@MapperScan(basePackages = {"com.ht.auth2.mapper"})
@EnableFeignClients
@EnableEurekaClient
public class MAuth2Application {

    public static void main(String[] args) {
        SpringApplication.run(MAuth2Application.class, args);
    }

    @Bean(name = "restTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }



}
