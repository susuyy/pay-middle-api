package com.ht.feignapi.result;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.feignapi.config.HttpCodeMapConfig;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Map;

import static com.alibaba.fastjson.serializer.SerializerFeature.WriteMapNullValue;

@RestControllerAdvice
public class GlobalControllerResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return !methodParameter.getParameterType().isAssignableFrom(Result.class);
    }

    @Override
    public Object beforeBodyWrite(Object data,
                                  MethodParameter returnType,
                                  MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> aClass,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        if (returnType.getGenericParameterType().equals(String.class)) {
            return new Result(ResultTypeEnum.SERVICE_SUCCESS, data);
        }
        //异常校验判断 详细状态码再次扩展
        if (data instanceof Map) {
            Map dataMap = (Map) data;
            if (dataMap.get("status") != null ) {
                int status =(int) dataMap.get("status");
                boolean containsKey = HttpCodeMapConfig.httpCodeMap.containsKey(status);
                if (containsKey){
                    if (status == 500){
                        return new Result(ResultTypeEnum.SERVICE_ERROR, data);
                    }
                    return new Result(status,HttpCodeMapConfig.httpCodeMap.get(status), data);
                }
            }
        }

        // 否则直接包装成Result返回
        return new Result(ResultTypeEnum.SERVICE_SUCCESS, data);
    }
}
