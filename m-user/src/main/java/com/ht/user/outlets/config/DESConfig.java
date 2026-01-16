package com.ht.user.outlets.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class DESConfig {

    /**
     * des秘钥
     */
    public String password = "outlets-hlta-password-0211115";

    /**
     * des偏移向量
     */
    public String ivParameter = "9ot%58&s";
}
