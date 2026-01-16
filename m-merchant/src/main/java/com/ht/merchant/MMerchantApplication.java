package com.ht.merchant;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ht.merchant.mapper")
public class MMerchantApplication {

    public static void main(String[] args) {
        SpringApplication.run(MMerchantApplication.class, args);
    }

}
