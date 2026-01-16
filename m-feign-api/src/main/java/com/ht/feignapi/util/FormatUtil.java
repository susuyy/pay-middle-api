package com.ht.feignapi.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class FormatUtil {

    // For json
    private static ObjectMapper objectMapper = new ObjectMapper()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
//            .registerModule(new JodaModule());

    static public String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    /**
     * 适用于日志输出时使用
     */
    static public String toJsonNoException(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    static public <T> T fromJson(String json, Class<T> type) throws IOException {
        return objectMapper.readValue(json, type);
    }

    static public String getProperty(String json, String propName) {
        try {
            if (StringUtils.isEmpty(json)) {
                return "";
            }
            HashMap properties = objectMapper.readValue(json, HashMap.class);
            Object o = properties.get(propName);
            if (o == null) {
                return "";
            }
            return o.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}