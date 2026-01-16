package com.ht.feignapi.config;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import sun.nio.cs.FastCharsetProvider;

import java.util.TimeZone;

/**
 * @Author: Liwg
 * @Date: 2020/9/7 14:36
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");

        registry.addResourceHandler("/swagger/**").addResourceLocations("classpath:/static/swagger/");

    }

//    @Bean
//    public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomization() {
//        return jacksonObjectMapperBuilder ->
//                jacksonObjectMapperBuilder.timeZone(TimeZone.getTimeZone("GMT+8"));
//    }

}
