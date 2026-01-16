package com.ht.feignapi.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class DESConfig {

    /**
     * des秘钥
     */
    @Value("${desConfig.password}")
    public String password;

    /**
     * des偏移向量
     */
    @Value("${desConfig.ivParameter}")
    public String ivParameter;
}
