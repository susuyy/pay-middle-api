package com.ht.user.config;

import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class HttpCodeMapConfig {

    public static Map<Integer,String> httpCodeMap = new HashMap<>();

    {
        httpCodeMap.put(400,"Bad Request");
        httpCodeMap.put(404,"Not Found");
        httpCodeMap.put(405,"Method Not Allowed");
        httpCodeMap.put(500,"Internal Server Error");
    }
}
